package com.sum.user.dialog

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import com.sum.common.util.Loge
import com.sum.framework.ext.onClick
import com.sum.framework.toast.TipsToast
import com.tbruyelle.rxpermissions3.RxPermissions

class ChoosePhotoDialogHlj {
    class BuilderHlj(activity: FragmentActivity) : ChoosePhotoDialog.Builder(activity) {
        private var mOnTakePicturesCallUri: ((Uri) -> Unit)? = null
        private var mOnPhotoAlbumCallUri: ((Uri) -> Unit)? = null
        fun setTakePicturesCallUri(onTakePicturesCallUri: ((Uri) -> Unit)): BuilderHlj {
            setTakePicturesCall {
                takePictures()
            }
            mOnTakePicturesCallUri = onTakePicturesCallUri
            return this
        }

        fun setPhotoAlbumCallUri(onPhotoAlbumCallUri: ((Uri) -> Unit)): BuilderHlj {
            setPhotoAlbumCall {
                openAlbum()
            }
            mOnPhotoAlbumCallUri = onPhotoAlbumCallUri
            return this
        }


        init {
            RxPermissions(activity).request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ).subscribe { granted ->
                if (granted) {
                    Loge.e("已经取到权限")
                } else {
                    TipsToast.showTips(com.sum.common.R.string.default_agree_permission)
                    dismiss()
                }
            }

        }

        fun takePictures() {
            val values = ContentValues()
            //根据uri查询图片地址
            val uri = activity.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            )
            activity.activityResultRegistry.register(
                "takePictures",
                ActivityResultContracts.TakePicture()
            ) {
                if (it) {
                    uri?.let { it1 -> mOnTakePicturesCallUri?.invoke(it1) }
                } else {
                    Loge.e("用户取消了拍照")
                }
            }.launch(uri)
        }

        fun openAlbum() {
            val intentAlbum = Intent(Intent.ACTION_PICK, null)
            intentAlbum.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            activity.activityResultRegistry.register(
                "打开相册",
                ActivityResultContracts.StartActivityForResult()
            ) {
                if (it.resultCode == Activity.RESULT_OK) {
                    it.data?.data?.let {uri->
                        mOnPhotoAlbumCallUri?.invoke(uri)
                    }
                } else {
                    Loge.e("用户没有选择照片")
                }
            }.launch(intentAlbum)
        }

    }

}