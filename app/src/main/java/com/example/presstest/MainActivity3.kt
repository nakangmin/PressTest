package com.example.presstest

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Math.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow
import kotlin.time.ExperimentalTime




@Suppress("UNNECESSARY_SAFE_CALL")
class MainActivity3 : AppCompatActivity(), LocationListener {
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private val INTERVAL: Long = 2000
    private val FASTEST_INTERVAL: Long = 1000
    lateinit var mLastLocation: Location
    internal lateinit var mLocationRequest: LocationRequest
    private val REQUEST_PERMISSION_LOCATION = 10

    private val btnStartUpdates: Button by lazy { findViewById(R.id.btn_start_upds) }
    private val btnStopUpdates: Button by lazy { findViewById(R.id.btn_stop_upds) }
    private val txtLat: TextView by lazy { findViewById(R.id.txtLat) }
    private val txtLong: TextView by lazy { findViewById(R.id.txtLong) }
    private val txtTime: TextView by lazy { findViewById(R.id.txtTime) }

    private val txtSpeed: TextView by lazy {findViewById(R.id.txtSpeed)}
    private val txtRidingTime: TextView by lazy { findViewById(R.id.txtRidingTime) }
    private val txtDistance: TextView by lazy { findViewById(R.id.txtDistance) }
    private val txtKcal: TextView by lazy { findViewById(R.id.txtKcal) }

    private var timerTask: Timer? = null
    private var time = 0
    private var isRunning = false


    private var speed = 0



    var theta = 0.0
    var dist:kotlin.Double = 0.0
    var bef_lat = 0.0
    var bef_long:kotlin.Double = 0.0
    var cur_lat:kotlin.Double = 0.0
    var cur_long:kotlin.Double = 0.0


    private val retrofit = Retrofit.Builder().baseUrl("http://parkbomin.iptime.org:18000/")
        .addConverterFactory(GsonConverterFactory.create()).build()
    private val service = retrofit.create(LocationService::class.java)




    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        mLocationRequest = LocationRequest()




        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        }


        btnStartUpdates.setOnClickListener {
            if (checkPermissionForLocation(this)) {
                startLocationUpdates()
                ridingStart()
                btnStartUpdates.isEnabled = false
                btnStopUpdates.isEnabled = true
            }
        }

        btnStopUpdates.setOnClickListener {
            stoplocationUpdates()
            reset()
            txtTime.text = "Updates Stoped"
            btnStartUpdates.isEnabled = true
            btnStopUpdates.isEnabled = false
        }

    }

    private fun buildAlertMessageNoGps() {

        val builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                startActivityForResult(
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    , 11)
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.cancel()
                finish()
            }
        val alert: AlertDialog = builder.create()
        alert.show()


    }


    protected fun startLocationUpdates() {

        // Create the location request to start receiving updates

        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest!!.setInterval(INTERVAL)
        mLocationRequest!!.setFastestInterval(FASTEST_INTERVAL)

        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        val locationSettingsRequest = builder.build()

        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            return
        }
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback,
            Looper.myLooper())
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // do work here
            locationResult.lastLocation
            locationResult.lastLocation?.let { onLocationChanged(it) }
        }
    }





    private fun ridingStart(){
        timerTask = kotlin.concurrent.timer(period = 10){
            time++
            val sec = time /100
            val milli = time % 100

            runOnUiThread {
                txtRidingTime.text = "$sec"
            }
        }
    }
//    private fun ridingStop(){
//        if ()
//    }
    private fun reset() {
        timerTask?.cancel() // timerTask가 null이 아니라면 cancel() 호출

        time = 0 // 시간저장 변수 초기화
        isRunning = false // 현재 진행중인지 판별하기 위한 Boolean변수 false 세팅
        txtRidingTime.text = "0" // 시간(초) 초기화
//        milliText.text = "00" // 시간(밀리초) 초기화

//        startBtn.text ="시작"
//        lap_Layout.removeAllViews() // Layout에 추가한 기록View 모두 삭제
//        index = 1
    }



    @OptIn(ExperimentalTime::class)
    override fun onLocationChanged(location: Location) {
        // New location has now been determined
        mLastLocation = location

        val date: Date = Calendar.getInstance().time
        val sdf = SimpleDateFormat("hh:mm:ss a")
        txtTime.text = "Updated at : " + sdf.format(date)
        txtLat.text = "LATITUDE : " + mLastLocation.latitude
        txtLong.text = "LONGITUDE : " + mLastLocation.longitude
        // You can now create a LatLng Object for use with maps

        var getSpeed: String = String.format("%.3f", location.speed)
        txtSpeed.setText(getSpeed)







        val data : LoacationData = LoacationData(mLastLocation.latitude.toFloat(), mLastLocation.longitude.toFloat())

        service.locationPost(data)
            .enqueue(object : Callback<LoacationData> {
                override fun onResponse(call: Call<LoacationData>, response: Response<LoacationData>) {
                    if (response.isSuccessful) {
                        if (response.code() == 200) {

                            var result: LoacationData? = response.body()
                            Log.d("YMC", "onResponse 성공: " + result?.toString());

                        }
                    }
                }

                override fun onFailure(call: Call<LoacationData>, t: Throwable) {
                    Log.e("RETRO_ERR", "Error")
                }
            })


    }

    private fun stoplocationUpdates() {
        mFusedLocationProviderClient!!.removeLocationUpdates(mLocationCallback)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
                btnStartUpdates.isEnabled = false
                btnStopUpdates.isEnabled = true
            } else {
                Toast.makeText(this@MainActivity3, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }



    fun checkPermissionForLocation(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                // Show the permission request
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSION_LOCATION)
                false
            }
        } else {
            true
        }
    }

    object DistanceManager {

        private const val R = 6372.8 * 1000

        /**
         * 두 좌표의 거리를 계산한다.
         *
         * @param lat1 위도1
         * @param lon1 경도1
         * @param lat2 위도2
         * @param lon2 경도2
         * @return 두 좌표의 거리(m)
         */
        fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Int {
            val dLat = Math.toRadians(lat2 - lat1)
            val dLon = Math.toRadians(lon2 - lon1)
            val a = sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2))
            val c = 2 * asin(sqrt(a))
            return (R * c).toInt()
        }
    }



}



