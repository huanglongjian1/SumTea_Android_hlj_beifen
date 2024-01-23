package com.sum.video.videofragment.test6;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;

import com.danikula.videocache.HttpProxyCacheServer;
import com.sum.common.util.Loge;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends MediaBrowserServiceCompat {
    private static final String TAG = "MusicService";
    private MediaSessionCompat mSession;
    private PlaybackStateCompat mPlaybackState;
    private MediaPlayer mMediaPlayer;
    File music_service;
    String cacheurl;
    private HttpProxyCacheServer httpProxyCacheServer;

    @Override
    public void onCreate() {
        super.onCreate();
        music_service = new File(getCacheDir() + "/music_service");
        if (!music_service.exists()) {
            music_service.mkdirs();
        }
        httpProxyCacheServer = new HttpProxyCacheServer.Builder(getApplication())
                .cacheDirectory(music_service)
                .maxCacheSize(1024 * 1024 * 100)
                .build();


        mPlaybackState = new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_NONE, 0, 1.0f)
                .build();

        mSession = new MediaSessionCompat(this, "MusicService");
        mSession.setCallback(SessionCallback);//设置回调
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mSession.setPlaybackState(mPlaybackState);

        //设置token后会触发MediaBrowserCompat.ConnectionCallback的回调方法
        //表示MediaBrowser与MediaBrowserService连接成功
        setSessionToken(mSession.getSessionToken());

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(PreparedListener);
        mMediaPlayer.setOnCompletionListener(CompletionListener);

    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        Log.e(TAG, "onGetRoot-----------");
        return new BrowserRoot("MEDIA_ID_ROOT", null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        Log.e(TAG, "onLoadChildren--------");
        //将信息从当前线程中移除，允许后续调用sendResult方法
        result.detach();


        //我们模拟获取数据的过程，真实情况应该是异步从网络或本地读取数据
        MediaMetadataCompat metadata = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "jinglebells")
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "圣诞歌")
                .build();
        ArrayList<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();
        mediaItems.add(createMediaItem(metadata));


        //向Browser发送数据
        result.sendResult(mediaItems);

    }

    private MediaBrowserCompat.MediaItem createMediaItem(MediaMetadataCompat metadata) {
        return new MediaBrowserCompat.MediaItem(
                metadata.getDescription(),
                MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
        );
    }

    private Handler handler = new Handler(Looper.myLooper());
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            MediaMetadataCompat metadataCompat = new MediaMetadataCompat.Builder()
                    .putLong("current_duration", mMediaPlayer.getCurrentPosition())
                    .build();
            mSession.setMetadata(metadataCompat);
            handler.postDelayed(this, 1000);
        }
    };
    /**
     * 响应控制器指令的回调
     */
    private android.support.v4.media.session.MediaSessionCompat.Callback SessionCallback = new MediaSessionCompat.Callback() {
        /**
         * 响应MediaController.getTransportControls().play
         */
        @Override
        public void onPlay() {
            Log.e(TAG, "onPlay");
            handler.postDelayed(runnable, 1000);
            if (mPlaybackState.getState() == PlaybackStateCompat.STATE_PAUSED) {
                mMediaPlayer.start();
                mPlaybackState = new PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0f)
                        .build();
                mSession.setPlaybackState(mPlaybackState);
            }
        }

        /**
         * 响应MediaController.getTransportControls().onPause
         */
        @Override
        public void onPause() {
            Log.e(TAG, "onPause");
            handler.removeCallbacks(runnable);
            if (mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
                mMediaPlayer.pause();
                mPlaybackState = new PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_PAUSED, 0, 1.0f)
                        .build();
                mSession.setPlaybackState(mPlaybackState);
            }
        }


        /**
         * 响应MediaController.getTransportControls().playFromUri
         * @param uri
         * @param extras
         */
        @Override
        public void onPlayFromUri(Uri uri, Bundle extras) {
            Log.e(TAG, "onPlayFromUri");
            handler.post(runnable);
            try {
                switch (mPlaybackState.getState()) {
                    case PlaybackStateCompat.STATE_PLAYING:
                    case PlaybackStateCompat.STATE_PAUSED:
                    case PlaybackStateCompat.STATE_NONE:
                        mMediaPlayer.reset();

                        cacheurl = httpProxyCacheServer.getProxyUrl(uri.toString());

                        mMediaPlayer.setDataSource(MusicService.this, Uri.parse(cacheurl));
                        mMediaPlayer.prepare();//准备同步
                        mPlaybackState = new PlaybackStateCompat.Builder()
                                .setState(PlaybackStateCompat.STATE_CONNECTING, 0, 1.0f)
                                .build();
                        mSession.setPlaybackState(mPlaybackState);
                        Loge.e("getDuration:" + mMediaPlayer.getDuration());
                        //我们可以保存当前播放音乐的信息，以便客户端刷新UI
                        mSession.setMetadata(new MediaMetadataCompat.Builder()
                                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, extras.getString("title"))
                                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mMediaPlayer.getDuration())
                                .build()
                        );
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mMediaPlayer.seekTo(pos, MediaPlayer.SEEK_CLOSEST_SYNC);
                Loge.e("pos:" + String.valueOf(pos));
            }
        }

        @Override
        public void onPlayFromSearch(String query, Bundle extras) {
        }
    };

    /**
     * 监听MediaPlayer.prepare()
     */
    private MediaPlayer.OnPreparedListener PreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            Loge.e("onPrepared");
            mMediaPlayer.start();
            mPlaybackState = new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0f)
                    .build();
            mSession.setPlaybackState(mPlaybackState);
        }
    };

    /**
     * 监听播放结束的事件
     */
    private MediaPlayer.OnCompletionListener CompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            Loge.e("onCompletion");
            mPlaybackState = new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_NONE, 0, 1.0f)
                    .build();
            mSession.setPlaybackState(mPlaybackState);
            mMediaPlayer.reset();
        }
    };

}