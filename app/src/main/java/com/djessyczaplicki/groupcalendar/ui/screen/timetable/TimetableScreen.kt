package com.djessyczaplicki.groupcalendar.ui.screen.timetable

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable


@Composable
fun TimetableScreen(
    timetableViewModel: TimetableViewModel
) {
    var groups = timetableViewModel.groups

    Column {
        groups.forEach{
            Text(text = it.name)
        }
    }
}