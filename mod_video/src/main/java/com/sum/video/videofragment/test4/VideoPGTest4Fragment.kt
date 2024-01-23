package com.sum.video.videofragment.test4

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.sum.common.util.Loge
import com.sum.video.R
import com.sum.video.videofragment.test4.exovideoplayer.ExoVideoPlayer
import com.sum.video.videofragment.test4.exovideoplayer.IVideoStateListener
import com.sum.video.videofragment.test4.exovideoplayer.State

class VideoPGTest4Fragment : Fragment() {
    val url by lazy {
        arguments?.getString("url", "")
    }
    val position by lazy {
        arguments?.getInt("position", 0)
    }

    companion object {
        fun newInstance(url: String, position: Int): Fragment {
            return VideoPGTest4Fragment().apply {
                val bundle = Bundle()
                bundle.putString("url", url)
                bundle.putInt("position", position)
                arguments = bundle
            }
        }
    }

    private val player by lazy { ExoVideoPlayer(requireActivity()) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val itemView = inflater.inflate(R.layout.videopgtest4_fragment, container, false)
        val frameLayout = itemView.findViewById<FrameLayout>(R.id.videopgtest4_frameLayout)
        frameLayout.addView(player)
        player.listener = object : IVideoStateListener {
            override fun onStateChange(state: State) {
                Loge.e(state.toString())
                when (state) {
                    is State.End -> {
                        player.load()
                        player.play()
                    }

                    is State.Idle -> {
                        Loge.e(state.currentPosition.toString())
                    }

                    is State.Error -> {
                        Loge.e(state.exception.toString())
                    }

                    else -> {}
                }
            }

        }
        return itemView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val button = view.findViewById<Button>(R.id.btn_fullscreen)
        button.setOnClickListener {

            if (this.getResources()
                    .getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
            ) {//横屏
                requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            } else if (this.getResources()
                    .getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
            ) {//竖屏
                requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            }

        }
    }

    override fun onResume() {
        super.onResume()
        player.url = url!!
        player.load()
        player.play()
    }

    override fun onPause() {
        super.onPause()
        player.stop()
        // player.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        Loge.e("onDestroy" + position)
        player.release()
    }
}