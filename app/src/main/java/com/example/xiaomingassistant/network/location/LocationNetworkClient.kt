package com.example.xiaomingassistant.data.network.location

import android.content.Context
import android.util.Log
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener

class LocationNetworkClient(context: Context) {

    private val appContext = context.applicationContext
    private var locationClient: AMapLocationClient? = null

    fun locateOnce(
        onSuccess: (AMapLocation) -> Unit,
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
                    Log.e(TAG, "location is null")
                    onError("定位结果为空")
                    destroy()
                    return
                }

                Log.d(TAG, "errorCode=${location.errorCode}")
                Log.d(TAG, "errorInfo=${location.errorInfo}")
                Log.d(TAG, "city=${location.city}")
                Log.d(TAG, "district=${location.district}")
                Log.d(TAG, "address=${location.address}")
                Log.d(TAG, "lat=${location.latitude}, lng=${location.longitude}")

                if (location.errorCode == 0) {
                    onSuccess(location)
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

    companion object {
        private const val TAG = "LocationNetworkClient"
    }
}