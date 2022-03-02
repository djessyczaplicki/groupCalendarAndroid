package com.djessyczaplicki.groupcalendar.ui.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.djessyczaplicki.groupcalendar.ui.screen.MainScreen
import com.djessyczaplicki.groupcalendar.ui.screen.timetable.TimetableViewModel
import com.djessyczaplicki.groupcalendar.ui.theme.GroupCalendarTheme
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger

class MainActivity : ComponentActivity() {

    private val timetableViewModel: TimetableViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.addLogAdapter(AndroidLogAdapter())

        setContent {
            MainScreen(
                timetableViewModel
            )
        }
    }
}