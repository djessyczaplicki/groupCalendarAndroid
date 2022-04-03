package com.djessyczaplicki.groupcalendar.ui.screen

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.djessyczaplicki.groupcalendar.data.remote.model.Event
import com.djessyczaplicki.groupcalendar.ui.screen.editevent.EditEventScreen
import com.djessyczaplicki.groupcalendar.ui.screen.editevent.EditEventViewModel
import com.djessyczaplicki.groupcalendar.ui.screen.editgroup.EditGroupScreen
import com.djessyczaplicki.groupcalendar.ui.screen.editgroup.EditGroupViewModel
import com.djessyczaplicki.groupcalendar.ui.screen.event.EventScreen
import com.djessyczaplicki.groupcalendar.ui.screen.event.EventViewModel
import com.djessyczaplicki.groupcalendar.ui.screen.invite.InviteScreen
import com.djessyczaplicki.groupcalendar.ui.screen.invite.InviteViewModel
import com.djessyczaplicki.groupcalendar.ui.screen.login.LoginScreen
import com.djessyczaplicki.groupcalendar.ui.screen.login.LoginViewModel
import com.djessyczaplicki.groupcalendar.ui.screen.register.RegisterScreen
import com.djessyczaplicki.groupcalendar.ui.screen.register.RegisterViewModel
import com.djessyczaplicki.groupcalendar.ui.screen.timetable.TimetableScreen
import com.djessyczaplicki.groupcalendar.ui.screen.timetable.TimetableViewModel
import com.djessyczaplicki.groupcalendar.ui.theme.GroupCalendarTheme

@Composable
fun MainScreen(
    loginViewModel: LoginViewModel,
    timetableViewModel: TimetableViewModel,
    eventViewModel: EventViewModel,
    editEventViewModel: EditEventViewModel,
    editGroupViewModel: EditGroupViewModel,
    inviteViewModel: InviteViewModel,
    registerViewModel: RegisterViewModel,
    intent: Intent
) {

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
                AppScreens.TimetableScreen.route + AppScreensVariables.GroupIds.variable,
                arguments = listOf(
                    navArgument("group_ids") { type = NavType.StringType }
                )
            ) {
                val groupIdsString = it.arguments?.getString("group_ids")!!
                val groupIds = groupIdsString.split(",")
                timetableViewModel.loadShownGroups(groupIds)
                LaunchedEffect("") {
                    timetableViewModel.loadGroups()
                }
                TimetableScreen(navController, timetableViewModel)
            }
            composable(
                AppScreens.EditEventScreen.route
                        + AppScreensVariables.GroupId.variable
                        + AppScreensVariables.EventId.variable,
                arguments = listOf(
                    navArgument("group_id") { type = NavType.StringType },
                    navArgument("event_id") { type = NavType.StringType }
                )
            ) {
                editEventViewModel.isEditing.value = true
                EditEventScreen(navController, editEventViewModel)
            }
            composable(
                AppScreens.EditEventScreen.route + AppScreensVariables.GroupId.variable,
                arguments = listOf(
                    navArgument("group_id") { type = NavType.StringType }
                )
            ) {
                // These two lines are used to reset the values
                editEventViewModel.event.value = Event()
                editEventViewModel.isEditing.value = false

                editEventViewModel.groupId = it.arguments?.getString("group_id")!!
                editEventViewModel.loadGroup()
                EditEventScreen(navController, editEventViewModel)
            }
            composable(
                AppScreens.EventScreen.route
                        + AppScreensVariables.GroupId.variable
                        + AppScreensVariables.EventId.variable,
                arguments = listOf(
                    navArgument("group_id") { type = NavType.StringType },
                    navArgument("event_id") { type = NavType.StringType }
                )
            ) {
                val groupId = it.arguments?.getString("group_id")!!
                val eventId = it.arguments?.getString("event_id")!!
                eventViewModel.loadEvent(groupId, eventId)
                EventScreen(navController, eventViewModel)
                // I preload the editEventView model, so when the user wants to edit it, it's already loaded
                editEventViewModel.loadEvent(groupId, eventId)
            }
            composable(
                AppScreens.EditGroupScreen.route + AppScreensVariables.GroupId.variable,
                arguments = listOf(
                    navArgument("group_id") { type = NavType.StringType }
                )
            ) {
                val groupId = it.arguments?.getString("group_id")!!
                editGroupViewModel.groupId = groupId
                editGroupViewModel.isEditing.value = true
                EditGroupScreen(navController, editGroupViewModel)
            }
            composable(
                AppScreens.EditGroupScreen.route
            ) {
                // resetting values
                editGroupViewModel.groupId = null
                editGroupViewModel.isEditing.value = false

                EditGroupScreen(navController, editGroupViewModel)
            }
            composable(
                AppScreens.InviteScreen.route + AppScreensVariables.GroupId.variable,
                arguments = listOf(
                    navArgument("group_id") { type = NavType.StringType }
                )
            ) {
                val groupId = it.arguments?.getString("group_id")!!
                inviteViewModel.groupId.value = groupId
                inviteViewModel.load()

                InviteScreen(navController, inviteViewModel)
            }
            composable(
                AppScreens.RegisterScreen.route
            ) {
                RegisterScreen(navController = navController, registerViewModel = registerViewModel)
            }
        }
    }
}