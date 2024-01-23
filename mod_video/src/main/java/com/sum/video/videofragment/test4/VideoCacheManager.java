package com.sum.video.videofragment.test4;

import android.annotation.SuppressLint;
import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;
import com.danikula.videocache.file.FileNameGenerator;

import java.io.File;

/**
 * videocache 本地缓存、播放代理类 Created by liangzhen on 2018/07/30.
 */
public class VideoCacheManager {

    private Context context;

    private HttpProxyCacheServer httpProxyCacheServer;

    @SuppressLint("StaticFieldLeak")
    private static volatile VideoCacheManager videoCacheManager;

    private String feed;

    private VideoCacheManager(Context context) {
        this.context = context.getApplicationContext();//避免内存泄露，使用APP的context
    }

    public static VideoCacheManager getVideoCacheManager(Context context) {
        if (videoCacheManager == null) {
            synchronized (VideoCacheManager.class) {
                if (videoCacheManager == null) {
                    videoCacheManager = new VideoCacheManager(context);
                }
            }
        }
        return videoCacheManager;
    }

    //设置一下ID（用于缓存的名字）
    public VideoCacheManager setFeed(String feed) {
        this.feed = feed;
        return this;
    }

    public String getVideoCacheUriDir() {
        String videoCachePath = context.getExternalFilesDir("video_cache").getAbsolutePath();
        makeMultiDirs(videoCachePath);
        return videoCachePath;
    }

    public HttpProxyCacheServer getProxy() {
        if (httpProxyCacheServer == null) {
            httpProxyCacheServer = newProxy();
        }
        return httpProxyCacheServer;
    }

    //用于做一些缓存规则
    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(context)
                .cacheDirectory(new File(getVideoCacheUriDir()))
                .maxCacheFilesCount(5)
                .maxCacheSize(1024 * 1024 * 300)
                .fileNameGenerator(new FileNameGenerator() {
                    @Override
                    public String generate(String url) {
                        return feed == null ? "" : feed + ".mp4";
                    }
                })
                .build();
    }


    //创建文件夹
    public static boolean makeMultiDirs(String dirs) {
        boolean mkOK = false;
        File file = new File(dirs);
        if (!file.exists()) {
            mkOK = file.mkdirs();
        }
        return mkOK;
    }

}