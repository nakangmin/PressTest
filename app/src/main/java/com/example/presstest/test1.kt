package com.example.presstest

import com.google.gson.annotations.SerializedName

data class test1(
    @SerializedName("id") var id : Int,
    @SerializedName("u_status1") var u_status1 : String,
    @SerializedName("u_status2") var u_status2 : String

)
data class test2(
    @SerializedName("usan2") var usan2: List<test1>
)
