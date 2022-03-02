package com.djessyczaplicki.groupcalendar.core

import android.util.Log
import com.djessyczaplicki.groupcalendar.data.network.ApiClient
import com.djessyczaplicki.groupcalendar.util.Constants.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    private var mtoken: String = ""
    private val httpClient =  OkHttpClient.Builder()
    private val builder = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())

    private lateinit var apiClient: ApiClient

    fun getApiClient():ApiClient{
        if (!this::apiClient.isInitialized)
            apiClient = builder.build().create(ApiClient::class.java)
        return apiClient
    }

    fun setToken(token: String) {
        mtoken = token
        Log.i("RetrofitHelper", "This is my new token: $mtoken")
        val interceptor = AuthenticationInterceptor("Bearer $mtoken")
        if (!httpClient.interceptors().contains(interceptor)) {
            httpClient.addInterceptor(interceptor)
            builder.client(httpClient.build())
        }
        apiClient = builder.build().create(ApiClient::class.java)
    }
}