package com.djessyczaplicki.groupcalendar.ui.screen.login

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djessyczaplicki.groupcalendar.core.RetrofitHelper
import com.djessyczaplicki.groupcalendar.domain.userusecase.GetUserByIdUseCase
import com.djessyczaplicki.groupcalendar.util.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val TAG = "LoginViewModel"
    private val isLoading = mutableStateOf(false)
    private val loadError = mutableStateOf("")
    private lateinit var auth: FirebaseAuth

    private val getUserByIdUseCase = GetUserByIdUseCase()

    fun isLoading(): MutableState<Boolean> = isLoading
    fun loadError() : MutableState<String> = loadError



    fun login(email: String, password: String, context: Context, onSuccessCallback: () -> Unit, onFailureCallback: (exception: String?) -> Unit){
        viewModelScope.launch {
            isLoading.value = true
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithEmail:success")
                        val token = auth.getAccessToken(false).result?.token
                        viewModelScope.launch {
                            Log.i(TAG, auth.currentUser!!.uid)
                            UserPreferences(context).saveAuthToken(token?:"")
                            RetrofitHelper.setToken(token?:"")
                            onSuccessCallback()
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        onFailureCallback(task.exception?.message)
                    }
                }
        }
    }

    fun testAuth(context: Context, onSuccessCallback: () -> Unit) {
        auth = Firebase.auth
        if (auth.currentUser != null) {
            auth.getAccessToken(false).addOnCompleteListener {
                viewModelScope.launch {
                    Log.i(TAG, auth.currentUser!!.uid)
                    val token = it.result?.token
                    UserPreferences(context).saveAuthToken(token ?: "")
                    RetrofitHelper.setToken(token ?: "")
                    onSuccessCallback()
                }
            }
        }
    }

    fun loadUserGroups(onSuccessCallback: (groupId: String) -> Unit) {
        viewModelScope.launch{
            val userId = Firebase.auth.currentUser!!.uid
            val user = getUserByIdUseCase(userId)
            val groupId = if (user.groups.isNotEmpty()) {
                user.groups[0]
            } else {
                "0"
            }
            onSuccessCallback(groupId)
        }
    }
}