package com.djessyczaplicki.groupcalendar.ui.screen.timetable

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.djessyczaplicki.groupcalendar.R
import com.djessyczaplicki.groupcalendar.data.remote.model.Event
import com.djessyczaplicki.groupcalendar.ui.item.BasicEvent
import com.djessyczaplicki.groupcalendar.ui.item.DrawerContent
import com.djessyczaplicki.groupcalendar.ui.item.GroupRow
import com.djessyczaplicki.groupcalendar.ui.item.TopBar
import com.djessyczaplicki.groupcalendar.ui.screen.AppScreens
import com.djessyczaplicki.groupcalendar.ui.theme.GroupCalendarTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import java.lang.Integer.max
import java.lang.Integer.min
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt


@OptIn(ExperimentalPagerApi::class)
@Composable
fun TimetableScreen(
    navController: NavController,
    timetableViewModel: TimetableViewModel
) {
    val numberOfPages = 1000
    val startPage = numberOfPages / 2
    val daysToShow = 7L
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val groups = timetableViewModel.groups
    var gesturesEnabled by remember { mutableStateOf(false) }
    var isDailyViewEnabled by rememberSaveable { mutableStateOf(false) }
    var page by rememberSaveable { mutableStateOf(startPage) }
    val pagerState = rememberPagerState()
    ModalDrawer(
        drawerState = drawerState,
        gesturesEnabled = gesturesEnabled,
        drawerContent = {
            DrawerContent(
                groups = groups.value,
                navController = navController
            ) { route ->
                scope.launch {
                    gesturesEnabled = false
                    drawerState.close()
                }

                navController.navigate(route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                val resources = LocalContext.current.resources
                val shownGroups = timetableViewModel.shownGroups.value
                val scope = rememberCoroutineScope()
                val groupNames = shownGroups.joinToString { it.name }
                TopBar(
                    title = resources.getQuantityString(R.plurals.timetable_title, shownGroups.size) + ": $groupNames",
                    navController = navController,
                    isDailyViewEnabled = isDailyViewEnabled,
                    onIconClicked = {
                        isDailyViewEnabled = !isDailyViewEnabled
                        scope.launch{
                            // this method is used in order to compensate the gap between moving pages by days and by week,
                            // so when we swap from days to week or vice versa, the number of the actual page adapts
                            // ex isDailyViewEnabled = true:
                            //      we are at page 502/1000, with startPage = 500 -> 502 - 500 = 2; 2 * 7 = 14; 14 + 500 = 514
                            // ex isDailyViewEnabled = false:
                            //      we are at page 490/1000, with startPage = 500 -> 486 - 500 = -14; -14 / 7 = -2; -2 + 500 = 498
                            pagerState.scrollToPage(
                                if (isDailyViewEnabled)
                                    max(min(((page - startPage) * daysToShow.toInt()) + startPage, numberOfPages), 0)
                                else
                                    ((page - startPage).floorDiv(daysToShow.toInt())) + startPage
                            )
                        }
                    },
                    onButtonClicked = {
                        scope.launch {
                            gesturesEnabled = true
                            drawerState.open()
                        }
                    }
                )
            },
            content = {
                val scrollState = rememberScrollState()
                val heightPx = with(LocalDensity.current) { 64.dp.roundToPx()}
                LaunchedEffect("key") { scrollState.animateScrollTo(LocalTime.now().hour * heightPx) }

                HorizontalPager(count = numberOfPages, state = pagerState ) { pageNum ->
                    TimetablePage(
                        navController = navController,
                        timetableViewModel = timetableViewModel,
                        page = pageNum - startPage,
                        scrollState = scrollState,
                        daysToShow = if (isDailyViewEnabled) 1 else daysToShow
                    )
                    page = pagerState.currentPage
                }
                LaunchedEffect("key1") {
                    pagerState.scrollToPage(if (page == 0) startPage else page)
                }
            },
            floatingActionButton =  {
                TimetableFAB(navController = navController, timetableViewModel = timetableViewModel)
            }
        )
    }

}

@Composable
fun TimetablePage(
    navController: NavController,
    timetableViewModel: TimetableViewModel,
    page: Int,
    scrollState: ScrollState,
    daysToShow: Long = 7L
) {
    val day = LocalDate.now().plusDays(page * daysToShow)
    // https://stackoverflow.com/questions/28450720/get-date-of-first-day-of-week-based-on-localdate-now-in-java-8
    val dayOfWeek = DayOfWeek.MONDAY
    val firstDateOfWeek by rememberSaveable {
        mutableStateOf(if (daysToShow == 7L) day.with(dayOfWeek) else day)
    }
    val lastDateOfWeek = firstDateOfWeek.plusDays(daysToShow)
    val events = timetableViewModel.events.value
    val eventsOfTheWeek = events.filter { event ->
        event.start.isAfter(firstDateOfWeek.atStartOfDay())
        && event.end.isBefore(lastDateOfWeek.atStartOfDay())
    }
    Column(
        Modifier
    ) {
        Schedule(
            events = eventsOfTheWeek,
            minDate = firstDateOfWeek,
            maxDate = lastDateOfWeek,
            eventContent = { event ->
                val group = timetableViewModel.findParentGroup(event)
                BasicEvent(event = event) {
                    if (group != null) {
                        navController.navigate(AppScreens.EventScreen.route + "/${group.id}/${event.id}")
                    }
                }
            },
            scrollState = scrollState
        )

    }
}

/* Credits to Daniel Rapmelt */
/* https://danielrampelt.com/blog/jetpack-compose-custom-schedule-layout-part-1/ */

@Composable
fun Schedule(
    events: List<Event>?,
    modifier: Modifier = Modifier,
    eventContent: @Composable (event: Event) -> Unit = { BasicEvent(event = it) },
    dayHeader: @Composable (day: LocalDate) -> Unit = { BasicDayHeader(day = it) },
    minDate: LocalDate,
    maxDate: LocalDate,
    scrollState: ScrollState
) {
    val days = ChronoUnit.DAYS.between(minDate, maxDate).toInt()
    val hourHeight = 64.dp
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
                    .verticalScroll(scrollState)
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
                    .verticalScroll(scrollState)
            )
        }
    }
}

@Composable
fun BasicSchedule(
    events: List<Event>?,
    modifier: Modifier = Modifier,
    eventContent: @Composable (event: Event) -> Unit = { BasicEvent(event = it) },
    minDate: LocalDate,
    maxDate: LocalDate,
    dayWidth: Dp,
    hourHeight: Dp
) {
    val numDays = ChronoUnit.DAYS.between(minDate, maxDate).toInt()
    val dividerColor = Color.LightGray
    Layout(
        content = {
            events?.sortedBy(Event::start)?.forEach { event ->
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
        val width = dayWidth.roundToPx() * numDays
        val placeablesWithEvents = measurables.map { measurable ->
            val event = measurable.parentData as Event
            val eventDurationMinutes = ChronoUnit.MINUTES.between(event.start, event.end)
            val eventHeight = max(
                ((eventDurationMinutes.div(60f)) * hourHeight.toPx()).roundToInt(),
                50
            )
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

@Composable
fun TimetableFAB(navController: NavController, timetableViewModel: TimetableViewModel) {
    var isDialogShown by remember { mutableStateOf(false) }
    val shownGroups = timetableViewModel.shownGroups.value
    FloatingActionButton(
        onClick = {
            if (shownGroups.size == 1) navController.navigate(AppScreens.EditEventScreen.route
                    + "/${shownGroups[0].id}")
            else
                isDialogShown = true
        }) {
        Text("+")
    }
    if (isDialogShown) {
        AlertDialog(
            onDismissRequest = { isDialogShown = false },
            title = { Text (stringResource(id = R.string.select_a_group)) },
            buttons = {
                LazyColumn {
                    items(shownGroups) { group ->
                        GroupRow(
                            onDestinationClicked = {
                                navController.navigate(AppScreens.EditEventScreen.route
                                        + "/${group.id}")
                            },
                            group = group
                        )
                    }
                }
            }
        )
    }
}