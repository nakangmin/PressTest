package com.example.presstest

import com.google.gson.annotations.SerializedName

data class Pressure(
    @SerializedName("pressure") var pressure : Float

)
