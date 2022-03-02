package com.djessyczaplicki.groupcalendar.data.network

import com.djessyczaplicki.groupcalendar.data.remote.model.Group
import retrofit2.Response
import retrofit2.http.*


interface ApiClient {
    @GET("/groups.json")
    suspend fun getAllGroups(): Response<List<Group>>
}