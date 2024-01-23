package com.sum.video.videofragment.test4.exovideoplayer

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
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
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.sum.video.R
import java.io.File
import java.lang.Math.abs

class ExoVideoPlayer(context: Context) : FrameLayout(context), VideoPlayer {
    private var playerView: StyledPlayerView? = null
    private val skipStates = listOf(Player.STATE_BUFFERING, Player.STATE_ENDED)
    private val exoListener: Player.Listener by lazy {
        object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_ENDED -> listener?.onStateChange(State.End)
                    Player.STATE_BUFFERING -> listener?.onStateChange(State.Buffering)
                    Player.STATE_IDLE -> listener?.onStateChange(State.Idle(_player.currentPosition))
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
    override var listener: IVideoStateListener? = null
    private var mediaItem: MediaItem? = null

    private fun buildMediaSource(context: Context): MediaSource {
        if (mediaItem == null) mediaItem = MediaItem.fromUri(url.toString())

        return run {
            val cacheDataSourceFactory =
                CacheDataSource.Factory().setCache(VideoCache.getSimpleCache(context, mediaItem!!))
                    .setUpstreamDataSourceFactory(DefaultDataSource.Factory(context))
                    .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
            if (url.endsWith("m3u8")) {
                HlsMediaSource.Factory(cacheDataSourceFactory).createMediaSource(mediaItem!!) //m3u8
            } else {
                ProgressiveMediaSource.Factory(cacheDataSourceFactory)
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

    override var url: String = ""
        get() = field
        set(value) {
            field = value
            mediaItem = MediaItem.fromUri(value.toString())
        }

//    override var playControl: MediaController.MediaPlayerControl = PlayerControl(_player)
//
//    private fun PlayerControl(_player: ExoPlayer): MediaController.MediaPlayerControl {
//
//    }

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


//            val urlCache = VideoCacheManager.getVideoCacheManager(context)
//                .setFeed("httpcache").proxy.getProxyUrl(url)
//            setMediaItem(MediaItem.fromUri(urlCache))   //使用不同的缓存策略

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

object VideoCache {
    @Volatile
    private var cache: SimpleCache? = null
    fun getSimpleCache(context: Context, mediaItem: MediaItem): SimpleCache {
        val cacheFile =
            context.cacheDir.resolve("CACHE_TEST5" + File.separator + abs(mediaItem.hashCode()))
        return cache
            ?: synchronized(this) {
                SimpleCache(
                    cacheFile,
                    LeastRecentlyUsedCacheEvictor(200 * 1024 * 1024),
                    StandaloneDatabaseProvider(context)
                ).also { cache = it }
            }
    }


}