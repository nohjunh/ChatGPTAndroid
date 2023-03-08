package com.nohjunh.test.network

import com.nohjunh.test.model.GptR
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface Apis {
    @POST("v1/chat/completions")
    suspend fun postRequest(@Body json: RequestBody): GptR

}