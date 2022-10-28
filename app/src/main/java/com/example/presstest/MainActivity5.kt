package com.example.presstest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity5 : AppCompatActivity() {

    private val retrofit = Retrofit.Builder().baseUrl("http://ci2022kingsman.dongyangmirae.kr")
        .addConverterFactory(GsonConverterFactory.create()).build()
    private val service = retrofit.create(testInterface::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main5)

        service.test()
            .enqueue(object : Callback<test2> {
                override fun onResponse(call: Call<test2>, response: Response<test2>) {
                    if (response.isSuccessful) {
                        if (response.code() == 200) {

                            var result: test2? = response.body()
                            Log.d("YMC", "onResponse 성공: " + result?.usan2?.get(0)?.id.toString())
                            Log.d("YMC", "onResponse 성공: " + result?.usan2?.get(0)?.u_status1.toString())
                            Log.d("YMC", "onResponse 성공: " + result?.usan2?.get(0)?.u_status2.toString());
                            if(result?.usan2?.get(0)?.u_status1 != null){
                                Toast.makeText(this@MainActivity5, "안녕 반납 됩니다", Toast.LENGTH_SHORT).show()
                            }else{
                                Toast.makeText(this@MainActivity5, "안녕 반납 안됩니다", Toast.LENGTH_SHORT).show()
                            }

                        }
                    }
                }

                override fun onFailure(call: Call<test2>, t: Throwable) {
                    Log.e("RETRO_ERR", "Error")
                }
            })
    }
}