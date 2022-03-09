package com.djessyczaplicki.groupcalendar.ui.screen

import com.djessyczaplicki.groupcalendar.R

sealed class AppScreens(val route: String, val name: Int) {
    object LoginScreen: AppScreens("login_screen", R.string.login)
    object TimetableScreen: AppScreens("timetable_screen", R.string.timetable_screen)
    object AddEventScreen: AppScreens("add_event_screen", R.string.add_event_screen)
}