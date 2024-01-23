package com.sum.video.videofragment.test4

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.sum.common.constant.VIDEO_TEST4_EXOVIDEOPLAYERACTIVITY
import com.sum.common.util.Loge
import com.sum.framework.base.BaseDataBindActivity
import com.sum.video.databinding.ExovideoplayerActivityBinding
import com.sum.video.videofragment.VideoPageAdapter
import com.sum.video.videofragment.test4.exovideoplayer.ExoVideoPlayer

@Route(path = VIDEO_TEST4_EXOVIDEOPLAYERACTIVITY)
class ExoVideoPlayerTest4Activity : BaseDataBindActivity<ExovideoplayerActivityBinding>() {
    val url =
        "http://wxsnsdy.tc.qq.com/105/20210/snsdyvideodownload?filekey=30280201010421301f0201690402534804102ca905ce620b1241b726bc41dcff44e00204012882540400&bizid=1023&hy=SH&fileparam=302c020101042530230204136ffd93020457e3c4ff02024ef202031e8d7f02030f42400204045a320a0201000400"
    val list by lazy {
        mutableListOf<String>()
    }

    override fun initView(savedInstanceState: Bundle?) {
        for (i in 0..200) {
            list.add(url)
        }

        mBinding.test4Viewpage2.apply {
            adapter = ExovideoTest4ViewPage2Adapter(supportFragmentManager, lifecycle, list)
            orientation = ViewPager2.ORIENTATION_VERTICAL
            offscreenPageLimit =1

        }
    }

    override fun onResume() {
        super.onResume()

    }
}