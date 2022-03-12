package com.djessyczaplicki.groupcalendar.ui.screen

import com.djessyczaplicki.groupcalendar.R

sealed class AppScreens(val route: String, val name: Int) {
    object LoginScreen: AppScreens("login_screen", R.string.login)
    object TimetableScreen: AppScreens("timetable_screen", R.string.timetable_screen)
    object EditEventScreen: AppScreens("edit_event_screen", R.string.edit_event_screen)
    object EventScreen: AppScreens("event_screen", R.string.event_screen)
}