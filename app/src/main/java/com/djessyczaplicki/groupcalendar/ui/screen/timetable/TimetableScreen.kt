package com.djessyczaplicki.groupcalendar.ui.screen.timetable

import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.djessyczaplicki.groupcalendar.R
import com.orhanobut.logger.Logger
import java.util.*


@Composable
fun TimetableScreen(
    timetableViewModel: TimetableViewModel
) {
    val groups = timetableViewModel.groups
    val daysOfTheWeek = LinkedList<String>()
    daysOfTheWeek.addAll(listOf(
        stringResource(R.string.monday_short),
        stringResource(R.string.tuesday_short),
        stringResource(R.string.wednesday_short),
        stringResource(R.string.thursday_short),
        stringResource(R.string.friday_short),
        stringResource(R.string.saturday_short),
        stringResource(R.string.sunday_short)
    ))
    val firstDay = 0
    for (i in 0 until firstDay) {
        daysOfTheWeek.addLast(daysOfTheWeek.removeFirst())
    }
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            daysOfTheWeek.forEach{
                Column (
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = it,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            daysOfTheWeek.forEach{ _ ->
                Card(
                    elevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(2.dp)
                ) {
                    Text("test")
                }
            }
        }

    }

}

@Composable
fun TabScreen() {

}