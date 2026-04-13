package com.example.xiaomingassistant.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.widget.NestedScrollView
import com.example.xiaomingassistant.R
import com.example.xiaomingassistant.ui.view.TopBarWithScrollView

class WeatherActivity : AppCompatActivity() {

    private lateinit var topBarView: TopBarWithScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_interface_weather)

        // 全面屏设置
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.isAppearanceLightStatusBars = true



    }
}