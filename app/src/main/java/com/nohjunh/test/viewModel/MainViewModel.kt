package com.nohjunh.test.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.nohjunh.test.BuildConfig
import com.nohjunh.test.R
import com.nohjunh.test.database.entity.ContentEntity
import com.nohjunh.test.model.event.SteamDataEvent
import com.nohjunh.test.repository.DatabaseRepository
import com.nohjunh.test.repository.NetWorkRepository
import com.nohjunh.test.repository.SpRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import kotlin.random.Random

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

    private var _chatGptNewMsg = MutableLiveData<ContentEntity>()
    val chatGptNewMsg : LiveData<ContentEntity>
        get() = _chatGptNewMsg

    private val gson by lazy { Gson() }
    var sendBySteam = false

    init {
        _showDeleteGuide.postValue(spRepository.isFirstOpen())
        val token = spRepository.getToken()
        if (token.isNotEmpty()) {
            netWorkRepository.setToken(token)
        } else {
            _chatGptNewMsg.value = databaseRepository.insertContent(application.getString(R.string.api_key_empty_error), ContentEntity.Gpt, ContentEntity.TYPE_SYSTEM)
        }
        sendBySteam = spRepository.getSendBySteam()
    }

    fun postResponse(query: String) = viewModelScope.launch {
        val jsonObj = mutableMapOf<String, Any>()

        jsonObj["model"] = "gpt-3.5-turbo-0301"
        jsonObj["messages"] = listOf(mapOf("role" to "user", "content" to query))
        jsonObj["temperature"] = 1
        jsonObj["max_tokens"] = 1000
        jsonObj["top_p"] = 1
        jsonObj["stream"] = sendBySteam

        withContext(Dispatchers.IO) {
            val body = gson.toJson(jsonObj).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            if (sendBySteam) {
//                if (BuildConfig.DEBUG) {
//                    val text = "我是一个能够为您提供智能化语言交互服务的AI助手。我被称为OpenAI GPT-3，可以帮助您回答各种问题、提供不同主题的建议和信息等。"
//                    delay(Random.nextLong(1000, 2000))
//                    EventBus.getDefault().post(SteamDataEvent("", SteamDataEvent.TYPE_STEAM_START))
//                    for (c in text.toCharArray()) {
//                        EventBus.getDefault().post(SteamDataEvent(c.toString(), SteamDataEvent.TYPE_STEAM))
//                        delay(Random.nextLong(50, 500))
//                    }
//                    EventBus.getDefault().post(SteamDataEvent("", SteamDataEvent.TYPE_STEAM_END))
//                } else {
                    netWorkRepository.sendSteamRequest(body)
//                }
            } else {
                _showLoading.postValue(true)
                val msg = try {
                    val response = netWorkRepository.postResponse(body)
                    if (response.choices.isEmpty()) {
                        val content = getApplication<Application>().getString(R.string.chat_error_no_answers)
                        databaseRepository.insertContent(content, ContentEntity.Gpt, ContentEntity.TYPE_SYSTEM)
                    } else {
                        val content = response.choices.first().message.content
                        databaseRepository.insertContent(content, ContentEntity.Gpt, ContentEntity.TYPE_CONVERSATION)
                    }
                } catch (e: Exception) {
                    Timber.e(e)
                    val content = getApplication<Application>().getString(R.string.chat_error_catch) + e.message
                    databaseRepository.insertContent(content, ContentEntity.Gpt, ContentEntity.TYPE_SYSTEM)
                }
                _chatGptNewMsg.postValue(msg)
                _showLoading.postValue(false)
            }
        }
        _showLoading.postValue(false)
    }

    fun getContentData() = viewModelScope.launch(Dispatchers.IO) {
        _contentList.postValue(databaseRepository.getContentData())
        _deleteCheck.postValue(false)
        _gptInsertCheck.postValue(false)
    }

    fun insertContent(content: String, gptOrUser: Int, type: Int = ContentEntity.TYPE_CONVERSATION, addToView: Boolean = true) = viewModelScope.launch(Dispatchers.IO) {
        val insertContent = databaseRepository.insertContent(content, gptOrUser, type)
        if (addToView) {
            _chatGptNewMsg.postValue(insertContent)
        }
    }

    fun deleteSelectedContent(id: Int) = viewModelScope.launch(Dispatchers.IO) {
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