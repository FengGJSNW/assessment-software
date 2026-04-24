package com.example.xiaomingassistant

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import com.example.xiaomingassistant.data.repository.WeatherRepository
import com.example.xiaomingassistant.location.LocationHelper
import com.example.xiaomingassistant.ui.view.TopBarWithScrollView
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
        initViews()

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



    private fun initViews() {
        topBarView = findViewById(R.id.weather_topbar_container)
        locationText = findViewById(R.id.weather_text_location)
        degreeText = findViewById(R.id.weather_text_degree)
        stateText = findViewById(R.id.weather_text_state)
        maxDegreeText = findViewById(R.id.weather_text_max_degree)
        minDegreeText = findViewById(R.id.weather_text_min_degree)
    }

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
                            Toast.makeText(this, "天气获取失败", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            onError = { msg ->
                Log.e("LOC_FLOW", "location error=$msg")
                runOnUiThread {
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, "没有定位权限", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationHelper.destroy()
    }
}