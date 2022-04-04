package com.djessyczaplicki.groupcalendar.ui.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.djessyczaplicki.groupcalendar.R
import com.djessyczaplicki.groupcalendar.ui.screen.MainScreen
import com.djessyczaplicki.groupcalendar.ui.screen.editevent.EditEventViewModel
import com.djessyczaplicki.groupcalendar.ui.screen.editgroup.EditGroupViewModel
import com.djessyczaplicki.groupcalendar.ui.screen.event.EventViewModel
import com.djessyczaplicki.groupcalendar.ui.screen.invite.InviteViewModel
import com.djessyczaplicki.groupcalendar.ui.screen.login.LoginViewModel
import com.djessyczaplicki.groupcalendar.ui.screen.register.RegisterViewModel
import com.djessyczaplicki.groupcalendar.ui.screen.timetable.TimetableViewModel
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author Djessy Czaplicki
 */

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val TAG = "MainActivity"
    private val loginViewModel: LoginViewModel by viewModels()
    private val timetableViewModel: TimetableViewModel by viewModels()
    private val eventViewModel: EventViewModel by viewModels()
    private val editEventViewModel: EditEventViewModel by viewModels()
    private val editGroupViewModel: EditGroupViewModel by viewModels()
    private val inviteViewModel: InviteViewModel by viewModels()
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        handleCloudMessaging()

        setContent {
            MainScreen(
                loginViewModel,
                timetableViewModel,
                eventViewModel,
                editEventViewModel,
                editGroupViewModel,
                inviteViewModel,
                registerViewModel,
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
            val args = appLinkData?.encodedPath?.split("/")
            val option = args?.get(1)
            val value = args?.get(2)
            when (option) {
                "group" -> intent.putExtra("group_id", value)
                "invite" -> intent.putExtra("invite", value)
                "event" -> intent.putExtra("event", value)
            }
        }

//            appLinkData?.lastPathSegment?.also { groupId ->
//                intent.putExtra("group_id", groupId)
//                // https://developer.android.com/studio/write/app-link-indexing
//            }
//        }
    }

    private fun handleCloudMessaging() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d(TAG, "FirebaseMessaging token: $token")
        }
    }
}