package com.example.xiaomingassistant

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import com.amap.api.location.AMapLocationClient
import com.example.xiaomingassistant.data.model.WeatherForecast
import com.example.xiaomingassistant.data.repository.WeatherRepository
import com.example.xiaomingassistant.location.LocationHelper
import com.example.xiaomingassistant.ui.view.TopBarWithScrollView
import com.example.xiaomingassistant.util.calc.dp
import com.example.xiaomingassistant.util.toast.showShortToast

class WeatherActivity : AppCompatActivity() {

    private lateinit var topBarView: TopBarWithScrollView
    private lateinit var locationText: TextView
    private lateinit var degreeText: TextView
    private lateinit var stateText: TextView
    private lateinit var maxDegreeText: TextView
    private lateinit var minDegreeText: TextView
    private lateinit var forecastList: LinearLayout

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
        forecastList = findViewById(R.id.weather_forecast_list)
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
                            val tempText = data.temp.toDoubleOrNull()
                                ?.toInt()
                                ?.toString()
                                ?: data.temp.ifBlank { "--" }

                            // locationText.text = data.city
                            degreeText.text = "$tempText°"
                            stateText.text = data.wea
                            maxDegreeText.text = "最高 ${data.max.ifBlank { "--" }}°"
                            minDegreeText.text = "最低 ${data.min.ifBlank { "--" }}°"
                            renderForecasts(data.forecasts)
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

    // 将未来三天预报逐行渲染到模糊卡片内
    private fun renderForecasts(forecasts: List<WeatherForecast>) {
        forecastList.removeAllViews()

        if (forecasts.isEmpty()) {
            forecastList.addView(createEmptyForecastView())
            return
        }

        forecasts.forEach { forecast ->
            val itemView = LayoutInflater.from(this)
                .inflate(R.layout.myview_inner_weather_forecast_day, forecastList, false)

            itemView.findViewById<TextView>(R.id.weather_forecast_date).text = formatForecastDate(forecast.date)
            itemView.findViewById<TextView>(R.id.weather_forecast_week).text = forecast.week.ifBlank { "--" }
            itemView.findViewById<TextView>(R.id.weather_forecast_weather).text = formatWeatherText(forecast)
            itemView.findViewById<TextView>(R.id.weather_forecast_wind).text = formatWindText(forecast)
            itemView.findViewById<TextView>(R.id.weather_forecast_temp).text = formatTempText(forecast)

            forecastList.addView(itemView)
        }
    }

    private fun createEmptyForecastView(): View {
        return TextView(this).apply {
            text = "暂无未来三天天气"
            setTextColor(getColor(R.color.white_alpha80))
            textSize = 15f
            setPadding(18.dp, 18.dp, 18.dp, 18.dp)
        }
    }

    private fun formatForecastDate(date: String): String {
        val parts = date.split("-")
        return if (parts.size == 3) {
            "${parts[1]}/${parts[2]}"
        } else {
            date.ifBlank { "--/--" }
        }
    }

    private fun formatWeatherText(forecast: WeatherForecast): String {
        val dayWeather = forecast.dayWeather.ifBlank { "--" }
        val nightWeather = forecast.nightWeather.ifBlank { dayWeather }
        return if (dayWeather == nightWeather) dayWeather else "${dayWeather}转$nightWeather"
    }

    private fun formatWindText(forecast: WeatherForecast): String {
        val wind = forecast.dayWind.ifBlank { "风向未知" }
        val speed = forecast.dayWindSpeed.ifBlank { "风力未知" }
        return "$wind $speed"
    }

    private fun formatTempText(forecast: WeatherForecast): String {
        val dayTemp = forecast.dayTemp.ifBlank { "--" }
        val nightTemp = forecast.nightTemp.ifBlank { "--" }
        return "$dayTemp° / $nightTemp°"
    }
}
