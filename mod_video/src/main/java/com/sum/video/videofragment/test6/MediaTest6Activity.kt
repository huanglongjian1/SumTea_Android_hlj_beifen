package com.sum.video.videofragment.test6

import android.content.ComponentName
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.SeekBar
import androidx.annotation.NonNull
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.sum.common.constant.VIDEO_TEST6_MEDIA_ACTIVITY
import com.sum.common.constant.VIDEO_TEST6_MEDIA_ACTIVITY_2
import com.sum.common.util.Loge
import com.sum.framework.base.BaseDataBindActivity
import com.sum.video.databinding.Mediatest6ActivityBinding

@Route(path = VIDEO_TEST6_MEDIA_ACTIVITY)
class MediaTest6Activity : BaseDataBindActivity<Mediatest6ActivityBinding>() {
    lateinit var mBrowser: MediaBrowserCompat
    val _url =
        "http://wxsnsdy.tc.qq.com/105/20210/snsdyvideodownload?filekey=30280201010421301f0201690402534804102ca905ce620b1241b726bc41dcff44e00204012882540400&bizid=1023&hy=SH&fileparam=302c020101042530230204136ffd93020457e3c4ff02024ef202031e8d7f02030f42400204045a320a0201000400"
    val mController by lazy {
        MediaControllerCompat(this, mBrowser.sessionToken)
    }

    val BrowserConnectionCallback by lazy {
        object : MediaBrowserCompat.ConnectionCallback() {
            override fun onConnected() {
                if (mBrowser.isConnected) {
                    val mediaId = mBrowser.getRoot();
                    Loge.e("mediaId:" + mediaId)
                    mBrowser.unsubscribe(mediaId);
                    mBrowser.subscribe(mediaId, BrowserSubscriptionCallback);
                    mController.registerCallback(ControllerCallback)
                }
            }

            override fun onConnectionFailed() {
                Loge.e("链接失败")
            }

        }
    }

    val ControllerCallback: MediaControllerCompat.Callback =
        object : MediaControllerCompat.Callback() {
            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                super.onPlaybackStateChanged(state)
                Loge.e(state.toString())
            }

            override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                super.onMetadataChanged(metadata)
                if (metadata == null) return
                var title = ""
                if (metadata != null && metadata.containsKey(
                        MediaMetadataCompat.METADATA_KEY_TITLE
                    )
                ) {
                    title = metadata.description.title.toString()
                }

                if (metadata != null && metadata.containsKey(
                        MediaMetadataCompat.METADATA_KEY_DURATION
                    )
                ) {
                    val duration = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
                    mBinding.seekBar.max = duration.toInt()
                    Loge.e("duration:" + duration)
                }
                if (metadata != null && metadata.containsKey(
                        "current_duration"
                    )
                ) {
                    val current_duration = metadata.getLong("current_duration")
                    mBinding.seekBar.progress = current_duration.toInt()
                }
                mBinding.position.text =
                    title + "\n" + (mBinding.seekBar.progress / 1000 / 60).toString() + ":" + (mBinding.seekBar.progress / 1000 % 60) + "/" + (mBinding.seekBar.max / 1000 / 60) + ":" + (mBinding.seekBar.max / 1000 % 60)
            }
        }

    val BrowserSubscriptionCallback by lazy {
        object : MediaBrowserCompat.SubscriptionCallback() {
            override fun onChildrenLoaded(
                @NonNull parentId: String,
                @NonNull children: List<MediaBrowserCompat.MediaItem>
            ) {
                Loge.e(children.toString())
            }


        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBrowser = MediaBrowserCompat(
            this, ComponentName(this, MusicService::class.java),
            BrowserConnectionCallback,
            null
        )

        mBinding.play.setOnClickListener {
            mController.transportControls.play()
        }
        mBinding.stop.setOnClickListener {
            mController.transportControls.pause()
        }
        mBinding.uri.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("title", "明年今日" + index++)
            mController.transportControls.playFromUri(Uri.parse(_url), bundle)

        }
        mBinding.position.setOnClickListener {

        }
        mBinding.goto2.setOnClickListener {
            ARouter.getInstance().build(VIDEO_TEST6_MEDIA_ACTIVITY_2).navigation()
        }
        mBinding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                Loge.e(p1.toString())
                if (p2) {
                    mController.transportControls.seekTo(p1.toLong())
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
    }

    var index = 0
    override fun onStart() {
        super.onStart()
        mBrowser.connect()
    }

    override fun onStop() {
        super.onStop()
        mController.transportControls.pause()
        mBrowser.disconnect()
    }

    override fun initView(savedInstanceState: Bundle?) {


    }

    override fun onDestroy() {
        super.onDestroy()

    }
}