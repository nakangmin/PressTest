package com.example.presstest


import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface LocationService {
    @POST("/sensor/phone/")
    @Headers("accept: application/json", "content-type: application/json")
    fun locationPost(
        @Body locationTest : LoacationData
    ) : Call<LoacationData>
}