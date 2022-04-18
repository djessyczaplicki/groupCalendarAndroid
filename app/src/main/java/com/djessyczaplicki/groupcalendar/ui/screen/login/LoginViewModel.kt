package com.djessyczaplicki.groupcalendar.ui.screen.login

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.djessyczaplicki.groupcalendar.GroupCalendarApp
import com.djessyczaplicki.groupcalendar.R
import com.djessyczaplicki.groupcalendar.core.AuthenticationInterceptor
import com.djessyczaplicki.groupcalendar.data.local.exception.UserNotFoundException
import com.djessyczaplicki.groupcalendar.data.remote.model.User
import com.djessyczaplicki.groupcalendar.domain.userusecase.GetUserByIdUseCase
import com.djessyczaplicki.groupcalendar.domain.userusecase.UpdateUserUseCase
import com.djessyczaplicki.groupcalendar.ui.screen.AppScreens
import com.djessyczaplicki.groupcalendar.util.UserPreferences
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val interceptor: AuthenticationInterceptor,
) : ViewModel() {
    private val TAG = "LoginViewModel"
    private val isLoading = mutableStateOf(false)
    private val loadError = mutableStateOf("")

    fun isLoading(): MutableState<Boolean> = isLoading
    fun loadError(): MutableState<String> = loadError
    private lateinit var googleSignInClient: GoogleSignInClient

    fun login(
        email: String,
        password: String,
        context: Context,
        onSuccessCallback: () -> Unit,
        onFailureCallback: (exception: String?) -> Unit
    ) {
        viewModelScope.launch {
            isLoading.value = true
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithEmail:success")
                        val token = auth.getAccessToken(false).result?.token
                        viewModelScope.launch {
                            Log.i(TAG, auth.currentUser!!.uid)
                            UserPreferences(context).saveAuthToken(token ?: "")
                            interceptor.setSessionToken(token ?: "")
                            onSuccessCallback()
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        onFailureCallback(task.exception?.message)
                    }
                    isLoading.value = false
                }
        }
    }

    fun testAuth(context: Context, onSuccessCallback: () -> Unit) {
        isLoading.value = true
        if (auth.currentUser != null) {
            auth.getAccessToken(false).addOnCompleteListener {
                viewModelScope.launch {
                    Log.i(TAG, auth.currentUser!!.uid)
                    val token = it.result?.token
                    UserPreferences(context).saveAuthToken(token ?: "")
                    interceptor.setSessionToken(token ?: "")
                    onSuccessCallback()
                }
            }
        }
        isLoading.value = false
    }

    fun loadUserGroups(
        onGroupFoundCallback: (groupId: String) -> Unit,
        onNoGroupCallback: () -> Unit,
        onFailureCallback: (message: String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val userId = Firebase.auth.currentUser!!.uid
                val user =
                    getUserByIdUseCase(userId) ?: throw UserNotFoundException("User not found")
                if (user.groups.isNotEmpty()) {
                    onGroupFoundCallback(user.groups[0])
                } else {
                    onNoGroupCallback()
                }
            } catch (ex: UserNotFoundException) {
                onFailureCallback(ex.message!!)
            }
        }
    }

    fun loginWithGoogle(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.web_client_id))
            .requestEmail()
            .build()


//        googleSignInClient.signOut()

        val activity = context as Activity

        googleSignInClient = GoogleSignIn.getClient(context, gso)
        googleSignIn(googleSignInClient, activity)
    }

    private fun googleSignIn(
        googleSignInClient: GoogleSignInClient,
        activity: Activity
    ) {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInClient.signOut()

        activity.startActivityForResult(signInIntent, 1234)
    }

    fun finishLogin(
        accountTask: Task<GoogleSignInAccount>,
        intent: Intent,
        navController: NavHostController,
        context: Context
    ) {
        val account: GoogleSignInAccount? = accountTask.getResult(ApiException::class.java)!!
        account?.idToken?.let { token ->
            val credential = GoogleAuthProvider.getCredential(token, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        checkAccountRegistered(account, intent, navController, context)
                    } else {
                        Log.w(ContentValues.TAG, "Google sign in failed bruuuu")
                    }

                }
        }
//        Log.w(ContentValues.TAG, "Google sign in failed bruuuuuuuh", e)

    }

    private fun checkAccountRegistered(
        googleAccount: GoogleSignInAccount,
        intent: Intent,
        navController: NavHostController,
        context: Context
    ) {
        viewModelScope.launch {
            val user = getUserByIdUseCase(auth.uid!!)
            if (user == null) {
                createAccount(googleAccount, context) {
                    success(intent, navController)
                }

            } else {
                success(intent, navController);
            }
        }
    }

    private fun createAccount(
        googleAccount: GoogleSignInAccount,
        context: Context,
        onSuccessCallback: () -> Unit
    ) {
        val user = User(
            id = auth.uid!!,
            name = googleAccount.givenName ?: "NoGoogleName",
            surname = googleAccount.familyName ?: "NoGoogleFamilyName",
            email = googleAccount.email ?: "example@gmail.com",
            username = googleAccount.displayName ?: "NoGoogleDisplayName"
        )
        viewModelScope.launch {
            updateUserUseCase(user)
            val credential = GoogleAuthProvider.getCredential(
                googleAccount.idToken, null
            )
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithCredential:success")
                        val token = auth.getAccessToken(false).result?.token
                        viewModelScope.launch {
                            Log.i(TAG, auth.currentUser!!.uid)
                            UserPreferences(context).saveAuthToken(token ?: "")
                            interceptor.setSessionToken(token ?: "")
                            user.id = auth.currentUser!!.uid
                            updateUserUseCase(user)
                            onSuccessCallback()
                            isLoading.value = false
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        Toast.makeText(
                            context,
                            context.getString(R.string.google_sign_in_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
        }
    }

    fun success(intent: Intent, navController: NavController) {
        if (!intent.getStringExtra("invite").isNullOrBlank()) {
            val groupId = intent.getStringExtra("invite")!!
            Log.d(TAG, "invite: $groupId")
            navController.navigate(AppScreens.InviteScreen.route + "/$groupId") {
                popUpTo(0)
            }
        } else if (!intent.getStringExtra("event_id")
                .isNullOrBlank() && !intent.getStringExtra("group_id").isNullOrBlank()
        ) {
            val groupId = intent.getStringExtra("group_id")!!
            val eventId = intent.getStringExtra("event_id")!!
            Log.d(TAG, "Group: $groupId, Event: $eventId")
            navController.navigate(AppScreens.EventScreen.route + "/$groupId/$eventId") {
                popUpTo(0)
            }
        } else if (!intent.getStringExtra("group_id").isNullOrBlank()) {
            val groupId = intent.getStringExtra("group_id")!!
            Log.d(TAG, groupId)
            navController.navigate(AppScreens.TimetableScreen.route + "/$groupId") {
                popUpTo(0)
            }
        } else {
            loadUserGroups(
                onGroupFoundCallback = { groupId ->
                    navController.navigate(AppScreens.TimetableScreen.route + "/$groupId") {
                        popUpTo(0)
                    }
                },
                onNoGroupCallback = {
                    navController.navigate(AppScreens.EditGroupScreen.route) {
                        popUpTo(0)
                    }
                },
                onFailureCallback = { message ->
                    Toast.makeText(
                        GroupCalendarApp.applicationContext(),
                        message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }

}
