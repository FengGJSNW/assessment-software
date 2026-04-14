package com.example.xiaomingassistant.data.model

data class LocationResult (
    val city: String,
    val district: String?,
    val latitude: Double,
    val longitude: Double,
    val address: String?
)