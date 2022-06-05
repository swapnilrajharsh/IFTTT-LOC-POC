package com.swapnil.iftttpoc.utils

import android.util.Log
import com.squareup.moshi.Moshi
import com.swapnil.iftttpoc.UserAuthenticationListener
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

object ApiHelper {
    private lateinit var userDataTokenApi: UserDataTokenApi
    private lateinit var userAuthenticationListener: UserAuthenticationListener

    init {
        val BASE_URL = "https://20db-2405-201-a416-d981-c09a-c30a-cab3-45ed.ngrok.io"
        val client = OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
        val moshi = Moshi.Builder().build()
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        userDataTokenApi = retrofit.create(UserDataTokenApi::class.java)
    }

    fun registerUserAuthenticationListener(userAuthenticationListener: UserAuthenticationListener) {
        this.userAuthenticationListener = userAuthenticationListener
    }



    fun updateUserSpecificDeviceId(oauthcode : String) {
        val response = userDataTokenApi.updateDeviceId(DeviceId(oauthcode))

        response.enqueue(object : Callback<CoordData>{
            override fun onResponse(
                call: Call<CoordData>,
                response: Response<CoordData>
            ) {
                if (!response.isSuccessful()) {
                    Log.d("IFTTT", "Not successful response")
                } else {
                    Log.d("IFTTT", "Response Successful and data received is " +
                            "${response.body()!!.lat}")
                    userAuthenticationListener.authenticationDone(response.body()!!)
                }
            }

            override fun onFailure(call: Call<CoordData>, t: Throwable) {
                Log.d("IFTTT", "Update Failed")
            }
        })
    }

    fun updateGeofenceStatus(oauthcode: String, data: String) {
        val response = userDataTokenApi.updateGeofenceStatus(
            GeofenceUpdate(
            oauthcode, data
        )
        )

        response.enqueue(object : Callback<Status>{
            override fun onResponse(call: Call<Status>, response: Response<Status>) {
                if (!response.isSuccessful()) {
                    Log.d("IFTTT", "Not successful response")
                } else {
                    Log.d("IFTTT", "Response Successful and data received is " +
                            "${response.body()!!.status}")
                }
            }

            override fun onFailure(call: Call<Status>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private interface UserDataTokenApi {

        @POST("/showconnection/details")
        fun updateDeviceId( @Body deviceid : DeviceId ) : Call<CoordData>

        @POST("/updategeofencestatus")
        fun updateGeofenceStatus( @Body geofenceUpdate: GeofenceUpdate ) : Call<Status>
    }

    class CoordData(val lat: Double, val lng: Double, val radius: Double)

    private class DeviceId(val oauthcode: String)
    private class GeofenceUpdate(val oauthcode: String, val data: String)
    private class Status(val status: String)
}