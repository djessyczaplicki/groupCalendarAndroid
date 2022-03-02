package com.djessyczaplicki.groupcalendar.ui.screen

import com.djessyczaplicki.groupcalendar.R

sealed class AppScreens(val route: String, val name: Int) {
    object TimetableScreen: AppScreens("timetable_screen", R.string.timetable_screen)
}