package com.djessyczaplicki.groupcalendar.ui.screen.login

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djessyczaplicki.groupcalendar.R
import com.orhanobut.logger.Logger
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val isLoading = mutableStateOf(false)
    private val loadError = mutableStateOf("")

    fun isLoading(): MutableState<Boolean> = isLoading
    fun loadError() : MutableState<String> = loadError



    fun login(username: String, password: String, context: Context, onSuccessCallback: () -> Unit){
        viewModelScope.launch {
            isLoading.value = true
            try {
                val token = loginUseCase(username, password)
                if (token.isNotEmpty()){
                    isLoading.value = false
                }
                Logger.i(token)
                val userPreferences = UserPreferences(context)
                userPreferences.saveAuthToken(token)
                onSuccessCallback()

            } catch (e: WrongCredentialsException) {
                Toast.makeText(
                    context,
                    context.getString(R.string.wrong_credentials_exception_message),
                    Toast.LENGTH_SHORT
                ).show()
                isLoading.value = false

            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    context.getString(R.string.connection_error_exception_message),
                    Toast.LENGTH_SHORT
                ).show()
                Logger.e(e.message ?: e.toString())
                isLoading.value = false

            }
        }
    }
}