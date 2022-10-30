package com.example.presstest

import android.graphics.Bitmap
import android.graphics.Matrix
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jusqre.greenpath.util.LocationStore
import com.skt.Tmap.TMapGpsManager
import com.skt.Tmap.TMapMarkerItem
import com.skt.Tmap.TMapPoint
import com.skt.Tmap.TMapView


class MainActivity : AppCompatActivity(), TMapGpsManager.onLocationChangedCallback {

    var API_Key = "l7xxf8a0d7c16d01466a8bae0f6451de98be"

    var speed = 0.0

    private val _map = MutableLiveData<TMapView>()
    val map: LiveData<TMapView>
        get() = _map

    private lateinit var currentPosition: Location


    private val text: TextView by lazy {findViewById(R.id.tvtv)}
    private val text1: TextView by lazy {findViewById(R.id.tvtv1)}
    private val text2: TextView by lazy {findViewById(R.id.tvtv2)}

    lateinit var tMapPointStart: TMapPoint
    lateinit var TMapPointEnd: TMapPoint


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        _map.value = TMapView(this).apply {
            this.setSKTMapApiKey(API_Key)
        }


        map.value?.let {
            setUpGPS()
        }
        LocationStore.lastLocation.observe(this) {
            onLocationUpdated(it)
        }
        initMap()


    }
    private fun initMap() {
        findViewById<FrameLayout>(R.id.tmapViewContainer).addView(map.value)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setUpGPS() {
        val gps = TMapGpsManager(this).apply {
            minTime = 100
            minDistance = 5f
            provider = TMapGpsManager.GPS_PROVIDER
            setLocationCallback()
        }

//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//            && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            requestPermissions(
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                1
//            ) //위치권한 탐색 허용 관련 내용
//        }
        gps.OpenGps()
        map.value?.moveCamera(gps.location.longitude, gps.location.latitude)
    }

    override fun onLocationChange(location: Location) {
        LocationStore.updateLocation(location)
    }

    private fun onLocationUpdated(location: Location) {
        if (!::currentPosition.isInitialized) {
            currentPosition = location
            println("not initialized")
        } else {
            currentPosition = location
        }
        map.value?.let {
            it.moveCamera(location.longitude, location.latitude)
            it.setUserMarker(location)
        }
    }

    private fun TMapView.setUserMarker(location: Location) {
        this.addMarkerItem("currentPosition", TMapMarkerItem().apply {
            longitude = location.longitude
            latitude = location.latitude
        })
    }

    private fun TMapView.moveCamera(longitude: Double, latitude: Double) {
        this.setLocationPoint(longitude, latitude)
        this.setCenterPoint(longitude, latitude)
    }

    private fun Bitmap.rotate(bearing: Float): Bitmap {
        val matrix = Matrix().apply {
            postRotate(bearing)
        }
        return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
    }




}