package com.djessyczaplicki.groupcalendar.data.network

import com.djessyczaplicki.groupcalendar.data.remote.model.FCMTopicBody
import com.djessyczaplicki.groupcalendar.data.remote.model.PushNotification
import com.djessyczaplicki.groupcalendar.util.Constants.CONTENT_TYPE
import com.djessyczaplicki.groupcalendar.util.Constants.FCM_URL
import com.djessyczaplicki.groupcalendar.util.Constants.SERVER_KEY
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Url

interface NotificationApi {

    @Headers("Authorization:key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST
    suspend fun postNotification(
        @Body notification: PushNotification,
        @Url url: String = FCM_URL
    ): Response<FCMTopicBody>
}
