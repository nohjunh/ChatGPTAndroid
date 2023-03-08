package com.nohjunh.test.repository

import com.nohjunh.test.network.Apis
import com.nohjunh.test.network.RetrofitInstance
import okhttp3.RequestBody

class NetWorkRepository {

    private val chatGPTClient by lazy { RetrofitInstance.getInstance().create(Apis::class.java) }

    suspend fun postResponse(jsonData: RequestBody) = chatGPTClient.postRequest(jsonData)

    fun setToken(token: String) {
        RetrofitInstance.token = token
    }
}