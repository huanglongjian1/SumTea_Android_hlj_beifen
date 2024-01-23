package com.sum.video.videofragment.test4

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ExovideoTest4ViewPage2Adapter(val fragmentManager: FragmentManager,lifecycle: Lifecycle,val list: List<String>): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
       return list.size
    }

    override fun createFragment(position: Int): Fragment {
       return VideoPGTest4Fragment.newInstance(list[position],position)
    }
}