package com.swapnil.iftttpoc

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ifttt.connect.ui.ConnectButton
import com.ifttt.connect.ui.ConnectResult
import com.ifttt.connect.ui.CredentialsProvider
import com.swapnil.iftttpoc.service.LocationService
import com.swapnil.iftttpoc.utils.ApiHelper

class MainActivity : AppCompatActivity() {
    private lateinit var connectButton : ConnectButton
    private lateinit var credentialsProvider: CredentialsProvider
    private val TAG = "IFTTTACT"

    companion object{
        private val REDIRECT_URI : Uri= Uri.parse("iftttpoc://connectcallback")
        private val CONNECTION_ID: String = "YUqhEZ6p"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Check & Request Permission to Location
        if (!checkPermission()) {
            Log.d(TAG, "Requesting Permission")
            requestPermission()
        } else {
            Log.d(TAG, "Has Permission")
            //startLocationService()
        }

        connectButton = findViewById(R.id.connect_button)
        setUpCredentialsProvider()
        val configuration =
            ConnectButton.Configuration.newBuilder("swapnilrajharsh@gmail.com", REDIRECT_URI)
                .withConnectionId(CONNECTION_ID)
                .withCredentialProvider(credentialsProvider)
                .setOnFetchCompleteListener(fetchCompleteListener)
                .build()
        /*QSUwqKTF*/
        /*fWj4fxYg*/
        Log.d("SALPHA", "P3")
        connectButton.setup(configuration)
        Log.d("SALPHA", "P4")

        ApiHelper.registerUserAuthenticationListener(object : UserAuthenticationListener{
            override fun authenticationDone(coordData: ApiHelper.CoordData) {
                val lat: Double =  coordData.lat/*26.11716807743129*/
                val lng:Double =  coordData.lng/*85.38506227549782*/
                val radius: Double = coordData.radius/*99.2161946679141*/
                Log.d("IFTTTACT", "---   $lat + $lng + $radius")
                startLocationService(lat, lng, radius)
            }

            override fun authenticationUnsuccessful(error: String) {
                TODO("Not yet implemented")
            }
        })
    }

    private val fetchCompleteListener = ConnectButton.OnFetchConnectionListener {
        Log.d("SALPHA", "${it.name} and status : ${it.status}" )
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)
        val result1 = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION)
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }
    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 2703)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 2703 ) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Got the Permission")
                //startLocationService()
            }
        }
    }

    private fun startLocationService(lat : Double, lng : Double, radius : Double) {
        Log.d(TAG, "Starting Service")
        val intent = Intent(this, LocationService::class.java)
        intent.putExtra("LAT", lat)
        intent.putExtra("LNG", lng)
        intent.putExtra("RADIUS", radius)
        ContextCompat.startForegroundService(this, intent)
    }

    private fun setUpCredentialsProvider() {
        credentialsProvider = object : CredentialsProvider {
            override fun getUserToken(): String? {
                Log.d("SALPHA", "P0")
                return null
            }
            /*jBHBKURAPZEGpnW5HYBrF2blHgBbG3RBsoJN2R5nq-F*/

            override fun getOAuthCode(): String {
                Log.d("SALPHA", "P1")
                /*return "jBHBKURAPZEGpnW5HYBrF2blHgBbG3RBsoJN2R5nq-F"*/
                return "swapnilrajharsh@gmail.com"
            }

        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val connectResult = ConnectResult.fromIntent(intent)
        if ( connectResult != null ) {
            Log.d("SALPHA", "IF 1 ${connectResult.userToken}")
            // Start Service
            ApiHelper.updateUserSpecificDeviceId("jfmSPBkHrSseUg9KJHFkXNimZOnwhXjOVyauk5Krmiw")

        } else {
            Log.d("SALPHA", "NO RESULT")
        }
        connectButton.setConnectResult(connectResult)
    }
}

interface UserAuthenticationListener {
    fun authenticationDone(coordData: ApiHelper.CoordData)
    fun authenticationUnsuccessful(error: String)
}