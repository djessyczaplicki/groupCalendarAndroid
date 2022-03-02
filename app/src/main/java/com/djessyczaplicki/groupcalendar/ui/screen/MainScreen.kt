package com.djessyczaplicki.groupcalendar.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.djessyczaplicki.groupcalendar.ui.screen.timetable.TimetableScreen
import com.djessyczaplicki.groupcalendar.ui.screen.timetable.TimetableViewModel

@Composable
fun MainScreen(
    timetableViewModel: TimetableViewModel
) {
    val context = LocalContext.current

    timetableViewModel.loadGroups()
    TimetableScreen(timetableViewModel)
}