package com.example.presstest

import com.google.gson.annotations.SerializedName

data class LoacationData(
    @SerializedName("lat") var lat : Float,
    @SerializedName("lng") var lng: Float
)
