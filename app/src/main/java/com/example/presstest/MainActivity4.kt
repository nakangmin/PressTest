package com.example.presstest

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.reactivex.plugins.RxJavaPlugins.reset
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow
import kotlin.time.ExperimentalTime


class MainActivity4 : AppCompatActivity(), LocationListener {
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private val INTERVAL: Long = 2000
    private val FASTEST_INTERVAL: Long = 1000
    private var mLastLocation: Location? = null
    internal lateinit var mLocationRequest: LocationRequest
    private val REQUEST_PERMISSION_LOCATION = 10

    private var time = 0
    private var isRunning = false
    private var timerTask: Timer? = null
    private var index :Int = 1
    private var i:Int = 0


    private val tvSpeed: TextView by lazy { findViewById(R.id.tvSpeed) }
    private val tvRidingTime: TextView by lazy { findViewById(R.id.tvRidingTime) }
    private val tvKcal: TextView by lazy { findViewById(R.id.tvKcal) }
    private val btnStartRiding: FloatingActionButton by lazy { findViewById(R.id.btnStartRiding) }
    private val btnStopRiding: FloatingActionButton by lazy { findViewById(R.id.btnStopRiding) }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)

        mLocationRequest = LocationRequest()


        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        }


        btnStartRiding.setOnClickListener {
            if (checkPermissionForLocation(this))
            {
                startLocationUpdates()
                start()
                btnStartRiding.isEnabled = false
                btnStopRiding.isEnabled = true
            }
        }

        btnStopRiding.setOnClickListener {
            stoplocationUpdates()
            pause()
            btnStartRiding.isEnabled = true
            btnStopRiding.isEnabled = false

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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        mFusedLocationProviderClient!!.requestLocationUpdates(
            mLocationRequest!!, mLocationCallback,
            Looper.myLooper())
    }


    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // do work here
            locationResult.lastLocation
            locationResult.lastLocation?.let { onLocationChanged(it) }
        }
    }


    @OptIn(ExperimentalTime::class)
    override fun onLocationChanged(location: Location) {



    }

    private fun stoplocationUpdates() {
        mFusedLocationProviderClient!!.removeLocationUpdates(mLocationCallback)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
                btnStartRiding.isEnabled = false
                btnStopRiding.isEnabled = true
            } else {
                Toast.makeText(this@MainActivity4, "Permission Denied", Toast.LENGTH_SHORT).show()
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

    private fun start() {

        timerTask = kotlin.concurrent.timer(period = 10) {
            time++
            var sec = time / 100
            runOnUiThread {

                if (sec == 6){
                    sec = 0
                    time = 0
                    i++
                }
                val min = i
                tvRidingTime.text = " $min 분 $sec 초"
            }
        }
    }

    private fun pause() {
        timerTask?.cancel();
    }

    private fun reset() {
        timerTask?.cancel()

        time = 0
        isRunning = false

        tvRidingTime.text = "0"

        index = 1
    }

    private fun lapTime() {
        val lapTime = time
        val textView = TextView(this).apply {
            setTextSize(20f)
        }
        textView.text = "${lapTime / 100}.${lapTime % 100}"

        index++
    }



}