package com.sum.video.videofragment.test

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import cn.jzvd.JzvdStd
import com.bumptech.glide.Glide
import com.danikula.videocache.HttpProxyCacheServer
import com.sum.common.util.Loge
import com.sum.framework.adapter.BaseBindViewHolder
import com.sum.framework.adapter.BaseRecyclerViewAdapter
import com.sum.video.databinding.VideoItemBinding

class Video_Adapter(val httpProxyCacheServer: HttpProxyCacheServer) :
    BaseRecyclerViewAdapter<String, VideoItemBinding>() {


    override fun onBindDefViewHolder(
        holder: BaseBindViewHolder<VideoItemBinding>,
        item: String?,
        position: Int
    ) {
        if (item == null) return
        Loge.e(item)
        val url = httpProxyCacheServer.getProxyUrl(item)
        holder.binding.videoItem.setUp(url, "第" + (position + 1) + "页视频")
        holder.binding.videoItem.startVideo()
        // Glide.with(holder.itemView.context).load(item).into(holder.binding.videoItem.thumbImageView)

    }

    override fun getViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): VideoItemBinding {
        return VideoItemBinding.inflate(layoutInflater)
    }
}