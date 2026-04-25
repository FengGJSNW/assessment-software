package com.example.xiaomingassistant.data.model

data class WeatherData(
    val city: String,
    val temp: String,
    val wea: String,
    val max: String,
    val min: String,
    val forecasts: List<WeatherForecast> = emptyList()
)

data class WeatherForecast(
    val date: String,
    val week: String,
    val dayTemp: String,
    val nightTemp: String,
    val dayWeather: String,
    val nightWeather: String,
    val dayWind: String,
    val dayWindSpeed: String
)
