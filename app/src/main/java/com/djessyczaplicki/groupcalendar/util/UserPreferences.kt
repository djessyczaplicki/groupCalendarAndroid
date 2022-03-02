package com.djessyczaplicki.groupcalendar.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("asdf")

class UserPreferences (
    val context: Context
){
    val TOKEN_AUTH = stringPreferencesKey("token_auth")
    private val applicationContext = context.applicationContext

    val authToken: Flow<String?> = context.dataStore.data.map { preferences ->
            preferences[TOKEN_AUTH]
        }

    suspend fun saveAuthToken(authToken: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_AUTH] = authToken
        }
    }


}