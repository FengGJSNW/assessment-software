package com.example.xiaomingassistant.data.repository

import android.util.Log
import com.example.xiaomingassistant.data.model.WeatherData
import com.example.xiaomingassistant.data.network.weather.WeatherNetworkClient
import org.json.JSONObject
import java.net.URLEncoder

class WeatherRepository(
    private val networkClient: WeatherNetworkClient = WeatherNetworkClient()
) {

    companion object {
        private const val TAG = "WeatherRepositroy.kt"

        private const val BASE_URL = "http://pddfps.tianqiapi.com/api"
        private const val VERSION = "v63"
        private const val APP_ID = "14342518"
        private const val APP_SECRET = "D0qMb6tt"
    }

    /**
     * 对外暴露天气状况
     */
    fun getWeather(
        city: String? = null,
        callback: (WeatherData?) -> Unit
    ) {
        Thread {
            try {
                val requestUrl = buildWeatherUrl(city)
                val response = networkClient.get(requestUrl)
                val weatherData = parseWeatherData(response)

                Log.d(TAG, "request city=$city")
                Log.d(TAG, "request url=$requestUrl")
                Log.d(TAG, "response raw=$response")
                Log.d(TAG, "response city=${weatherData.city}")

                callback(weatherData)

            } catch (e: Exception) {
                e.printStackTrace()
                callback(null)
            }
        }.start()
    }

    /**
     * 拼接天气 API 地址
     */
    private fun buildWeatherUrl(city: String?): String {
        val urlBuilder = StringBuilder(
            "$BASE_URL" +
                    "?unescape=1" +
                    "&version=$VERSION" +
                    "&appid=$APP_ID" +
                    "&appsecret=$APP_SECRET"
        )

        if (!city.isNullOrBlank()) {
            val cleanCity = city.replace("市", "").trim()
            val encodedCity = URLEncoder.encode(cleanCity, "UTF-8")
            urlBuilder.append("&city=$encodedCity")
        }

        return urlBuilder.toString()
    }

    /**
     * 把服务器返回的 JSON 解析成 WeatherData
     */
    private fun parseWeatherData(response: String): WeatherData {
        val json = JSONObject(response)

        return WeatherData(
            city = json.optString("city"),
            temp = json.optString("tem"),
            wea = json.optString("wea"),
            max = json.optString("tem1"),
            min = json.optString("tem2")
        )
    }
}