package com.sum.main.ui

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.ExplainReasonCallback
import com.permissionx.guolindev.callback.RequestCallback
import com.permissionx.guolindev.request.ExplainScope
import com.sum.common.provider.MainServiceProvider
import com.sum.common.util.Loge
import com.sum.framework.base.BaseDataBindActivity
import com.sum.framework.ext.countDownCoroutines
import com.sum.framework.ext.onClick
import com.sum.framework.utils.NetworkUtil
import com.sum.framework.utils.StatusBarSettingHelper
import com.sum.main.R
import com.sum.main.databinding.ActivitySplashBinding
import java.security.Permission
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @author mingyan.su
 * @date   2023/3/29 14:25
 * @desc   启动页
 */
class SplashActivity : BaseDataBindActivity<ActivitySplashBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        StatusBarSettingHelper.setStatusBarTranslucent(this)
        mBinding.tvSkip.onClick {
            MainServiceProvider.toMain(this)
        }
        //倒计时
        countDownCoroutines(500, lifecycleScope, onTick = {
            mBinding.tvSkip.text = getString(R.string.splash_time, it.plus(1).toString())
        }) {
            MainServiceProvider.toMain(this)
            finish()
        }

        mBinding.netInfo.text = "拍照"
        mBinding.netInfo.setOnClickListener {


        }
        PermissionX.init(this).permissions("android.permission.READ_PHONE_STATE")
            .explainReasonBeforeRequest()
            .onExplainRequestReason { scope, deniedList -> }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    Loge.e("权限同意")
                } else {
                    Loge.e("用户拒绝权限")
                }
            }

    }

    private val format = SimpleDateFormat("yyyy-MM-dd:HH:mm", Locale.CHINA)
}