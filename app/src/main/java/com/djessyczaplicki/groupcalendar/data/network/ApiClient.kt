package com.djessyczaplicki.groupcalendar.data.network

import com.djessyczaplicki.groupcalendar.data.remote.model.Event
import com.djessyczaplicki.groupcalendar.data.remote.model.Group
import com.djessyczaplicki.groupcalendar.data.remote.model.User
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

    @PUT("/groups/{groupId}.json")
    suspend fun updateGroup(
        @Body group: Group,
        @Path("groupId") groupId: String
    ): Response<Group>

    @GET("/users/{id}.json")
    suspend fun getUser(
        @Path("id") id: String
    ): Response<User>

    @PUT("/users/{userId}.json")
    suspend fun updateUser(
        @Body user: User,
        @Path("userId") userId: String
    ): Response<User>

    @DELETE("/groups/{groupId}.json")
    suspend fun deleteGroup(
        @Path("groupId") groupId: String
    ): Response<Any>

//    @POST("/groups/{groupId}")
//    suspend fun createGroup(
//        @Body group: Group,
//        @Path("groupId") groupId: String
//    ): Response<Group>


}
