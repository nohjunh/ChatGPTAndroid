package com.nohjunh.test.repository

import android.content.Context

class SpRepository(context: Context) {

  private val sp = context.getSharedPreferences("config", Context.MODE_PRIVATE)

  fun isFirstOpen(): Boolean {
    return sp.getBoolean("isFirstOpen", true)
  }

  fun setFirstOpen(isFirstOpen: Boolean) {
    sp.edit().putBoolean("isFirstOpen", isFirstOpen).apply()
  }

  fun getGptModel(): String {
    return sp.getString("gptModel", "gpt-3.5-turbo-0301") ?: "gpt-3.5-turbo-0301"
  }

  fun setGptModel(gptModel: String) {
    sp.edit().putString("gptModel", gptModel).apply()
  }

  fun getToken(): String {
    return sp.getString("token", "") ?: ""
  }

  fun setToken(token: String) {
    sp.edit().putString("token", token).apply()
  }
}