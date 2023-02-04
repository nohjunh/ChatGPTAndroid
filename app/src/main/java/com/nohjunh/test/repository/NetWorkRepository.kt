package com.nohjunh.test.repository

import com.google.gson.JsonObject
import com.nohjunh.test.network.Apis
import com.nohjunh.test.network.RetrofitInstance
import org.json.JSONObject

class NetWorkRepository {

    private val chatGPTClient = RetrofitInstance.getInstance().create(Apis::class.java)

    suspend fun postResponse(jsonData : JsonObject) = chatGPTClient.postRequest(jsonData)
}