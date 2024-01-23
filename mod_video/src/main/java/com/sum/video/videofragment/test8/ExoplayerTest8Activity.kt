package com.sum.video.videofragment.test8

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.util.Util
import com.sum.common.constant.VIDEO_TEST8_EXOPLAYER_ACTIVITY
import com.sum.framework.base.BaseDataBindActivity
import com.sum.video.databinding.ExoplayerTest8ActivityBinding
import com.sum.video.videofragment.test4.exovideoplayer.VideoCache
import com.sum.video.videofragment.test7.URL_8_HD1
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Route(path = VIDEO_TEST8_EXOPLAYER_ACTIVITY)
class ExoplayerTest8Activity : BaseDataBindActivity<ExoplayerTest8ActivityBinding>() {
    private val userAgent by lazy {
        Util.getUserAgent(this, packageName)
    }
    private val exoPlayer by lazy {
        ExoPlayer.Builder(this)
            .build()
    }
    val url =
        "http://cdn3.toronto360.tv:8081/toronto360/hd/playlist.m3u8"
    private lateinit var mediaItem: MediaItem

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.fullscreen.setOnClickListener {
            fullOrNotScreen()
        }
        lifecycleScope.launch {
            //   prepareMediaSource2()
            prepareMediaSource()
        }
    }

    private fun prepareMediaSource2() {
        mediaItem = MediaItem.fromUri(url)


        val mediaSource =
            HlsMediaSource.Factory(

                CacheDataSource.Factory()
                    .setUpstreamDataSourceFactory(
                        DefaultHttpDataSource.Factory()
                            .setUserAgent(userAgent)
                    )
                    .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)


            ).createMediaSource(mediaItem) //m3u8

        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.playWhenReady = true
        exoPlayer.prepare()
        mBinding.PlayerView.player = exoPlayer
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.PlayerView.player?.stop()
        mBinding.PlayerView.player?.release()
        mBinding.PlayerView.player = null
    }

    private fun fullOrNotScreen() {
        if (this.resources
                .configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        ) {//横屏
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;

        } else if (this.resources
                .configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        ) {//竖屏
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

        }
    }

    private suspend fun prepareMediaSource() {
        withContext(Dispatchers.Main) {
            mediaItem = MediaItem.fromUri(url)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.playWhenReady = true
            exoPlayer.prepare()
            mBinding.PlayerView.player = exoPlayer
        }
    }


}