package com.sum.video.service

import android.content.Context
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.sum.common.constant.VIDEO_SERVICE
import com.sum.common.service.IVideoService
import com.sum.video.videofragment.VideoDialogFragment
import com.sum.video.videofragment.test2.VideoFragment2

@Route(path = VIDEO_SERVICE)
class VideoService : IVideoService {
    override fun getFragment(): DialogFragment {
        return VideoDialogFragment()
    }

    override fun getVideoFragment2(): DialogFragment {
        return VideoFragment2()
    }

    override fun init(context: Context?) {

    }
}