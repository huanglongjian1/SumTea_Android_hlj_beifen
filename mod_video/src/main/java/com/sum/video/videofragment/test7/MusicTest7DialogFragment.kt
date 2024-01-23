package com.sum.video.videofragment.test7

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sum.common.util.Loge
import com.sum.video.databinding.DialogFragmentBinding

class MusicTest7DialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: DialogFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFragmentBinding.inflate(inflater)
        return binding.root
    }

    private val exoPlayer by lazy {
        CacheController.getMediaSourceFactory()?.let {
            Loge.e("getMediaSourceFactory")
            ExoPlayer.Builder(requireActivity()).setMediaSourceFactory(it).build().apply {
                playWhenReady = true
            }
        }
    }
    private val urlList by lazy {
        mutableListOf(URL_5, URL_4, URL_3, URL_2, URL_1)
    }
    var index = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.dialogPlayerView.player = exoPlayer
        binding.changeTv.setOnClickListener {
            Loge.e("点击了:"+index)
            if (index>=urlList.size)index=0
            exoPlayer?.stop()
            exoPlayer?.setMediaItem(MediaItem.fromUri(urlList[index]))
            exoPlayer?.prepare()
            index++
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        Loge.e("onDestroy")
        binding.dialogPlayerView.player?.stop()
        binding.dialogPlayerView.player?.release()
        binding.dialogPlayerView.player = null

    }
}