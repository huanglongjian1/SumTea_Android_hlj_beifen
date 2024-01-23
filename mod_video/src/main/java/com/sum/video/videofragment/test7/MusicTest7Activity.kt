package com.sum.video.videofragment.test7

import android.Manifest
import android.content.ComponentName
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.common.util.concurrent.MoreExecutors
import com.permissionx.guolindev.PermissionX
import com.sum.common.constant.VIDEO_TEST7_MUSIC_ACTIVITY
import com.sum.common.util.Loge
import com.sum.framework.base.BaseDataBindActivity
import com.sum.video.databinding.Musictest7ActivityBinding
import com.sum.video.listener.OnViewPagerListener
import com.sum.video.manager.PagerLayoutManager
import com.sum.video.videofragment.test7.adapter.MusicData
import com.sum.video.videofragment.test7.adapter.MusicTest7Adapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

@Route(path = VIDEO_TEST7_MUSIC_ACTIVITY)
class MusicTest7Activity : BaseDataBindActivity<Musictest7ActivityBinding>() {
    lateinit var player: Player
    val url =
        "http://wxsnsdy.tc.qq.com/105/20210/snsdyvideodownload?filekey=30280201010421301f0201690402534804102ca905ce620b1241b726bc41dcff44e00204012882540400&bizid=1023&hy=SH&fileparam=302c020101042530230204136ffd93020457e3c4ff02024ef202031e8d7f02030f42400204045a320a0201000400"
    val imageUrl = "https://ask.qcloudimg.com/http-save/yehe-3985899/rlyugy6saa.gif"


    override fun initView(savedInstanceState: Bundle?) {
        PermissionX.init(this).permissions(
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.READ_MEDIA_AUDIO
        ).request { allGranted, grantedList, deniedList ->
            if (allGranted) {
                Loge.e("已经获取到权限")
            } else {
                Loge.e("请用户授予权限")
            }
        }

        CacheController.init(this)
        lifecycleScope.async(Dispatchers.IO) {
            CacheController.cacheMedia(arrayListOf(URL_1, URL_2, URL_3, URL_4, URL_5))
        }
        mBinding.musicTest7Tv.setOnClickListener {
            val musicTest7DialogFragment = MusicTest7DialogFragment()
            musicTest7DialogFragment.show(supportFragmentManager, "MusicTest7Activity")
        }


        val sessionToken =
            SessionToken(applicationContext, ComponentName(this, MusicPlayerService::class.java))
        val mediaControllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        mediaControllerFuture.addListener({
            player = mediaControllerFuture.get()
            addMediaItem(Uri.parse(url))

            val musicTest7Adapter = MusicTest7Adapter()
            for (i in 0..5) {
                val musicData = MusicData(url, imageUrl)
                musicTest7Adapter.addItem(musicData)
                addMediaItem(Uri.parse(url))
            }
            mBinding.test7RecyclerView.apply {
                adapter = musicTest7Adapter
                val pagerLayoutManager =
                    PagerLayoutManager(this@MusicTest7Activity, LinearLayoutManager.VERTICAL)
                pagerLayoutManager.setOnViewPagerListener(onViewPagerListener)
                layoutManager = pagerLayoutManager
            }

            player.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    when (playbackState) {
                        Player.STATE_ENDED -> {
                            Loge.e("end")
                            mBinding.test7RecyclerView.smoothScrollToPosition(indexLast++)
                        }

                        else -> {
                            Loge.e("playbackState")
                        }
                    }
                }
            })

            mBinding.test7PlayerView.player = player
        }, MoreExecutors.directExecutor())

    }

    fun addMediaItem(uri: Uri) {
        val newItem = MediaItem.Builder()
            .setMediaId("$uri")
            .build()
        player.addMediaItem(newItem)
    }

    var indexLast = 0;
    private val onViewPagerListener = object : OnViewPagerListener {
        override fun onInitComplete(view: View?) {
            player.seekTo(0, 0)
            player.prepare()

        }

        override fun onPageRelease(isNext: Boolean, position: Int, view: View?) {

        }

        override fun onPageSelected(position: Int, isBottom: Boolean, view: View?) {
            if (indexLast != position) {
                player.seekTo(position, 0)
                player.prepare()
                indexLast = position
            }
        }

    }
}