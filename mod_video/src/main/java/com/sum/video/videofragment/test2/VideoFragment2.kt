package com.sum.video.videofragment.test2

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.MediaController.MediaPlayerControl
import androidx.fragment.app.DialogFragment
import com.sum.common.util.Loge
import com.sum.video.databinding.VideoFragment2Binding
import com.sum.video.videofragment.MediaPlayerManager

class VideoFragment2 : DialogFragment() {

    private val binding by lazy {
        VideoFragment2Binding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val url =
            "http://wxsnsdy.tc.qq.com/105/20210/snsdyvideodownload?filekey=30280201010421301f0201690402534804102ca905ce620b1241b726bc41dcff44e00204012882540400&bizid=1023&hy=SH&fileparam=302c020101042530230204136ffd93020457e3c4ff02024ef202031e8d7f02030f42400204045a320a0201000400"

        binding.videoView.setVideoPath(
            MediaPlayerManager.getHttpProxyCacheServer().getProxyUrl(url)
        )
        binding.play.setOnClickListener {
            binding.videoView.start()
        }
        val mediaController = MediaController(requireActivity())

        mediaController.setPrevNextListeners({
            Loge.e("上一首")
        }, {
            Loge.e("下一首")
        })

        mediaController.setAnchorView(binding.videoView)
        mediaController.setMediaPlayer(binding.videoView)
        binding.videoView.setMediaController(mediaController)

        binding.videoView.setOnCompletionListener { p0 ->
            p0?.start()
            binding.videoView.resume()
            Loge.e("重新播放")
            binding.videoView.start()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        Loge.e("onDestroy")
        binding.videoView.stopPlayback()
        binding.videoView.suspend()
    }
}