package com.sum.video.videofragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder

class VideoPageAdapter(
    private val fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val urls: List<String>
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return urls.size
    }

    override fun createFragment(position: Int): Fragment {
        return PageVideoFragment(urls[position])
    }

    override fun onBindViewHolder(
        holder: FragmentViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
    }

    fun getFragment(index: Int): VideoFragment? {
        return fragmentManager.findFragmentByTag("f${getItemId(index)}") as? VideoFragment
    }
}