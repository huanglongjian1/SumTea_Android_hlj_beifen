package com.sum.video.videofragment.test5

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentViewHolder
import com.sum.video.videofragment.VideoFragment

class VideoPageAdapter(
    private val fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val urls: List<String>
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return urls.size
    }

    override fun onViewAttachedToWindow(holder: FragmentViewHolder) {
        super.onViewAttachedToWindow(holder)
        // 获取刚进入视窗的 Fragment 实例
        val attachFragment = getFragment(getItemId(holder.adapterPosition))
        //  (attachFragment as? VideoFragment)?.load()
    }

    override fun onViewDetachedFromWindow(holder: FragmentViewHolder) {
        // 获取刚移出视窗的 Fragment 实例
        val detachFragment = getFragment(getItemId(holder.adapterPosition))
        //  (detachFragment as? VideoFragment)?.release()
    }

    override fun createFragment(position: Int): Fragment {
        return VideoFragment()
    }
}