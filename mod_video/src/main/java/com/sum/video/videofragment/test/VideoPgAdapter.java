package com.sum.video.videofragment.test;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.danikula.videocache.HttpProxyCacheServer;
import com.sum.video.R;

import java.util.List;

import cn.jzvd.JzvdStd;

public class VideoPgAdapter extends RecyclerView.Adapter<VideoPgAdapter.VideoViewHolder> {
    private Context context;
    private List<String> mUrlList;
    private HttpProxyCacheServer httpProxyCacheServer;

    public VideoPgAdapter(Context context, List<String> urlList) {
        this.context = context;
        this.mUrlList = urlList;
        httpProxyCacheServer = new HttpProxyCacheServer.Builder(context)
                .cacheDirectory(context.getExternalFilesDir("VideoPgAdapter")) // 设置磁盘存储地址
                .maxCacheSize(1024 * 1024 * 100)     // 设置可存储1G资源
                .build();
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoViewHolder(LayoutInflater.from(context).inflate(R.layout.video_item,
                parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        // 设置视频url和视频标题
        holder.jzVideo.setUp(httpProxyCacheServer.getProxyUrl(mUrlList.get(position)), "第" + (position + 1) + "视频",
                JzvdStd.STATE_NORMAL);
        // 设置自动播放
        if (position == 0) {
            holder.jzVideo.startVideo();
        }
        // 添加视频封面
         Glide.with(context).load(mUrlList.get(position)).into(holder.jzVideo.thumbImageView);
    }

    @Override
    public int getItemCount() {
        return mUrlList.size();
    }


    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        public JzvdStd jzVideo;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            // 视图绑定
            jzVideo = itemView.findViewById(R.id.video_item);

        }
    }

}
