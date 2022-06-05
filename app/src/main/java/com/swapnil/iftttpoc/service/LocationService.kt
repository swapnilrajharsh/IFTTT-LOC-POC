package com.swapnil.iftttpoc.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.swapnil.iftttpoc.R
import com.swapnil.iftttpoc.utils.ApiHelper
import com.swapnil.iftttpoc.utils.LocationHelper
import com.swapnil.iftttpoc.utils.MyLocationListener


class LocationService : Service() {
    private val NOTIFICATION_CHANNEL_ID = "my_notification_location"
    private val TAG = "SWAPNILIFTTT"
    private val DESTINATION = Location("DEST")


    private var INSIDEGEOFENCE = false


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service Started")
        val lat = intent!!.getDoubleExtra("LAT", 0.0)
        val lng = intent.getDoubleExtra("LNG", 0.0)
        val radius = intent.getDoubleExtra("RADIUS", 0.0)
        DESTINATION.latitude = lat
        DESTINATION.longitude = lng

        LocationHelper().startListeningUserLocation(
            this, object : MyLocationListener {
                override fun onLocationChanged(location: Location?) {
                    Log.d(TAG, "Location : Changes")
                    Log.d(TAG, "Location :  ${location!!.latitude} and ${location.longitude} " +
                            " and distance : ${location.distanceTo(DESTINATION)}")
                    if ( location.distanceTo(DESTINATION) < radius && !INSIDEGEOFENCE) {
                        INSIDEGEOFENCE = true
                        ApiHelper.updateGeofenceStatus("jfmSPBkHrSseUg9KJHFkXNimZOnwhXjOVyauk5Krmiw",
                        "999")
                    }
                    mLocation = location
                    /*mLocation?.let {
                    }*/

                }
            })
        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service Started -- Oncreate")
        isServiceStarted = true
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setOngoing(false)
                .setSmallIcon(R.drawable.ic_launcher_background)
        val notificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_LOW
        )
        notificationChannel.description = NOTIFICATION_CHANNEL_ID
        notificationChannel.setSound(null, null)
        notificationManager.createNotificationChannel(notificationChannel)
        startForeground(1, builder.build(), FOREGROUND_SERVICE_TYPE_LOCATION)
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceStarted = false
    }
    companion object {
        var mLocation: Location? = null
        var isServiceStarted = false
    }
}