package com.sum.video.videofragment

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.danikula.videocache.HttpProxyCacheServer
import com.danikula.videocache.file.FileNameGenerator
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util
import com.sum.framework.helper.SumAppHelper
import java.io.File
import java.io.FileInputStream
import java.io.IOException

/**
 * date        ：2019/10/18
 * author      ：蒙景博
 * description ：
 */
class MediaPlayerManager {

    private var mUserAgent = this.javaClass.simpleName
    private var feed: String? = null

    // 视频加载代理
    @Volatile
    private var mProxyCacheServer: HttpProxyCacheServer? = null
    private var DISK_CACHE_DIR_NAME = "Video"

    companion object {
        @Volatile
        private var INSTANCES: MediaPlayerManager? = null

        fun getDefault(): MediaPlayerManager =
            INSTANCES ?: synchronized(this) { MediaPlayerManager().also { INSTANCES = it } }

        fun getHttpProxyCacheServer(): HttpProxyCacheServer {
            return getDefault().mProxyCacheServer ?: getDefault().createProxyCacheServer(
                SumAppHelper.getApplication()
            )
        }
    }

    fun init(context: Context, userAgent: String, cacheName: String = "Video"): MediaPlayerManager {
        mProxyCacheServer = createProxyCacheServer(context)
        mUserAgent = Util.getUserAgent(context, userAgent)
        DISK_CACHE_DIR_NAME = cacheName
        return this
    }

    /**
     * 将传入的uri构建为一个规媒体资源
     *
     * DashMediaSource         DASH.
     * SsMediaSource           SmoothStreaming.
     * HlsMediaSource          HLS.
     * ProgressiveMediaSource  常规媒体文件.
     *
     * @return 返回一个常规媒体资源
     */
    fun buildDataSource(context: Context, uri: String): MediaSource {
        // 构建一个默认的Http数据资源处理工厂
        val mHttpDataSourceFactory = DefaultHttpDataSource.Factory()
        // DefaultDataSourceFactory决定数据加载模式，是从网络加载还是本地缓存加载
        val mDataSourceFactory = DefaultDataSourceFactory(context, mHttpDataSourceFactory)
        // AndroidVideoCache库不支持DASH, SS(Smooth Streaming：平滑流媒体，如直播流), HLS数据格式，所以这里使用一个常见媒体转换数据资源工厂
        return ProgressiveMediaSource.Factory(mDataSourceFactory)
            .createMediaSource(MediaItem.fromUri(Uri.parse(getProxyUrl(uri))))
    }

    /**
     * 创建视频加载代理
     */
    private fun createProxyCacheServer(context: Context): HttpProxyCacheServer {
        return HttpProxyCacheServer.Builder(context)
            .cacheDirectory(getDiskCacheDirectory(context)) // 设置磁盘存储地址
            .maxCacheSize(1024 * 1024 * 1024)     // 设置可存储1G资源
//            .fileNameGenerator(FileNameGenerator {
//                return@FileNameGenerator if (feed == null) "" else "$feed.mp4"
//            })
            .build()
    }

    //设置一下ID（用于缓存的名字）
    fun setFeed(feed: String): MediaPlayerManager {
        this.feed = feed;
        return this;
    }

    /**
     * 获取代理地址
     */
    fun getProxyUrl(url: String): String? = mProxyCacheServer?.getProxyUrl(url)

    /**
     * 是否缓存
     * @return true:已经缓存
     */
    fun isCached(url: String) = mProxyCacheServer?.isCached(url) ?: false

    /**
     * 视频磁盘缓存地址
     */
    @SuppressLint("SdCardPath")
    fun getDiskCacheDirectory(context: Context): File {
        var cacheDir: File? = null
        if (Environment.MEDIA_MOUNTED == getExternalStorageState()) {
            cacheDir = getExternalCacheDir(context)
        }
        if (cacheDir == null) {
            cacheDir = context.cacheDir
        }
        if (cacheDir == null) {
            val cacheDirPath = "/data/data/${context.packageName}/cache/"
            cacheDir = File(cacheDirPath)
        }
        return File(cacheDir, DISK_CACHE_DIR_NAME)
    }

    private fun getExternalStorageState(): String {
        return try {
            Environment.getExternalStorageState()
        } catch (e: NullPointerException) {
            ""
        }
    }

    private fun getExternalCacheDir(context: Context): File? {
        val cacheDir = context.getExternalFilesDir("Cache")
        if (!cacheDir?.exists()!!) {
            if (!cacheDir.mkdirs()) {
                return null
            }
        }
        return cacheDir
    }

    /**
     * 删除所有视频缓存
     */
    @Throws(IOException::class)
    fun deleteAllCache(context: Context) {
        val mFile = getDiskCacheDirectory(context)
        if (!mFile.exists()) return
        val mFiles = mFile.listFiles()
        if (!mFiles.isNullOrEmpty() && mFiles.isNotEmpty()) {
            mFiles.forEach {
                deleteVideoCache(it)
            }
        }
    }

    /**
     * 删除视频缓存
     */
    @Throws(IOException::class)
    private fun deleteVideoCache(file: File) {
        if (file.isFile && file.exists()) {
            val isDeleted = file.delete()
            Log.e(javaClass.simpleName, "删除视频缓存：${file.path}\t删除状态：$isDeleted")
        }
    }

    /**
     * 获取磁盘缓存的数据大小，单位：KB
     */
    fun getDiskCacheSize(context: Context): Long {
        val file = getDiskCacheDirectory(context)
        var blockSize = 0L
        try {
            blockSize = if (file.isDirectory) getFileSizes(file) else getFileSize(file)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return blockSize
    }

    private fun getFileSizes(file: File): Long {
        var size = 0L
        file.listFiles()?.forEach {
            if (it.isDirectory) {
                size += getFileSizes(it)
            } else {
                try {
                    size += getFileSize(it)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return size
    }

    private fun getFileSize(file: File): Long {
        var size = 0L
        if (file.exists()) {
            FileInputStream(file).use {
                size = it.available().toLong()
            }
        }
        return size
    }
}