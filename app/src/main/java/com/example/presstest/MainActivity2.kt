package com.example.presstest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.concurrent.thread

class MainActivity2 : AppCompatActivity() {

    private val btn1: Button by lazy { findViewById(R.id.btn1) }
    private val btn2: Button by lazy { findViewById(R.id.btn2) }

    private val retrofit = Retrofit.Builder().baseUrl("http://parkbomin.iptime.org:18000/")
        .addConverterFactory(GsonConverterFactory.create()).build()
    private val service = retrofit.create(PressureService::class.java)

    private var isChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)





        btn1.setOnClickListener {
            showPressure()
            btn1.isEnabled = false
            btn2.isEnabled = true
        }

        btn2.setOnClickListener {
            btn1.isEnabled = true
            btn2.isEnabled = false
            resetPressure()
        }


    }

    private val mDelayHandler: Handler by lazy {
        Handler()
    }

    private fun waitPressure(){
        mDelayHandler.postDelayed(::showPressure, 5000) // 10초 후에 showGuest 함수를 실행한다.
    }

    private fun resetPressure(){
        mDelayHandler.removeMessages(0)
    }


    private fun showPressure(){
        // 실제 반복하는 코드를 여기에 적는다
        service.getPressure("1")?.enqueue(object : Callback<Pressure>{
            override fun onResponse(call: Call<Pressure>, response: Response<Pressure>) {
                if(response.isSuccessful){
                    // 정상적으로 통신이 성고된 경우
                    var result: Pressure? = response.body()
                    Log.d("YMC", "onResponse 성공: " + result?.toString());
                }else{
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("YMC", "onResponse 실패")
                }
            }

            override fun onFailure(call: Call<Pressure>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("YMC", "onFailure 에러: " + t.message.toString());
            }
        })

        waitPressure() // 코드 실행뒤에 계속해서 반복하도록 작업한다.
    }
}