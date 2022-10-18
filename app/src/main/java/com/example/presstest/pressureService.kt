package com.example.presstest

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PressureService {
    @GET("sensor/holder")
    fun getPressure(
        @Query("pressure") pressure: String
    ): Call<Pressure>



}