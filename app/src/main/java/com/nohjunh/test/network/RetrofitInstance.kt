package com.nohjunh.test.network

import com.google.gson.Gson
import com.nohjunh.test.BuildConfig
import com.nohjunh.test.model.SteamRsp
import com.nohjunh.test.model.event.SteamDataEvent
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import org.greenrobot.eventbus.EventBus
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit


object RetrofitInstance {
    private var okHttpClient = OkHttpClient
        .Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(1, TimeUnit.MINUTES)
        .writeTimeout(1, TimeUnit.MINUTES)
        .addInterceptor(HttpLoggingInterceptor {
            if (BuildConfig.DEBUG) {
                Timber.d(it)
            }
        }.apply {
            level = HttpLoggingInterceptor.Level.HEADERS
        })
        .addInterceptor { chain ->
            val request = chain.request()
            val newRequest = request.newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(newRequest)
        }
        .build()

    private const val BASE_URL = "https://api.openai.com/"

    const val CHAT_URL_PATH = "v1/chat/completions"

    fun getSteamRequest(): Request.Builder {
        return Request.Builder()
            .url(BASE_URL + CHAT_URL_PATH)
    }

    var token = ""

    private val client = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getInstance() : Retrofit {
        return client
    }

    private val eventFactory by lazy { EventSources.createFactory(okHttpClient) }
    private val gson by lazy { Gson() }

    private val sseListener by lazy {
        object : EventSourceListener() {
            override fun onClosed(eventSource: EventSource) {
                Timber.d("Event close ${eventSource.request().url}")
            }

            override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                super.onEvent(eventSource, id, type, data)
                Timber.d("Event data $data id $id type $type")
                if (data == "[DONE]") {
                    EventBus.getDefault().post(SteamDataEvent("", SteamDataEvent.TYPE_STEAM_END))
                } else {
                    val streamRsp = gson.fromJson(data, SteamRsp::class.java)
                    if (streamRsp.choices.isEmpty()) return
                    val delta = streamRsp.choices.first().delta
                    val content = delta.content
                    if (content.isEmpty()) return
                    EventBus.getDefault().post(SteamDataEvent(content, if(delta.role.isEmpty()) SteamDataEvent.TYPE_STEAM else SteamDataEvent.TYPE_STEAM_START))
                }
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                super.onFailure(eventSource, t, response)
                Timber.d("Event fail ${eventSource.request().url} $t $response")
            }

            override fun onOpen(eventSource: EventSource, response: Response) {
                super.onOpen(eventSource, response)
                Timber.d("Event open ${eventSource.request().url}")
            }
        }
    }

    fun sendRequestSteam(request: Request) {
        eventFactory.newEventSource(request, sseListener)
    }

}