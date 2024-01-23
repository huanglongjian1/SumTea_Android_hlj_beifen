package com.sum.video.videofragment.test7.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.sum.framework.adapter.BaseBindViewHolder
import com.sum.framework.adapter.BaseRecyclerViewAdapter
import com.sum.video.databinding.VideoFragment2Binding

class MusicTest7Adapter : BaseRecyclerViewAdapter<MusicData, VideoFragment2Binding>() {
    override fun onBindDefViewHolder(
        holder: BaseBindViewHolder<VideoFragment2Binding>,
        item: MusicData?,
        position: Int
    ) {
        holder.binding.play.text = item?.videoUrl
        Glide.with(holder.itemView.context).load(item?.imageUrl).into(holder.binding.rvItemImage)
    }

    override fun getViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): VideoFragment2Binding {
        return VideoFragment2Binding.inflate(layoutInflater)
    }
}

data class MusicData(val videoUrl: String, val imageUrl: String)