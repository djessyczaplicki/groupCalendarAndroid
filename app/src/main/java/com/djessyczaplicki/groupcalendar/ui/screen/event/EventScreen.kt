package com.djessyczaplicki.groupcalendar.ui.screen.event

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Textsms
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.djessyczaplicki.groupcalendar.R
import com.djessyczaplicki.groupcalendar.data.remote.model.Event
import com.djessyczaplicki.groupcalendar.ui.item.CollapsingTopBar
import com.djessyczaplicki.groupcalendar.ui.screen.AppScreens
import com.djessyczaplicki.groupcalendar.util.formatMinute
import com.djessyczaplicki.groupcalendar.util.formatted

@Composable
fun EventScreen(
    navController: NavController,
    eventViewModel: EventViewModel
) {
    val event = eventViewModel.event.value
    val group = eventViewModel.group.value
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    Scaffold(
        topBar = {
            CollapsingTopBar(
                modifier = Modifier
                    .background(event.color.toComposeColor())
                    .padding(4.dp),
                title = event.name,
                currentHeight = 90.dp,
                onBack = {
                    navController.popBackStack()
                },
                onShare = {
                    /* TODO */
                },
                onEdit = {
                    navController.navigate(AppScreens.EditEventScreen.route + "/${group.id}/${event.id}")
                }
            )
        },
        content = {
            EventScreenContent(
                navController = navController,
                eventViewModel = eventViewModel
            )
        },
        floatingActionButton = {
            EventScreenFAB(
                navController,
                eventViewModel
            )
        }
    )
}


@Composable
fun EventScreenContent(
    navController: NavController,
    eventViewModel: EventViewModel
) {
    val event = eventViewModel.event.value
    Column(
        Modifier.verticalScroll(rememberScrollState())
    ) {
        if (!event.description.isNullOrBlank()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ){
                Icon(Icons.Filled.Textsms, "description", Modifier.padding(8.dp))
                Text(
                    text = event.description!!,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        if (event.recurrenceId != null){
            Text(
                text = stringResource(id = R.string.this_is_a_recurrent_event),
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 40.dp, vertical = 4.dp)
            )
        }

        Divider(thickness = 1.dp)

        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Icon(Icons.Filled.Event, "Date", Modifier.padding(8.dp))
            Text(
                text = buildAnnotatedString {
                    append(stringResource(id = R.string.day) + ": ")
                    pushStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    append("${event.start.dayOfWeek.formatted()}, ${event.start.dayOfMonth}/${event.start.monthValue}/${event.start.year}")
                },
                modifier = Modifier.padding(4.dp)
            )
        }


        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Icon(Icons.Filled.Schedule, "Hour", Modifier.padding(8.dp))
            StartHour(event = event, modifier = Modifier.weight(1f))
            EndHour(event = event, modifier = Modifier.weight(1f))
        }

    }
}

@Preview(showBackground = true)
@Composable
fun EventScreenPreview() {
    val vm = EventViewModel()
    vm.event.value = Event(name = "Evento prueba", recurrenceId = "2", description = "Lorem Ipsum es simplemente el texto de relleno de las imprentas y archivos de texto. Lorem Ipsum ha sido el texto de relleno estándar de las industrias desde el año 1500,")
    EventScreen(rememberNavController(), vm)
}

@Composable
fun StartHour(event: Event, modifier: Modifier) {
    Text(
        text = buildAnnotatedString {
            append(stringResource(id = R.string.start_time) + ": ")
            pushStyle(style = SpanStyle(
                fontWeight = FontWeight.SemiBold
            ))
            append("${event.start.hour}:${event.start.minute.formatMinute()}")
        },
        modifier = Modifier
            .padding(4.dp)
    )
}

@Composable
fun EndHour(event: Event, modifier: Modifier) {
    Text(
        text = buildAnnotatedString {
            append(stringResource(id = R.string.end_time) + ": ")
            pushStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.SemiBold
                )
            )
            append("${event.end.hour}:${event.end.minute.formatMinute()}")
        },
        modifier = modifier
            .padding(4.dp)
    )
}

@Composable
fun EventScreenFAB(navController: NavController, eventViewModel: EventViewModel) {
    var isDeleteAllDialogVisible by remember { mutableStateOf(false) }
    var isConfirmDialogVisible by remember { mutableStateOf(false) }
    FloatingActionButton(
        onClick = {
            if (eventViewModel.event.value.recurrenceId != null) {
                isDeleteAllDialogVisible = true
            } else {
                isConfirmDialogVisible = true
            }
        }
    ) {
        Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete")
    }

    if (isConfirmDialogVisible) {
        AlertDialog(onDismissRequest = { isConfirmDialogVisible = false },
            title = {
                Text(stringResource(id = R.string.delete_event))
            },
            text = {
                Text(stringResource(id = R.string.delete_event_text))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        isConfirmDialogVisible = false
                        eventViewModel.delete {
                            navController.popBackStack()
                        }
                    }
                ) {
                    Text(stringResource(id = R.string.delete))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        isConfirmDialogVisible = false
                    }
                ) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }

    if (isDeleteAllDialogVisible) {
        AlertDialog(
            onDismissRequest = { isDeleteAllDialogVisible = false },
            title = {
                Text(stringResource(id = R.string.delete_events))
            },
            text = {
                Text(stringResource(id = R.string.delete_events_text))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        isDeleteAllDialogVisible = false
                        eventViewModel.deleteAll {
                            navController.popBackStack()
                        }
                    }
                ) {
                    Text(stringResource(id = R.string.delete_all_events))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        isDeleteAllDialogVisible = false
                        eventViewModel.delete {
                            navController.popBackStack()
                        }
                    }
                ) {
                    Text(stringResource(id = R.string.delete_one_event))
                }
            }
        )
    }
}