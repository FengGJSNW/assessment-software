package com.example.xiaomingassistant.data.repository

import android.util.Log
import com.example.xiaomingassistant.data.model.WeatherData
import com.example.xiaomingassistant.data.model.WeatherForecast
import com.example.xiaomingassistant.data.network.weather.WeatherNetworkClient
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.time.LocalDate

class WeatherRepository(
    private val networkClient: WeatherNetworkClient = WeatherNetworkClient()
) {

    companion object {
        private const val TAG = "WeatherRepositroy.kt"

        private const val BASE_URL = "http://pddfps.tianqiapi.com/api"
        private const val VERSION = "v93"
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
        val dayJson = json.optJSONObject("day")
        val forecasts = parseForecasts(json.optJSONArray("week"))
        val firstForecast = forecasts.firstOrNull()

        return WeatherData(
            city = json.optString("city"),
            temp = dayJson?.optString("tem").orEmpty().ifBlank { json.optString("tem") },
            wea = dayJson?.optString("wea").orEmpty().ifBlank { json.optString("wea") },
            max = firstForecast?.dayTemp.orEmpty().ifBlank { json.optString("tem1") },
            min = firstForecast?.nightTemp.orEmpty().ifBlank { json.optString("tem2") },
            forecasts = forecasts
        )
    }

    // 只取未来三天；如果接口把今天也放进 week 里，就先跳过今天
    private fun parseForecasts(weekArray: JSONArray?): List<WeatherForecast> {
        if (weekArray == null) return emptyList()

        val today = LocalDate.now().toString()
        val list = mutableListOf<WeatherForecast>()

        for (index in 0 until weekArray.length()) {
            val item = weekArray.optJSONObject(index) ?: continue
            val date = item.optString("date")
            if (date == today) continue

            list.add(
                WeatherForecast(
                    date = date,
                    week = item.optString("week"),
                    dayTemp = item.optString("day_tem"),
                    nightTemp = item.optString("night_tem"),
                    dayWeather = item.optString("day_wea"),
                    nightWeather = item.optString("night_wea"),
                    dayWind = item.optString("day_win"),
                    dayWindSpeed = item.optString("day_win_speed")
                )
            )

            if (list.size == 3) break
        }

        return list
    }
}
