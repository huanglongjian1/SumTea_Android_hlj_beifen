package com.sum.common.provider

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.launcher.ARouter
import com.sum.common.constant.VIDEO_SERVICE
import com.sum.common.service.IVideoService

object VideoServiceProvider {
    @Autowired(name = VIDEO_SERVICE)
    lateinit var videoService: IVideoService

    init {
        ARouter.getInstance().inject(this)
    }

    fun getFragment(): DialogFragment {
        return videoService.getFragment()
    }

    fun getVideoFragment2(): DialogFragment = videoService.getVideoFragment2()
}