package com.sum.video.videofragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.util.Util
import com.sum.video.databinding.PlayerviewBinding

class PageVideoFragment(private val url: String) : Fragment() {

    // 创建播放器
    private val player by lazy {
        ExoPlayer.Builder(requireActivity()).build()
    }
    private val binding by lazy {
        PlayerviewBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        MediaPlayerManager.getDefault().init(
            requireActivity(),
            Util.getUserAgent(requireActivity(), requireActivity().packageName)
        )
        player.setMediaSource(
            MediaPlayerManager.getDefault().buildDataSource(requireActivity(), url)
        )
        (view as StyledPlayerView).player = player
        player.playWhenReady = true
    }

    override fun onPause() {
        super.onPause()
    }
}