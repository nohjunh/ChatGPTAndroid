package com.nohjunh.test.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.nohjunh.test.R
import com.nohjunh.test.database.entity.ContentEntity
import com.nohjunh.test.repository.DatabaseRepository
import com.nohjunh.test.repository.NetWorkRepository
import com.nohjunh.test.repository.SpRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val databaseRepository = DatabaseRepository()
    private val netWorkRepository = NetWorkRepository()
    private val spRepository = SpRepository(application)

    private var _contentList = MutableLiveData<List<ContentEntity>>()
    val contentList : LiveData<List<ContentEntity>>
        get() = _contentList

    private var _deleteCheck = MutableLiveData<Boolean>(false)
    val deleteCheck : LiveData<Boolean>
        get() = _deleteCheck

    private var _gptInsertCheck = MutableLiveData<Boolean>(false)
    val gptInsertCheck : LiveData<Boolean>
        get() = _gptInsertCheck

    private var _showLoading = MutableLiveData(false)
    val showLoading : LiveData<Boolean>
        get() = _showLoading

    private var _showDeleteGuide = MutableLiveData(false)
    val showDeleteGuide : LiveData<Boolean>
        get() = _showDeleteGuide

    private val gson by lazy { Gson() }

    init {
        _showDeleteGuide.postValue(spRepository.isFirstOpen())
        val token = spRepository.getToken()
        if (token.isNotEmpty()) {
            netWorkRepository.setToken(token)
        } else {
            insertContent(application.getString(R.string.api_key_empty_error), 1)
        }
    }

    fun postResponse(query : String) = viewModelScope.launch {
        val jsonObj = mutableMapOf<String, Any>()

        jsonObj["model"] = "gpt-3.5-turbo-0301"
        jsonObj["messages"] = listOf(mapOf("role" to "user", "content" to query))
        jsonObj["temperature"] = 1
        jsonObj["max_tokens"] = 1000
        jsonObj["top_p"] = 1

        _showLoading.postValue(true)
        val message = try {
            val body = gson.toJson(jsonObj).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            val response = netWorkRepository.postResponse(body)
            if (response.choices.isEmpty()) {
                getApplication<Application>().getString(R.string.chat_error_no_answers)
            } else {
                response.choices.first().message.content
            }
        } catch (e : Exception) {
            Timber.e(e)
            getApplication<Application>().getString(R.string.chat_error_catch) + e.message
        }
        _showLoading.postValue(false)
        insertContent(message, 1)
    }

    fun getContentData() = viewModelScope.launch(Dispatchers.IO) {
        _contentList.postValue(databaseRepository.getContentData())
        _deleteCheck.postValue(false)
        _gptInsertCheck.postValue(false)
    }

    fun insertContent(content : String, gptOrUser : Int) = viewModelScope.launch(Dispatchers.IO) {
        databaseRepository.insertContent(content, gptOrUser)
        if (gptOrUser == 1) {
            _gptInsertCheck.postValue(true)
        }
    }

    fun deleteSelectedContent(id : Int) = viewModelScope.launch(Dispatchers.IO) {
        databaseRepository.deleteSelectedContent(id)
        _deleteCheck.postValue(true)
    }

    fun resetFirstOpen() {
        spRepository.setFirstOpen(false)
        _showDeleteGuide.postValue(false)
    }

    fun setupApiKey(text: String) {
        spRepository.setToken(text)
    }

}