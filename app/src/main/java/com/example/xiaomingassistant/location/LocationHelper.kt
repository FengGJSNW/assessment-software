package com.example.xiaomingassistant.location

import android.content.Context
import com.amap.api.location.AMapLocation
import com.example.xiaomingassistant.data.model.LocationResult
import com.example.xiaomingassistant.data.network.location.LocationNetworkClient

class LocationHelper(context: Context) {

    private val locationNetworkClient = LocationNetworkClient(context)

    fun locateOnce(
        onSuccess: (LocationResult) -> Unit,
        onError: (String) -> Unit
    ) {
        locationNetworkClient.locateOnce(
            onSuccess = { amapLocation ->
                onSuccess(amapLocation.toLocationResult())
            },
            onError = { errorMessage ->
                onError(errorMessage)
            }
        )
    }

    fun destroy() {
        locationNetworkClient.destroy()
    }

    /*
     * 解析定位
     **/
    private fun AMapLocation.toLocationResult(): LocationResult {
        return LocationResult(
            city = city ?: "",
            district = district,
            latitude = latitude,
            longitude = longitude,
            address = address
        )
    }
}