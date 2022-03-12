package com.djessyczaplicki.groupcalendar.ui.screen

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.djessyczaplicki.groupcalendar.data.remote.model.Event
import com.djessyczaplicki.groupcalendar.ui.screen.editevent.EditEventScreen
import com.djessyczaplicki.groupcalendar.ui.screen.editevent.EditEventViewModel
import com.djessyczaplicki.groupcalendar.ui.screen.event.EventScreen
import com.djessyczaplicki.groupcalendar.ui.screen.event.EventViewModel
import com.djessyczaplicki.groupcalendar.ui.screen.login.LoginScreen
import com.djessyczaplicki.groupcalendar.ui.screen.login.LoginViewModel
import com.djessyczaplicki.groupcalendar.ui.screen.timetable.TimetableScreen
import com.djessyczaplicki.groupcalendar.ui.screen.timetable.TimetableViewModel
import com.djessyczaplicki.groupcalendar.ui.theme.GroupCalendarTheme
import com.orhanobut.logger.Logger

@Composable
fun MainScreen(
    loginViewModel: LoginViewModel,
    timetableViewModel: TimetableViewModel,
    eventViewModel: EventViewModel,
    editEventViewModel: EditEventViewModel,
    intent: Intent
) {
    val context = LocalContext.current

    GroupCalendarTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = AppScreens.LoginScreen.route
        ) {
            composable(AppScreens.LoginScreen.route) {
                LoginScreen(
                    navController = navController,
                    loginViewModel = loginViewModel,
                    intent = intent
                )
            }
            composable(
                AppScreens.TimetableScreen.route + "/{group_id}",
                arguments = listOf(
                    navArgument("group_id") { type = NavType.StringType }
                )
            ) {
                val groupId = it.arguments?.getString("group_id")!!
                timetableViewModel.loadGroup(groupId)
                TimetableScreen(navController, timetableViewModel)
            }
            composable(
                AppScreens.EditEventScreen.route + "/{group_id}/{event_id}",
                arguments = listOf(
                    navArgument("group_id") { type = NavType.StringType },
                    navArgument("event_id") { type = NavType.StringType }
                )
            ) {
                editEventViewModel.isEditing.value = true
                val groupId = it.arguments?.getString("group_id")!!
                val eventId = it.arguments?.getString("event_id")!!
                editEventViewModel.loadEvent(groupId, eventId)
                EditEventScreen(navController, editEventViewModel)
            }
            composable(
                AppScreens.EditEventScreen.route + "/{group_id}",
                arguments = listOf(
                    navArgument("group_id") { type = NavType.StringType }
                )
            ) {
                editEventViewModel.event.value = Event()
                editEventViewModel.isEditing.value = false
                editEventViewModel.groupId = it.arguments?.getString("group_id")!!
                EditEventScreen(navController, editEventViewModel)
            }
            composable(
                AppScreens.EventScreen.route + "/{group_id}/{event_id}",
                arguments = listOf(
                    navArgument("group_id") { type = NavType.StringType },
                    navArgument("event_id") { type = NavType.StringType }
                )
            ) {
                val groupId = it.arguments?.getString("group_id")!!
                val eventId = it.arguments?.getString("event_id")!!
                eventViewModel.loadEvent(groupId, eventId)
                EventScreen(navController, eventViewModel)
            }
        }
    }
}