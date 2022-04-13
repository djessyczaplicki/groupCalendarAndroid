package com.djessyczaplicki.groupcalendar.ui.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.djessyczaplicki.groupcalendar.ui.screen.MainScreen
import com.djessyczaplicki.groupcalendar.ui.screen.editevent.EditEventViewModel
import com.djessyczaplicki.groupcalendar.ui.screen.editgroup.EditGroupViewModel
import com.djessyczaplicki.groupcalendar.ui.screen.event.EventViewModel
import com.djessyczaplicki.groupcalendar.ui.screen.invite.InviteViewModel
import com.djessyczaplicki.groupcalendar.ui.screen.login.LoginViewModel
import com.djessyczaplicki.groupcalendar.ui.screen.register.RegisterViewModel
import com.djessyczaplicki.groupcalendar.ui.screen.sendnotification.SendNotificationViewModel
import com.djessyczaplicki.groupcalendar.ui.screen.timetable.TimetableViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author Djessy Czaplicki
 */

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val TAG = "MainActivity"
    private lateinit var navController: NavHostController
    private val loginViewModel: LoginViewModel by viewModels()
    private val timetableViewModel: TimetableViewModel by viewModels()
    private val eventViewModel: EventViewModel by viewModels()
    private val editEventViewModel: EditEventViewModel by viewModels()
    private val editGroupViewModel: EditGroupViewModel by viewModels()
    private val inviteViewModel: InviteViewModel by viewModels()
    private val registerViewModel: RegisterViewModel by viewModels()
    private val sendNotificationViewModel: SendNotificationViewModel by viewModels()

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1234) {
            Log.d(TAG, "HELLO")
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            loginViewModel.finishLogin(task, intent, navController, this)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        handleCloudMessaging()

        setContent {
            navController = rememberNavController()
            MainScreen(
                loginViewModel,
                timetableViewModel,
                eventViewModel,
                editEventViewModel,
                editGroupViewModel,
                inviteViewModel,
                registerViewModel,
                sendNotificationViewModel,
                navController,
                intent
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }


    private fun handleIntent(intent: Intent) {
        val appLinkAction = intent.action
        val appLinkData: Uri? = intent.data
        if (Intent.ACTION_VIEW == appLinkAction) {
            val newIntent = Intent(this, MainActivity::class.java)
            val args = appLinkData?.encodedPath?.split("/")
            val option = args?.get(1)
            val value = args?.get(2)
            when (option) {
                "group" -> newIntent.putExtra("group_id", value)
                "invite" -> newIntent.putExtra("invite", value)
                "event" -> newIntent.putExtra("event", value)
            }
            newIntent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            )
            this.startActivity(newIntent)
            this.finish()
        }

//            appLinkData?.lastPathSegment?.also { groupId ->
//                intent.putExtra("group_id", groupId)
//                // https://developer.android.com/studio/write/app-link-indexing
//            }
//        }
    }

    private fun handleCloudMessaging() {
        val firebaseMessaging = FirebaseMessaging.getInstance()
        firebaseMessaging.token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d(TAG, "FirebaseMessaging token: $token")
        }
    }
}
