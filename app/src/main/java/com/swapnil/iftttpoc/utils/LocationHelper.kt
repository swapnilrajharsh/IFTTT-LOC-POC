package com.swapnil.iftttpoc.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat

class LocationHelper {
    private val TAG = "SWAPNILIFTTT"
    var LOCATION_REFRESH_TIME = 3000 // 3 seconds. The Minimum Time to get location update
    var LOCATION_REFRESH_DISTANCE =
        0 // 0 meters. The Minimum Distance to be changed to get location update

    fun startListeningUserLocation(context: Context, myListener: MyLocationListener) {
        Log.d(TAG, "Location Helper")

        val mLocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                Log.d(TAG, "Location Helper - 1")
                myListener.onLocationChanged(location) // calling listener to inform that updated location is available
            }
            override fun onProviderEnabled(provider: String) {
                Log.d(TAG, "Location Helper - 2 $provider")
            }
            override fun onProviderDisabled(provider: String) {
                Log.d(TAG, "Location Helper - 3 $provider")
            }
            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
                Log.d(TAG, "Location Helper - 4")
            }
        }
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "Don't have the permission")
            return
        }
        Log.d(TAG, "Has the permission")
        mLocationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            LOCATION_REFRESH_TIME.toLong(),
            LOCATION_REFRESH_DISTANCE.toFloat(),
            locationListener
        )
    }
}
interface MyLocationListener {
    fun onLocationChanged(location: Location?)
}
