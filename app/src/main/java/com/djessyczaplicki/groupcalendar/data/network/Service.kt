package com.djessyczaplicki.groupcalendar.data.network

import android.util.Log
import com.djessyczaplicki.groupcalendar.core.RetrofitHelper
import com.djessyczaplicki.groupcalendar.data.remote.model.Event
import com.djessyczaplicki.groupcalendar.data.remote.model.Group
import com.djessyczaplicki.groupcalendar.data.remote.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.JsonObject
import com.orhanobut.logger.Logger
import org.json.JSONObject

class Service {
    private val TAG = "Service"

    suspend fun getAllGroups(): List<Group>{
        val response = RetrofitHelper.getApiClient().getAllGroups()
        Log.i(TAG, response.toString())
        return response.body() ?: emptyList()
    }

    suspend fun updateGroupEvents(group: Group): List<Event>{
        val response = RetrofitHelper.getApiClient().updateGroupEvents(group.events, group.id)
        Log.i(TAG, response.toString())
        return response.body() ?: emptyList()
    }

    suspend fun updateGroup(group: Group): Group{
        val response = RetrofitHelper.getApiClient().updateGroup(group, group.id)
        Log.i(TAG, response.toString())
        return response.body()!!
    }

    suspend fun getGroupById(id: String): Group? {
        val response = RetrofitHelper.getApiClient().getGroup(id)
        Log.i(TAG, response.toString())
        return response.body()
    }

    suspend fun getUserById(id: String): User {
        val response = RetrofitHelper.getApiClient().getUser(id)
        Log.i(TAG, response.toString())
        return response.body()!!
    }

    suspend fun updateUser(user: User): User{
        val response = RetrofitHelper.getApiClient().updateUser(user, user.id)
        Log.i(TAG, response.toString())
        return response.body()!!
    }

//    suspend fun createGroup(group: Group): Group {
//        val response = RetrofitHelper.getApiClient().createGroup(group, group.id)
//        Log.i(TAG, response.toString())
//        return response.body()!!
//    }
}