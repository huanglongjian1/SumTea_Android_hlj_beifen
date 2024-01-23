package com.sum.video.videofragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import cn.jzvd.JzvdStd
import com.danikula.videocache.HttpProxyCacheServer
import com.sum.common.constant.KEY_VIDEO_PLAY_LIST
import com.sum.common.util.Loge
import com.sum.video.databinding.VideoFragmentBinding
import com.sum.video.videofragment.test.VideoPgAdapter
import com.tencent.mmkv.MMKV

class VideoDialogFragment : DialogFragment() {
    private lateinit var binding: VideoFragmentBinding
    private val urls by lazy {
        mutableListOf<String>()
    }
    private val httpProxyCacheServer by lazy {
        HttpProxyCacheServer.Builder(requireActivity())
            .cacheDirectory(requireActivity().getExternalFilesDir("videoDialogFragment3")) // 设置磁盘存储地址
            .maxCacheSize(1024 * 1024 * 100)     // 设置可存储1G资源
            .build()
    }
    private lateinit var _adapter: VideoPgAdapter
    private val snapHelper by lazy {
        PagerSnapHelper()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = VideoFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        urls.add("http://wxsnsdy.tc.qq.com/105/20210/snsdyvideodownload?filekey=30280201010421301f0201690402534804102ca905ce620b1241b726bc41dcff44e00204012882540400&bizid=1023&hy=SH&fileparam=302c020101042530230204136ffd93020457e3c4ff02024ef202031e8d7f02030f42400204045a320a0201000400")
        urls.add("https://user-images.githubusercontent.com/20841967/233770382-1f9fee7f-d418-4a27-9785-98c36eca143f.mp4")
        urls.add("https://user-images.githubusercontent.com/20841967/233770462-88ce1e3c-f1bd-47de-92e1-b4dff5818ecb.mp4")
        urls.add("https://user-images.githubusercontent.com/20841967/233770663-7a322b0d-43e4-4a86-86d1-57af50687e02.mp4")

        MMKV.defaultMMKV().decodeStringSet(KEY_VIDEO_PLAY_LIST)?.forEach {
            Loge.e(it.toString())
            urls.add(it)
        }

        urls.forEach {
            Loge.e(it)
        }

        _adapter = VideoPgAdapter(requireActivity(), urls)
        binding.mainRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            snapHelper.attachToRecyclerView(this)
            adapter = _adapter
        }
        binding.mainRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE ->//停止滚动
                    {
                        playVideoInSnapPosition(
                            recyclerView,
                            snapHelper,
                            binding.mainRecyclerView.layoutManager as LinearLayoutManager
                        );
                    }

                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        //拖动
                    }

                    RecyclerView.SCROLL_STATE_SETTLING -> {
                        //惯性滑动
                        // 发生惯性滑动时停止播放任何正在播放的视频，并释放相关的资源。这可以防止在快速滑动过程中出现视频播放错乱或冲突的情况。
                        JzvdStd.releaseAllVideos();
                    }
                }
            }
        })
    }

    var currentPosition = 0
    private fun playVideoInSnapPosition(
        recyclerView: RecyclerView,
        snapHelper: SnapHelper,
        linearLayoutManager: LinearLayoutManager

    ) {
        // 获取当前固定的视图
        val view = snapHelper.findSnapView(linearLayoutManager);
        if (view != null) {
            // 获取当前位置
            val position = recyclerView.getChildAdapterPosition(view);
            if (currentPosition != position) {
                // 如果切换到了新的固定视图
                JzvdStd.releaseAllVideos(); // 释放之前的资源（停止播放上一个视频）
                val viewHolder = recyclerView.getChildViewHolder(view);
                if (viewHolder is VideoPgAdapter.VideoViewHolder) {
                    // 开始播放视频
                    viewHolder.jzVideo.startVideo()
                }
            }
            currentPosition = position;
        }
    }

    override fun onPause() {
        super.onPause()
        JzvdStd.releaseAllVideos()
    }
}