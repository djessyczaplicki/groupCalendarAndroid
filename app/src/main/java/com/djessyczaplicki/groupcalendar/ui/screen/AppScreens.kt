package com.djessyczaplicki.groupcalendar.ui.screen

import com.djessyczaplicki.groupcalendar.R

sealed class AppScreens(val route: String, val name: Int) {
    object LoginScreen: AppScreens("login_screen", R.string.login)
    object RegisterScreen: AppScreens("register_screen", R.string.register)
    object TimetableScreen: AppScreens("timetable_screen", R.string.timetable_screen)
    object EditEventScreen: AppScreens("edit_event_screen", R.string.edit_event_screen)
    object EventScreen: AppScreens("event_screen", R.string.event_screen)
    object EditGroupScreen: AppScreens("edit_group_screen", R.string.edit_group_screen)
    object InviteScreen: AppScreens("invite_screen", R.string.invite_screen)
    object SendNotificationScreen: AppScreens("send_notification", R.string.send_notification_screen)
}
