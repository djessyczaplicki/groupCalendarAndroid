package com.djessyczaplicki.groupcalendar.core

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import com.orhanobut.logger.Logger
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthenticationInterceptor @Inject constructor() : Interceptor {
    private var authToken: String? = null

    fun setSessionToken(authToken: String) {
        Logger.i("My new token is: $authToken")
        this.authToken = authToken
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()
        Log.d("AuthenticationInt", "hello")
        if (authToken.isNullOrBlank()) {
            return chain.proceed(original.newBuilder().url(original.url).build())
        }
        Log.d("AuthenticationInt", "token-not-blank!")

        val originalHttpUrl = original.url

        val url = originalHttpUrl.newBuilder()
            .addQueryParameter("auth", authToken)
            .build()

        val requestBuilder: Request.Builder = original.newBuilder()
            .url(url)

        val request: Request = requestBuilder.build()

        return chain.proceed(request)
    }
}