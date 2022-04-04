package com.djessyczaplicki.groupcalendar.core

import com.djessyczaplicki.groupcalendar.data.remote.model.PushNotification
import com.djessyczaplicki.groupcalendar.util.Constants.CONTENT_TYPE
import com.djessyczaplicki.groupcalendar.util.Constants.FCM_BASE_URL
import com.djessyczaplicki.groupcalendar.util.Constants.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Url

interface NotificationAPI {
    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotifiaction(
        @Body notification: PushNotification,
        @Url url: String = FCM_BASE_URL
    ): Response<ResponseBody>
}