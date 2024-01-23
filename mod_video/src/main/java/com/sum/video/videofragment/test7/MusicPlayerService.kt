package com.sum.video.videofragment.test7

import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerNotificationManager
import com.danikula.videocache.HttpProxyCacheServer
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import java.io.File

class MusicPlayerService : MediaLibraryService() {


    lateinit var player: Player
    lateinit var session: MediaLibrarySession
    private val PLAYBACK_CHANNEL_ID = "playback_channel"
    private val PLAYBACK_NOTIFICATION_ID = 1
    private lateinit var playerNotificationManager: PlayerNotificationManager

    override fun onCreate() {
        super.onCreate()


        // 设置缓存目录和缓存机制，如果不需要清除缓存可以使用NoOpCacheEvictor
//        val cache = SimpleCache(
//            File(cacheDir, "example_media_cache_service"),
//            LeastRecentlyUsedCacheEvictor(100 * 1024 * 1024),
//            StandaloneDatabaseProvider(this)
//        )
        val cache = VideoCache2.getSimpleCache(
            applicationContext,
            File(cacheDir, "example_media_cache_service")
        )
        // 根据缓存目录创建缓存数据源
        val cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(cache)
            // 设置上游数据源，缓存未命中时通过此获取数据
            .setUpstreamDataSourceFactory(
                DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true)
            )

        player = ExoPlayer.Builder(applicationContext)
            .setAudioAttributes(AudioAttributes.DEFAULT, true) // 自动处理音频焦点
            .setHandleAudioBecomingNoisy(true) // 自动暂停播放
            .setMediaSourceFactory(ProgressiveMediaSource.Factory(cacheDataSourceFactory))//自动缓存
            .setRenderersFactory(
                DefaultRenderersFactory(this).setExtensionRendererMode(
                    DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER /* We prefer extensions, such as FFmpeg */
                )
            )
            .build()
        player.repeatMode = Player.REPEAT_MODE_OFF
        player.playWhenReady = true

        session = MediaLibrarySession.Builder(this, player,
            object : MediaLibrarySession.Callback {
                override fun onAddMediaItems(
                    mediaSession: MediaSession,
                    controller: MediaSession.ControllerInfo,
                    mediaItems: MutableList<MediaItem>
                ): ListenableFuture<MutableList<MediaItem>> {
                    val updatedMediaItems =
                        mediaItems.map { it.buildUpon().setUri(it.mediaId).build() }.toMutableList()
                    return Futures.immediateFuture(updatedMediaItems)
                }
            }).build()

        initNotification()
    }

    private fun initNotification() {
        playerNotificationManager = PlayerNotificationManager.Builder(
            applicationContext,
            PLAYBACK_NOTIFICATION_ID, PLAYBACK_CHANNEL_ID
        )
            .build()
        // 这里可以对通知栏进行更多设置
        playerNotificationManager.setPlayer(player)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return session
    }

    override fun onDestroy() {
        session.release()
        playerNotificationManager.setPlayer(null)
        player.release()
        super.onDestroy()
    }
}