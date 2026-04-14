package com.example.xiaomingassistant.location

import android.content.Context
import android.util.Log
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener

class LocationRepository(context: Context) {

    private val appContext = context.applicationContext
    private var locationClient: AMapLocationClient? = null

    data class LocationResult(
        val city: String,
        val district: String?,
        val latitude: Double,
        val longitude: Double,
        val address: String?
    )

    fun locateOnce(
        onSuccess: (LocationResult) -> Unit,
        onError: (String) -> Unit
    ) {
        destroy()

        locationClient = AMapLocationClient(appContext)

        val option = AMapLocationClientOption().apply {
            locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            isOnceLocation = true
            isNeedAddress = true
            isMockEnable = false
            httpTimeOut = 10000
        }

        locationClient?.setLocationOption(option)

        locationClient?.setLocationListener(object : AMapLocationListener {
            override fun onLocationChanged(location: AMapLocation?) {
                if (location == null) {
                    Log.e("LOC", "location is null")
                    onError("定位结果为空")
                    destroy()
                    return
                }

                Log.d("LOC", "errorCode=${location.errorCode}")
                Log.d("LOC", "errorInfo=${location.errorInfo}")
                Log.d("LOC", "city=${location.city}")
                Log.d("LOC", "district=${location.district}")
                Log.d("LOC", "address=${location.address}")
                Log.d("LOC", "lat=${location.latitude}, lng=${location.longitude}")

                if (location.errorCode == 0) {
                    onSuccess(
                        LocationResult(
                            city = location.city ?: "",
                            district = location.district,
                            latitude = location.latitude,
                            longitude = location.longitude,
                            address = location.address
                        )
                    )
                } else {
                    onError("定位失败：${location.errorInfo} (${location.errorCode})")
                }

                destroy()
            }
        })

        locationClient?.startLocation()
    }

    fun destroy() {
        locationClient?.stopLocation()
        locationClient?.onDestroy()
        locationClient = null
    }
}