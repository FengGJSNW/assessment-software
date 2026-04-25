package com.example.xiaomingassistant

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import com.example.xiaomingassistant.data.repository.WeatherRepository
import com.example.xiaomingassistant.location.LocationHelper
import com.example.xiaomingassistant.ui.view.TopBarWithScrollView
import com.example.xiaomingassistant.util.toast.showShortToast
import com.amap.api.location.AMapLocationClient

class WeatherActivity : AppCompatActivity() {

    private lateinit var topBarView: TopBarWithScrollView
    private lateinit var locationText: TextView
    private lateinit var degreeText: TextView
    private lateinit var stateText: TextView
    private lateinit var maxDegreeText: TextView
    private lateinit var minDegreeText: TextView

    private val weatherRepository = WeatherRepository()
    private lateinit var locationHelper: LocationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        bindViews()

        // 全面屏适配
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.isAppearanceLightStatusBars = false

        // 高德警告：确保调用SDK任何接口前先调用 updatePrivacyShow、updatePrivacyAgree
        AMapLocationClient.updatePrivacyShow(this, true, true)
        AMapLocationClient.updatePrivacyAgree(this, true)

        locationHelper = LocationHelper(this)

        requestLocationAndLoadWeather()
    }


    // 绑定天气页需要展示的文本组件
    private fun bindViews() {
        topBarView = findViewById(R.id.weather_topbar_container)
        locationText = findViewById(R.id.weather_text_location)
        degreeText = findViewById(R.id.weather_text_degree)
        stateText = findViewById(R.id.weather_text_state)
        maxDegreeText = findViewById(R.id.weather_text_max_degree)
        minDegreeText = findViewById(R.id.weather_text_min_degree)
    }

    // 先请求定位，再根据城市拉取天气数据
    private fun requestLocationAndLoadWeather() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                100
            )
            return
        }

        locationHelper.locateOnce(
            onSuccess = { result ->
                Log.d("LOC_FLOW", "result.city=${result.city}")
                Log.d("LOC_FLOW", "result.district=${result.district}")
                Log.d("LOC_FLOW", "result.address=${result.address}")

                locationText.text = result.city + " " + result.district

                weatherRepository.getWeather(result.city) { data ->
                    runOnUiThread {
                        if (data != null) {
                            val tempInt = data.temp.toDoubleOrNull()?.toInt() ?: 0

                            // locationText.text = data.city
                            degreeText.text = "${tempInt}°"
                            stateText.text = data.wea
                            maxDegreeText.text = "最高 ${data.max}°"
                            minDegreeText.text = "最低 ${data.min}°"
                        } else {
                            showShortToast("天气获取失败")
                        }
                    }
                }
            },
            onError = { msg ->
                Log.e("LOC_FLOW", "location error=$msg")
                runOnUiThread {
                    showShortToast(msg)
                }
            }
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationAndLoadWeather()
        } else {
            showShortToast("没有定位权限")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationHelper.destroy()
    }
}
