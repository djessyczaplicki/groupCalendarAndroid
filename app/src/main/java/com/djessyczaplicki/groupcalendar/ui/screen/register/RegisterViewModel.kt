package com.djessyczaplicki.groupcalendar.ui.screen.register

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djessyczaplicki.groupcalendar.R
import com.djessyczaplicki.groupcalendar.core.AuthenticationInterceptor
import com.djessyczaplicki.groupcalendar.data.remote.model.User
import com.djessyczaplicki.groupcalendar.domain.userusecase.UpdateUserUseCase
import com.djessyczaplicki.groupcalendar.util.UserPreferences
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val updateUserUseCase : UpdateUserUseCase,
    private val interceptor: AuthenticationInterceptor
) : ViewModel() {
    private val TAG = "LoginViewModel"
    private val isLoading = mutableStateOf(false)
    private val loadError = mutableStateOf("")
    private var auth = Firebase.auth


    fun isLoading(): MutableState<Boolean> = isLoading
    fun loadError() : MutableState<String> = loadError



    fun register(user: User, password: String, confirmPassword: String, context: Context, onSuccessCallback: () -> Unit, onFailureCallback: (message: String?) -> Unit) {
        viewModelScope.launch {
            if (user.email.isBlank()) return@launch onFailureCallback(context.getString(R.string.email_empty))
            if (password.isBlank() || password.length < 6) return@launch onFailureCallback(context.getString(R.string.password_must_be_6_or_greater))
            if (password != confirmPassword) return@launch onFailureCallback(context.getString(R.string.passwords_dont_match))
            if (user.username.length < 6 || user.username.length >= 20) return@launch onFailureCallback(context.getString(R.string.username_length_exception))
            if (user.age != null && user.age!! > 99) return@launch onFailureCallback(context.getString(R.string.wrong_age))
            isLoading.value = true
            auth.createUserWithEmailAndPassword(user.email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "registerInWithEmailAndPassword:success")
                        val token = auth.getAccessToken(false).result?.token
                        viewModelScope.launch {
                            Log.i(TAG, auth.currentUser!!.uid)
                            UserPreferences(context).saveAuthToken(token?:"")
                            interceptor.setSessionToken(token ?: "")
                            user.id = auth.currentUser!!.uid
                            updateUserUseCase(user)
                            onSuccessCallback()
                            isLoading.value = false
                        }
                    } else {
                        // If register fails, display a message to the user.
                        Log.w(TAG, "registerInWithEmailAndPassword:failure", task.exception)
                        onFailureCallback(task.exception?.message)
                        isLoading.value = false
                    }
                }
        }
    }
}