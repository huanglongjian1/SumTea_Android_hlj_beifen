package com.sum.video.videofragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.danikula.videocache.HttpProxyCacheServer
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sum.video.databinding.PlayerviewBinding
import com.sum.video.databinding.VideoFragmentBinding
import com.sum.video.videofragment.test.Video_Adapter

class VideoFragment : BottomSheetDialogFragment() {
    private lateinit var binding: VideoFragmentBinding

    //播放位置
    private var mPlayingPosition = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = VideoFragmentBinding.inflate(layoutInflater)
        return binding.root
    }


    // private var exoAdapter: VideoPageAdapter? = null
    private val urls = mutableListOf("https://user-images.githubusercontent.com/20841967/233770339-e9962ea2-436b-4c85-83f4-f80321015f20.mp4")
    private val _adapter by lazy {
        Video_Adapter(httpProxyCacheServer)
    }
    private val playerView by lazy {
        PlayerviewBinding.inflate(layoutInflater).root as StyledPlayerView
    }
    private val player by lazy {
        ExoPlayer.Builder(requireActivity()).build()
    }
    private val snapHelper by lazy {
        PagerSnapHelper()
    }
    private val httpProxyCacheServer by lazy {
        HttpProxyCacheServer.Builder(requireActivity())
            .cacheDirectory(requireActivity().getExternalFilesDir("cache4")) // 设置磁盘存储地址
            .maxCacheSize(1024 * 1024 * 1024)     // 设置可存储1G资源
            .build()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        urls.add("http://wxsnsdy.tc.qq.com/105/20210/snsdyvideodownload?filekey=30280201010421301f0201690402534804102ca905ce620b1241b726bc41dcff44e00204012882540400&bizid=1023&hy=SH&fileparam=302c020101042530230204136ffd93020457e3c4ff02024ef202031e8d7f02030f42400204045a320a0201000400")
//        urls.add("https://user-images.githubusercontent.com/20841967/233770382-1f9fee7f-d418-4a27-9785-98c36eca143f.mp4")
//        urls.add("https://user-images.githubusercontent.com/20841967/233770462-88ce1e3c-f1bd-47de-92e1-b4dff5818ecb.mp4")
//        urls.add("https://user-images.githubusercontent.com/20841967/233770663-7a322b0d-43e4-4a86-86d1-57af50687e02.mp4")

        _adapter.setData(urls)
        binding.mainRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            snapHelper.attachToRecyclerView(this)
            adapter = _adapter
        }


       // binding.video.setUp(  httpProxyCacheServer.getProxyUrl(urls[0]), "0")
    }

    override fun onPause() {
        super.onPause()

    }
}

