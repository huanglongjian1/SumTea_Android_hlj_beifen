package com.sum.video.videofragment.test6

import android.content.ComponentName
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.NonNull
import com.alibaba.android.arouter.facade.annotation.Route
import com.sum.common.constant.VIDEO_TEST6_MEDIA_ACTIVITY_2
import com.sum.common.util.Loge
import com.sum.framework.base.BaseDataBindActivity
import com.sum.video.databinding.VideoFragment2Binding

@Route(path = VIDEO_TEST6_MEDIA_ACTIVITY_2)
class MediaTest6Activity_2 : BaseDataBindActivity<VideoFragment2Binding>() {
    private lateinit var mBrowser: MediaBrowserCompat
    override fun initView(savedInstanceState: Bundle?) {

    }

    val BrowserConnectionCallback by lazy {
        object : MediaBrowserCompat.ConnectionCallback() {
            override fun onConnected() {
                if (mBrowser.isConnected) {
                    val mediaId = mBrowser.getRoot();
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
                when (state?.state) {
                    PlaybackStateCompat.STATE_NONE -> {

                    }

                    PlaybackStateCompat.STATE_PLAYING -> {

                    }

                    PlaybackStateCompat.STATE_PAUSED -> {

                    }
                }

                Loge.e("MediaTest6Activity_2:" + state.toString())
            }

            override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                super.onMetadataChanged(metadata)
                Loge.e(metadata?.description?.title.toString())
            }
        }
    val mController by lazy {
        MediaControllerCompat(this, mBrowser.sessionToken)
    }
    val BrowserSubscriptionCallback by lazy {
        object : MediaBrowserCompat.SubscriptionCallback() {
            override fun onChildrenLoaded(
                @NonNull parentId: String,
                @NonNull children: List<MediaBrowserCompat.MediaItem>
            ) {
                Loge.e("MediaTest6Activity_2:" + children.toString())
                children.forEach {
                   if (it.description.title?.startsWith("http") == true||it.description.title?.startsWith("file")==true){
                       mBinding.videoView.setVideoPath(it.description.title.toString())
                       mBinding.videoView.start()
                   }
                }
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
    }

    override fun onStart() {
        super.onStart()
        mBrowser.connect()
    }

    override fun onStop() {
        super.onStop()
        mBrowser.disconnect()
        mBinding.videoView.stopPlayback()
        mBinding.videoView.suspend()
    }
}