package com.djessyczaplicki.groupcalendar.ui.screen.timetable

import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.djessyczaplicki.groupcalendar.R
import com.djessyczaplicki.groupcalendar.data.remote.model.Event
import com.djessyczaplicki.groupcalendar.ui.item.BasicEvent
import com.djessyczaplicki.groupcalendar.ui.item.DrawerContent
import com.djessyczaplicki.groupcalendar.ui.item.GroupRow
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


@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
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
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = gesturesEnabled,
        drawerContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        drawerContainerColor = MaterialTheme.colorScheme.primaryContainer,
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
                val groupNames = shownGroups.joinToString { it.name }
                TopBar(
                    title = resources.getQuantityString(
                        R.plurals.timetable_title,
                        shownGroups.size
                    ) + ": $groupNames",
                    isDailyViewEnabled = isDailyViewEnabled,
                    onIconClicked = {
                        isDailyViewEnabled = !isDailyViewEnabled
                        scope.launch {
                            // this method is used in order to compensate the gap between moving pages by days and by week,
                            // so when we swap from days to week or vice versa, the number of the actual page adapts
                            // ex isDailyViewEnabled = true:
                            //      we are at page 502/1000, with startPage = 500 -> 502 - 500 = 2; 2 * 7 = 14; 14 + 500 = 514
                            // ex isDailyViewEnabled = false:
                            //      we are at page 490/1000, with startPage = 500 -> 486 - 500 = -14; -14 / 7 = -2; -2 + 500 = 498
                            pagerState.scrollToPage(
                                if (isDailyViewEnabled)
                                    max(
                                        min(
                                            ((page - startPage) * daysToShow.toInt()) + startPage,
                                            numberOfPages
                                        ), 0
                                    )
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
            content = { padding ->
                val scrollState = rememberScrollState()
                val heightPx = with(LocalDensity.current) { 64.dp.roundToPx() }
                LaunchedEffect("key") { scrollState.animateScrollTo(LocalTime.now().hour * heightPx) }

                HorizontalPager(
                    count = numberOfPages,
                    state = pagerState,
                    modifier = Modifier.padding(top = padding.calculateTopPadding())
                ) { pageNum ->
                    TimetablePage(
                        navController = navController,
                        timetableViewModel = timetableViewModel,
                        page = pageNum - startPage,
                        scrollState = scrollState,
                        daysToShow = if (isDailyViewEnabled) 1 else daysToShow
                    )
                    page = pagerState.currentPage
                    Log.d("HorizontalPager", page.toString())
                }
                LaunchedEffect("key1") {
                    pagerState.scrollToPage(if (page == 0) startPage else page)
                }
            },
            floatingActionButton = {
                val adminGroups = timetableViewModel.adminGroups.value
                if (adminGroups.isNotEmpty()) {
                    TimetableFAB(
                        navController = navController,
                        timetableViewModel = timetableViewModel
                    )
                }
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
    Log.d("TimetablePage", "page: $page")
    val day = LocalDate.now().plusDays(page * daysToShow)
    Log.d("TimetablePage", "day: $day")
    // https://stackoverflow.com/questions/28450720/get-date-of-first-day-of-week-based-on-localdate-now-in-java-8
    val dayOfWeek = DayOfWeek.MONDAY
    val firstDateOfWeek = if (daysToShow == 7L) day.with(dayOfWeek) else day
    val lastDateOfWeek = firstDateOfWeek.plusDays(daysToShow)
    val events = timetableViewModel.events.value
    val eventsOfTheWeek = events.filter { event ->
        event.localStart.isAfter(firstDateOfWeek.atStartOfDay())
                && event.localEnd.isBefore(lastDateOfWeek.atStartOfDay())
    }
    val fontSize = max((20 - 2 * daysToShow).toInt(), 10).sp
    Column(
        Modifier
    ) {
        Schedule(
            events = eventsOfTheWeek,
            minDate = firstDateOfWeek,
            maxDate = lastDateOfWeek,
            eventContent = { event ->
                val group = timetableViewModel.findParentGroup(event)
                BasicEvent(event = event, fontSize = fontSize) {
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
    scrollState: ScrollState,
) {
    val days = ChronoUnit.DAYS.between(minDate, maxDate).toInt()
    val hourHeight = 64.dp
    var dayWidth by remember { mutableStateOf(0) }
    var sidebarWidth by remember { mutableStateOf(0) }
    Column(modifier = modifier.background(MaterialTheme.colorScheme.background)) {
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


@Composable
fun TimetableFAB(
    navController: NavController,
    timetableViewModel: TimetableViewModel,
) {
    var isDialogShown by remember { mutableStateOf(false) }
    val shownGroups = timetableViewModel.shownGroups.value
    val adminGroups = timetableViewModel.adminGroups.value
    FloatingActionButton(
        onClick = {
            if (shownGroups.size == 1) navController.navigate(
                AppScreens.EditEventScreen.route
                        + "/${shownGroups[0].id}"
            )
            else
                isDialogShown = true
        }) {
        Text("+")
    }
    if (isDialogShown) {
        androidx.compose.material.AlertDialog(
            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            onDismissRequest = { isDialogShown = false },
            title = {
                Text(
                    stringResource(id = R.string.select_a_group),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            buttons = {
                LazyColumn {
                    items(adminGroups) { group ->
                        GroupRow(
                            onDestinationClicked = {
                                navController.navigate(
                                    AppScreens.EditEventScreen.route
                                            + "/${group.id}"
                                )
                            },
                            group = group,
                        )
                    }
                }
            }
        )
    }
}
