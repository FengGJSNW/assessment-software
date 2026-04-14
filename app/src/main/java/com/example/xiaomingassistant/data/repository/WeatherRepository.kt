package com.example.xiaomingassistant.data.repository

import android.util.Log
import com.example.xiaomingassistant.data.model.WeatherData
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class WeatherRepository {

    // 获取天气
    fun getWeather(
        city: String? = null,
        callback: (WeatherData?) -> Unit
    ) {
        Thread {
            var connection: HttpURLConnection? = null
            try {
                // api对接网站拼接
                val baseUrl = StringBuilder(
                    "http://pddfps.tianqiapi.com/api" +
                            "?unescape=1" +
                            "&version=v63" +
                            "&appid=14342518" +
                            "&appsecret=D0qMb6tt"
                )

                if (!city.isNullOrBlank()) {
                    val cleanCity = city.replace("市", "").trim()
                    val encodedCity = URLEncoder.encode(cleanCity, "UTF-8")
                    baseUrl.append("&city=$encodedCity")
                }

                Log.d("WEATHER", "request city=$city")
                Log.d("WEATHER", "request url=$baseUrl")

                val url = URL(baseUrl.toString())
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val response = connection.inputStream
                    .bufferedReader(Charsets.UTF_8)
                    .use { it.readText() }

                val json = JSONObject(response)

                Log.d("WEATHER", "response city=${json.optString("city")}")
                Log.d("WEATHER", "response raw=$response")

                val data = WeatherData(
                    city = json.optString("city"),
                    temp = json.optString("tem"),
                    wea = json.optString("wea"),
                    max = json.optString("tem1"),
                    min = json.optString("tem2")
                )

                callback(data)
            } catch (e: Exception) {
                e.printStackTrace()
                callback(null)
            } finally {
                connection?.disconnect()
            }
        }.start()
    }
}