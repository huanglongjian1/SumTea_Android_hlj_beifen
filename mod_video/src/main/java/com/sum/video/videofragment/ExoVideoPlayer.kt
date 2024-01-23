package com.sum.video.videofragment

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.MediaController
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.sum.common.util.Loge
import com.sum.video.R
import java.io.File

class ExoVideoPlayer(context: Context) : FrameLayout(context), VideoPlayer {
    private var playerView: StyledPlayerView? = null
    private val skipStates = listOf(Player.STATE_BUFFERING, Player.STATE_ENDED)
    private var resumePosition: Long = 0L
    private val exoListener: Player.Listener by lazy {
        object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_ENDED -> listener?.onStateChange(State.End)
                    Player.STATE_BUFFERING -> listener?.onStateChange(State.Buffering)
                    Player.STATE_IDLE -> resumePosition = _player.currentPosition
                    Player.STATE_READY -> listener?.onStateChange(State.Ready)
                }
            }

            override fun onRenderedFirstFrame() {
                listener?.onStateChange(State.FirstFrameRendered)
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    listener?.onStateChange(State.Playing)
                } else {
                    if (_player.playbackState !in skipStates && _player.playerError != null) {
                        listener?.onStateChange(State.Stop)
                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                listener?.onStateChange(State.Error(error))
            }
        }
    }

    private var _player = ExoPlayer.Builder(
        context,
        DefaultRenderersFactory(context).apply { setEnableDecoderFallback(true) })
        .build().also { player -> player.addListener(exoListener) }
    val player = _player

    override var listener: IVideoStateListener? = null
    private var cache: Cache? = null
    private var mediaItem: MediaItem? = null
    fun setVideoStateListener(iVideoStateListener: IVideoStateListener) {
        listener = iVideoStateListener
    }

    private fun buildMediaSource(context: Context): MediaSource {
        if (mediaItem == null) mediaItem = MediaItem.fromUri(url.toString())
        val cacheFile =
            context.cacheDir.resolve("CACHE_FOLDER_NAME" + File.separator + System.currentTimeMillis())
        if (!cacheFile.exists()) {
            Loge.e("mk:" + cacheFile.toString())
            cacheFile.mkdirs()
        } else {
            Loge.e(cacheFile.toString())
        }
        cache = SimpleCache(
            cacheFile,
            LeastRecentlyUsedCacheEvictor(1024 * 1024 * 200),
            StandaloneDatabaseProvider(context)
        )
        return run {
            val cacheDataSourceFactory = cache?.let {
                CacheDataSource.Factory().setCache(it)
                    .setUpstreamDataSourceFactory(DefaultDataSource.Factory(context))
                    .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
            }
            if (url.toString().endsWith("m3u8")) {
                HlsMediaSource.Factory(cacheDataSourceFactory!!)
                    .createMediaSource(mediaItem!!) //m3u8
            } else {
                ProgressiveMediaSource.Factory(cacheDataSourceFactory!!)
                    .createMediaSource(mediaItem!!)
            }
        }
    }

    init {
        playerView =
            LayoutInflater.from(context).inflate(R.layout.playerview, null) as StyledPlayerView
        this.addView(playerView)
        playerView?.player = _player
    }

    override var url: String? = null
        get() = field
        set(value) {
            field = value
            mediaItem = MediaItem.fromUri(value.toString())
        }

    //override var playControl: MediaController.MediaPlayerControl =_player

    override fun play() {
        if (_player.isPlaying) return
        if (_player.playbackState == Player.STATE_ENDED) {
            _player.seekTo(0)
        }
        _player.playWhenReady = true
    }

    override fun load() {
        _player.takeIf { !it.isLoading }?.apply {
            setMediaSource(buildMediaSource(context))
            prepare()
        }
    }

    override fun stop() {
        _player.stop()
    }

    override fun release() {
        _player.release()
    }
}

interface VideoPlayer {
    // 视频url
    var url: String?

    // 视频控制器，用于上层绘制进度条
    //var playControl: MediaController.MediaPlayerControl

    // 视频状态回调
    var listener: IVideoStateListener?

    // 播放视频
    fun play()

    // 加载视频
    fun load()

    // 停止视频
    fun stop()

    // 释放资源
    fun release()
}

interface IVideoStateListener {
    fun onStateChange(state: State)
}

//视频状态
sealed interface State {
    //第一帧被渲染
    object FirstFrameRendered : State

    //缓冲结束，随时可播放。
    object Ready : State

    //播放出错
    class Error(val exception: Exception) : State

    //播放中
    object Playing : State

    //播放手动停止
    object Stop : State

    //播放结束
    object End : State

    //缓冲中
    object Buffering : State
}