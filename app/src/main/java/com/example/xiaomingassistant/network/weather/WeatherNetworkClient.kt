package com.example.xiaomingassistant.data.network.weather

import java.net.HttpURLConnection
import java.net.URL

class WeatherNetworkClient {

    fun get(urlString: String): String {
        var connection: HttpURLConnection? = null

        try {
            // 连接服务器
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection

            // 协议
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            // 检查服务器回应代码
            val responseCode = connection.responseCode

            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw RuntimeException("天气网络请求失败，responseCode=$responseCode")
            }

            return connection.inputStream
                .bufferedReader(Charsets.UTF_8)
                .use { it.readText() }

        } finally {
            connection?.disconnect()
        }
    }
}