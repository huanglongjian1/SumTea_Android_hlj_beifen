package com.sum.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.sum.common.constant.MAIN_TEST_ACTIVITY

@Route(path = MAIN_TEST_ACTIVITY)
class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.style_mine_service)
        setContentView(R.layout.activity_splash)
    }
}