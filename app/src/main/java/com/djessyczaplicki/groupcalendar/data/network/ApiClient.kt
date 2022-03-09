package com.djessyczaplicki.groupcalendar.data.network

import com.djessyczaplicki.groupcalendar.data.remote.model.Event
import com.djessyczaplicki.groupcalendar.data.remote.model.Group
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import retrofit2.Response
import retrofit2.http.*


interface ApiClient {

    @GET("/groups.json")
    suspend fun getAllGroups(): Response<List<Group>>

    @GET("/groups/{id}.json")
    suspend fun getGroup(
        @Path("id") id: String
    ): Response<Group>

    @PUT("/groups/{groupId}/events.json")
    suspend fun updateGroupEvents(
        @Body events: List<Event>,
        @Path("groupId") groupId: String
    ): Response<List<Event>>



}