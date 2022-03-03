package com.djessyczaplicki.groupcalendar.ui.screen.timetable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.djessyczaplicki.groupcalendar.R
import com.djessyczaplicki.groupcalendar.data.remote.model.Event
import com.djessyczaplicki.groupcalendar.ui.item.BasicEvent
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
fun Schedule(
    events: List<Event>,
    modifier: Modifier = Modifier,
    eventContent: @Composable (event: Event) -> Unit = { BasicEvent(event = it) }
) {
    Layout(
        content = {
            events.sortedBy(Event::start).forEach { event ->
                eventContent(event)
            }
        },
        modifier = modifier
    ) { measurables, constraints ->
        var height = 0
        val placeables = measurables.map { measurable ->
            val placeable = measurable.measure(constraints.copy(maxHeight = 64.dp.roundToPx()))
            height += placeable.height
            placeable
        }
        layout(constraints.maxWidth, height) {
            var y = 0
            placeables.forEach{ placeable ->
                placeable.place(0, y)
                y += placeable.height
            }
        }
    }
}
