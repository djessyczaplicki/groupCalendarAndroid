package com.djessyczaplicki.groupcalendar.data.network

import android.util.Log
import com.djessyczaplicki.groupcalendar.core.RetrofitHelper
import com.djessyczaplicki.groupcalendar.data.remote.model.Group
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
}