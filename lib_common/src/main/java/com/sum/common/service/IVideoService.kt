package com.sum.common.service

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.template.IProvider

interface IVideoService : IProvider {
    fun getFragment(): DialogFragment
    fun getVideoFragment2(): DialogFragment
}