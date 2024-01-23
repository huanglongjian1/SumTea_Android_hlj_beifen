package com.sum.video.videofragment.test7

import android.content.Context
import android.os.Environment
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.CacheWriter
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.google.android.exoplayer2.MediaItem
import com.sum.common.util.Loge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

class CacheController(context: Context) {

    private val cache: Cache
    private val cacheDataSourceFactory: CacheDataSource.Factory
    private val cacheDataSource: CacheDataSource

    private val cacheTask: ConcurrentHashMap<String, CacheWriter> = ConcurrentHashMap()

    init {
        val cacheParentDirectory =
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
                File(
                    context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                    context.packageName
                )
            } else {
                File(context.filesDir, context.packageName)
            }

        // 设置缓存目录和缓存机制，如果不需要清除缓存可以使用NoOpCacheEvictor
//        cache = SimpleCache(
//            File(cacheParentDirectory, "example_media_cache5"),
//            LeastRecentlyUsedCacheEvictor(100 * 1024 * 1024),
//            StandaloneDatabaseProvider(context)
//        )
        cache = VideoCache2.getSimpleCache(context, File(cacheParentDirectory, "cacheController"))
        // 根据缓存目录创建缓存数据源
        cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(cache)
            // 设置上游数据源，缓存未命中时通过此获取数据
            .setUpstreamDataSourceFactory(
                DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true)
            )
        cacheDataSource = cacheDataSourceFactory.createDataSource()
    }

    companion object {

        @Volatile
        private var cacheController: CacheController? = null

        fun init(context: Context) {
            if (cacheController == null) {
                synchronized(CacheController::class.java) {
                    if (cacheController == null) {
                        cacheController = CacheController(context)
                    }
                }
            }
        }

        fun cacheMedia(mediaSources: ArrayList<String>) {
            cacheController?.run {
                mediaSources.forEachIndexed { index, mediaUrl ->

                    // 创建CacheWriter缓存数据
                    Loge.e("index, mediaUrl:" + index)
                    CacheWriter(
                        cacheDataSource,
                        DataSpec.Builder()
                            // 设置资源链接
                            .setUri(mediaUrl)
                            // 设置需要缓存的大小（可以只缓存一部分）
                            .setLength((getMediaResourceSize(mediaUrl) * 0.1 * (index + 1)).toLong())//越靠前，缓存的比例越多,以此递减
                            .build(),
                        null
                    ) { requestLength, bytesCached, newBytesCached ->
                        // 缓冲进度变化时回调
                        // requestLength 请求总大小
                        // bytesCached 已缓冲的字节数
                        // newBytesCached 新缓冲的字节数
                        Loge.e(requestLength.toString() + ":" + bytesCached + ":" + newBytesCached)
                    }.let { cacheWriter ->
                        cacheWriter.cache()
                        cacheTask[mediaUrl] = cacheWriter
                    }

                }
            }
        }

        fun cancelCache(mediaUrl: String) {
            // 取消缓存
            cacheController?.cacheTask?.get(mediaUrl)?.cancel()
        }

        fun getMediaSourceFactory(): MediaSource.Factory? {
            var mediaSourceFactory: MediaSource.Factory? = null
            cacheController?.run {
                // 创建逐步加载数据的数据源
                mediaSourceFactory = ProgressiveMediaSource.Factory(cacheDataSourceFactory)
            }
            return mediaSourceFactory
        }

        fun release() {
            cacheController?.cacheTask?.values?.forEach { it.cancel() }
            cacheController?.cache?.release()
        }
    }

    // 获取媒体资源的大小
    private fun getMediaResourceSize(mediaUrl: String): Long {
        try {
            val connection = URL(mediaUrl).openConnection() as HttpURLConnection
            // 请求方法设置为HEAD，只获取请求头
            connection.requestMethod = "HEAD"
            connection.connect()
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                return connection.getHeaderField("Content-Length").toLong()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0L
    }
}

object VideoCache2 {
    @Volatile
    private var cache: SimpleCache? = null
    fun getSimpleCache(
        context: Context, cacheFile: File
    ): SimpleCache {
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