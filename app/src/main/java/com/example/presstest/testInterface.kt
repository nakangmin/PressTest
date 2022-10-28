package com.example.presstest

import retrofit2.Call
import retrofit2.http.Headers
import retrofit2.http.POST

interface testInterface {
    @POST("/ksj/ex.php")
    @Headers("accept: application/json", "content-type: application/json")
    fun test(): Call<test2>
}