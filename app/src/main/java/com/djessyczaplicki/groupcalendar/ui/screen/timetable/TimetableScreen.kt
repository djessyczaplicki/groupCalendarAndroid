package com.djessyczaplicki.groupcalendar.ui.screen.timetable

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.djessyczaplicki.groupcalendar.data.remote.model.Event
import com.djessyczaplicki.groupcalendar.data.remote.model.Group
import com.djessyczaplicki.groupcalendar.ui.item.BasicEvent
import com.djessyczaplicki.groupcalendar.ui.screen.AppScreens
import com.djessyczaplicki.groupcalendar.ui.theme.GroupCalendarTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt


@Composable
fun TimetableScreen(
    navController: NavController,
    timetableViewModel: TimetableViewModel
) {
    val groups = timetableViewModel.groups.value
    var group: Group? = null
    var weeksToAdd by rememberSaveable { mutableStateOf(0) }
    val today = LocalDate.now().plusWeeks(weeksToAdd.toLong())
    // https://stackoverflow.com/questions/28450720/get-date-of-first-day-of-week-based-on-localdate-now-in-java-8
    val dayOfWeek = DayOfWeek.MONDAY
    val firstDateOfWeek by rememberSaveable {
        mutableStateOf(today.with(dayOfWeek))
    }
    val lastDateOfWeek = firstDateOfWeek.plusDays(7)
    var eventsOfTheWeek: List<Event>? = null
    if (groups.isNotEmpty()) {
        group = groups.find { it.id == "0" }
        eventsOfTheWeek = group!!.events.filter { event ->
            event.start.isAfter(firstDateOfWeek.atStartOfDay())
            event.start.isBefore(lastDateOfWeek.atStartOfDay())
        }
    }
    Column(
        Modifier
    ) {
        if (group != null) {
            Button(onClick = {
                navController.navigate(AppScreens.AddEventScreen.route + "/${group.id}")
            }) {
                Text("create event")
            }
            Button(onClick = { timetableViewModel.removeEvent(group.events[2], group) }) {
                Text("remove event")
            }
            Schedule(
                events = eventsOfTheWeek!!,
                minDate = firstDateOfWeek,
                maxDate = lastDateOfWeek
            )
        }

    }

}

@Composable
fun Schedule(
    events: List<Event>,
    modifier: Modifier = Modifier,
    eventContent: @Composable (event: Event) -> Unit = { BasicEvent(event = it) },
    dayHeader: @Composable (day: LocalDate) -> Unit = { BasicDayHeader(day = it) },
    minDate: LocalDate = events.minByOrNull(Event::start)!!.start.toLocalDate(),
    maxDate: LocalDate = events.maxByOrNull(Event::end)!!.end.toLocalDate()
) {
    val days = ChronoUnit.DAYS.between(minDate, maxDate).toInt()
    val hourHeight = 64.dp
    val verticalScrollState = rememberScrollState()
    val heightPx = with(LocalDensity.current) { hourHeight.roundToPx()}
    LaunchedEffect(Unit) { verticalScrollState.animateScrollTo(LocalTime.now().hour * heightPx) }
    var dayWidth by remember { mutableStateOf(0) }
    var sidebarWidth by remember { mutableStateOf(0) }
    Column(modifier = modifier) {
        ScheduleHeader(
            minDate = minDate,
            days = days,
            dayHeader = dayHeader,
            modifier = Modifier
                .padding(start = with(LocalDensity.current) { sidebarWidth.toDp() })
                .onGloballyPositioned { dayWidth = it.size.width / days }
        )
        Row(
            horizontalArrangement = Arrangement.Start
        ) {
            ScheduleSidebar(
                hourHeight = hourHeight,
                modifier = Modifier
                    .verticalScroll(verticalScrollState)
                    .onGloballyPositioned { sidebarWidth = it.size.width }
            )
            BasicSchedule(
                events = events,
                eventContent = eventContent,
                minDate = minDate,
                maxDate = maxDate,
                dayWidth = with(LocalDensity.current) { dayWidth.toDp() },
                hourHeight = hourHeight,
                modifier = Modifier
                    .verticalScroll(verticalScrollState)
            )
        }
    }
}

@Composable
fun BasicSchedule(
    events: List<Event>,
    modifier: Modifier = Modifier,
    eventContent: @Composable (event: Event) -> Unit = { BasicEvent(event = it) },
    minDate: LocalDate = events.minByOrNull(Event::start)!!.start.toLocalDate(),
    maxDate: LocalDate = events.maxByOrNull(Event::end)!!.end.toLocalDate(),
    dayWidth: Dp,
    hourHeight: Dp
) {
    val numDays = ChronoUnit.DAYS.between(minDate, maxDate).toInt()
    val dividerColor = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray
    Layout(
        content = {
            events.sortedBy(Event::start).forEach { event ->
                Box(modifier = Modifier.eventData(event)) {
                    eventContent(event)
                }
            }
        },
        modifier = modifier
            .drawBehind {
                repeat(23) {
                    drawLine(
                        dividerColor,
                        start = Offset(0f, (it + 1) * hourHeight.toPx()),
                        end = Offset(size.width, (it + 1) * hourHeight.toPx()),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                repeat(numDays - 1) {
                    drawLine(
                        dividerColor,
                        start = Offset((it + 1) * dayWidth.toPx(), 0f),
                        end = Offset((it + 1) * dayWidth.toPx(), size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                val time = LocalTime.now()
                drawLine(
                    Color.Cyan,
                    start = Offset(0f, time.hour * hourHeight.toPx() + time.minute * hourHeight.toPx() / 60),
                    end = Offset(size.width, time.hour * hourHeight.toPx() + time.minute * hourHeight.toPx() / 60),
                    strokeWidth = 3.dp.toPx()
                )
            }
    ) { measurables, constraints ->
        val height = hourHeight.roundToPx() * 24
//        val dayWidth = (constraints.maxWidth.toDp() / 7)
        val width = dayWidth.roundToPx() * numDays
        val placeablesWithEvents = measurables.map { measurable ->
            val event = measurable.parentData as Event
            val eventDurationMinutes = ChronoUnit.MINUTES.between(event.start, event.end)
            val eventHeight = ((eventDurationMinutes.div(60f)) * hourHeight.toPx()).roundToInt()
            val placeable = measurable.measure(
                constraints.copy(
                    minWidth = dayWidth.roundToPx(),
                    maxWidth = dayWidth.roundToPx(),
                    minHeight = eventHeight,
                    maxHeight = eventHeight
                )
            )
            Pair(placeable, event)
        }
        layout(width, height) {
            placeablesWithEvents.forEach { (placeable, event) ->
                val eventOffsetMinutes =
                    ChronoUnit.MINUTES.between(LocalTime.MIN, event.start.toLocalTime())
                val eventY = ((eventOffsetMinutes.div(60f)) * hourHeight.toPx()).roundToInt()
                val eventOffsetDays =
                    ChronoUnit.DAYS.between(minDate, event.start.toLocalDate()).toInt()
                val eventX = eventOffsetDays * dayWidth.roundToPx()
                placeable.place(eventX, eventY)
            }
        }
    }
}

@Composable
fun ScheduleHeader(
    minDate: LocalDate,
    days: Int,
    modifier: Modifier = Modifier,
    dayHeader: @Composable (day: LocalDate) -> Unit = { BasicDayHeader(day = it) }
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
    ) {
        val maxWidth = maxWidth
        Row {
            repeat(days) { i ->
                Box(
                    modifier = Modifier
                        .width(maxWidth / days)
                ) {
                    dayHeader(minDate.plusDays(i.toLong()))
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScheduleHeaderPreview() {
    GroupCalendarTheme {
        ScheduleHeader(
            minDate = LocalDate.now(),
            days = 7
        )
    }
}

@Composable
fun ScheduleSidebar(
    hourHeight: Dp,
    modifier: Modifier = Modifier,
    label: @Composable (time: LocalTime) -> Unit = { BasicSidebarLabel(time = it) }
) {
    Column(modifier = modifier) {
        val startTime = LocalTime.MIN
        repeat(24) { i ->
            Box(modifier = Modifier.height(hourHeight)) {
                label(startTime.plusHours(i.toLong()))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScheduleSidebarPreview() {
    GroupCalendarTheme {
        ScheduleSidebar(hourHeight = 64.dp)
    }
}

private fun Modifier.eventData(event: Event) = this.then(EventDataModifier(event))

private class EventDataModifier(
    val event: Event
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = event
}
