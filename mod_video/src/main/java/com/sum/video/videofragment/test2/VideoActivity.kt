package com.sum.video.videofragment.test2

import android.media.MediaPlayer
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.danikula.videocache.HttpProxyCacheServer
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import com.sum.common.constant.VIDEO_TEST2_ACTIVITY
import com.sum.common.util.Loge
import com.sum.framework.base.BaseDataBindActivity
import com.sum.video.databinding.VideoActivityBinding
import com.sum.video.videofragment.MediaPlayerManager

@Route(path = VIDEO_TEST2_ACTIVITY)
class VideoActivity : BaseDataBindActivity<VideoActivityBinding>() {
    val url =
        "http://wxsnsdy.tc.qq.com/105/20210/snsdyvideodownload?filekey=30280201010421301f0201690402534804102ca905ce620b1241b726bc41dcff44e00204012882540400&bizid=1023&hy=SH&fileparam=302c020101042530230204136ffd93020457e3c4ff02024ef202031e8d7f02030f42400204045a320a0201000400"
    val url2 =
        "https://user-images.githubusercontent.com/20841967/233770382-1f9fee7f-d418-4a27-9785-98c36eca143f.mp4"
    val mediaPlayer by lazy {
        MediaPlayer()
    }


    private var bufferPercentage = 0
    private val mPlayer by lazy {
//1\. 自定义 DefaultLoadControl 参数
        val MIN_BUFFER_MS = 5_000 // 最小缓冲时间，
        val MAX_BUFFER_MS = 7_000 // 最大缓冲时间
        val PLAYBACK_BUFFER_MS = 700 // 最小播放缓冲时间，只有缓冲到达这个时间后才是可播放状态
        val REBUFFER_MS = 1_000 // 当缓冲用完，再次缓冲的时间
        val loadControl = DefaultLoadControl.Builder()
            .setPrioritizeTimeOverSizeThresholds(true)//缓冲时时间优先级高于大小
            .setBufferDurationsMs(MIN_BUFFER_MS, MAX_BUFFER_MS, PLAYBACK_BUFFER_MS, REBUFFER_MS)
            .build()

        ExoPlayer.Builder(this)
            .setLoadControl(loadControl)
            .build()
    }


    val cacheFile by lazy {
        this.cacheDir.resolve("cache_url")
    }
    //2\. 构建缓存实例
    val cache by lazy {
        SimpleCache(
            cacheFile,
            LeastRecentlyUsedCacheEvictor(1024 * 1024 * 100),
            StandaloneDatabaseProvider(this)
        )
    }

    private fun urlCache(url: String): MediaSource {
        val mediaItem = MediaItem.fromUri(url)
//1\. 构建缓存文件
//3\. 构建 DataSourceFactory
        val dataSourceFactory =
            CacheDataSource.Factory().setCache(cache).setUpstreamDataSourceFactory(
                DefaultDataSource.Factory(this)
            )
//4\. 构建 MediaSource
        val mediaSource =
            ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
//5\. 设置给播放器
        //  player.setMediaSource(mediaSource)
        return mediaSource
    }

    private val httpProxyCacheServer by lazy {
        HttpProxyCacheServer.Builder(this)
            .cacheDirectory(getExternalFilesDir("mPlayer2"))
            .maxCacheSize(1024 * 1024 * 100)
            .build()
    }

    override fun initView(savedInstanceState: Bundle?) {
        //   val urlCache = httpProxyCacheServer.getProxyUrl(url)
        //   mPlayer.setMediaItem(MediaItem.fromUri(urlCache))

        mPlayer.setMediaSource(urlCache(url))
        mBinding.StyledPlayerView.player = mPlayer
        mPlayer.playWhenReady = true
        mPlayer.prepare()

        mBinding.StyledPlayerView.controllerHideOnTouch = false

        mBinding.videoTv.setOnClickListener {
            val urlCache2 = httpProxyCacheServer.getProxyUrl(url2)
            mPlayer.setMediaItem(MediaItem.fromUri(urlCache2))
        }


//2\. 设置给播放器
        mPlayer.addListener(listener)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPlayer.stop()
        mPlayer.release()
        cache.release()
    }

    //1\. 构建监听器
    val listener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_ENDED -> {
                    Loge.e(" 播放结束")
                }


                Player.STATE_BUFFERING -> {
                    Loge.e("正在缓冲")
                }

                Player.STATE_IDLE -> {
                    Loge.e(" 空闲状态")
                }

                Player.STATE_READY -> {
                    Loge.e("可以被播放状态")
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            Loge.e("播放出错")
        }

        override fun onRenderedFirstFrame() {
            Loge.e("第一帧已渲染")
        }
    }
}