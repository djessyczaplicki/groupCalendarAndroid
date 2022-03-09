package com.djessyczaplicki.groupcalendar.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.djessyczaplicki.groupcalendar.ui.screen.addevent.AddEventScreen
import com.djessyczaplicki.groupcalendar.ui.screen.addevent.AddEventViewModel
import com.djessyczaplicki.groupcalendar.ui.screen.login.LoginScreen
import com.djessyczaplicki.groupcalendar.ui.screen.login.LoginViewModel
import com.djessyczaplicki.groupcalendar.ui.screen.timetable.TimetableScreen
import com.djessyczaplicki.groupcalendar.ui.screen.timetable.TimetableViewModel
import com.djessyczaplicki.groupcalendar.ui.theme.GroupCalendarTheme
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MainScreen(
    loginViewModel: LoginViewModel,
    timetableViewModel: TimetableViewModel,
    addEventViewModel: AddEventViewModel
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
                    loginViewModel = loginViewModel
                )
            }
            composable(AppScreens.TimetableScreen.route) {
                timetableViewModel.loadGroups()
                TimetableScreen(navController, timetableViewModel)
            }
            composable(
                AppScreens.AddEventScreen.route + "/{group_id}",
                arguments = listOf(
                    navArgument("group_id") { type = NavType.StringType }
                )
            ) {
                addEventViewModel.groupId = it.arguments?.getString("group_id")!!
                AddEventScreen(navController, addEventViewModel)
            }
        }
    }
}