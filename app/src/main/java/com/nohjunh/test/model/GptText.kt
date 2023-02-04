package com.nohjunh.test.model

import com.google.gson.annotations.SerializedName

data class GptText(
    @SerializedName("text")
    val text : String
)
