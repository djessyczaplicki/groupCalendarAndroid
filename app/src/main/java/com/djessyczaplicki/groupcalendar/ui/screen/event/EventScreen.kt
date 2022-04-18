package com.djessyczaplicki.groupcalendar.ui.screen.event

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.djessyczaplicki.groupcalendar.R
import com.djessyczaplicki.groupcalendar.data.remote.model.Event
import com.djessyczaplicki.groupcalendar.ui.item.CollapsingTopBar
import com.djessyczaplicki.groupcalendar.ui.item.EventUserRow
import com.djessyczaplicki.groupcalendar.ui.screen.AppScreens
import com.djessyczaplicki.groupcalendar.util.formatMinute
import com.djessyczaplicki.groupcalendar.util.formatted
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreen(
    navController: NavController,
    eventViewModel: EventViewModel
) {
    val event = eventViewModel.event.value
    val uid = Firebase.auth.currentUser?.uid
    val group = eventViewModel.group.value
    val context = LocalContext.current
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
                    eventViewModel.share(context)
                },
                onEdit = {
                    navController.navigate(AppScreens.EditEventScreen.route + "/${group.id}/${event.id}")
                },
                showEdit = group.admins.contains(uid),
                contentColor = if (event.color.toComposeColor()
                        .luminance() > .5
                ) Color.Black else Color.White
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


@OptIn(ExperimentalMaterial3Api::class)
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
            ) {
                Icon(Icons.Filled.Textsms, "description", Modifier.padding(8.dp))
                Text(
                    text = event.description!!,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        if (event.recurrenceId != null) {
            Text(
                text = stringResource(id = R.string.this_is_a_recurrent_event),
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 40.dp, vertical = 4.dp)
            )
        }

        Divider(thickness = 0.5.dp)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row {
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

            IconButton(onClick = {
                eventViewModel.setAlarm()
            }) {
                ElevatedCard(shape = CircleShape) {
                    Icon(Icons.Filled.AlarmAdd, "set alarm", Modifier.padding(8.dp))
                }
            }
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

        Divider(thickness = 0.5.dp)
        if (event.requireConfirmation) {
            Text(
                text = stringResource(id = R.string.this_event_requires_confirmation),
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 40.dp, vertical = 4.dp)
            )
        }
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            val uid = Firebase.auth.currentUser?.uid ?: ""
            if (event.confirmedUsers.contains(uid)) {
                Text(stringResource(id = R.string.deny_attendance), Modifier.padding(8.dp))
                IconButton(onClick = {
                    eventViewModel.denyAttendance()
                }) {
                    ElevatedCard(shape = CircleShape) {
                        Icon(Icons.Filled.DoNotDisturb, "deny", Modifier.padding(8.dp))
                    }
                }
            } else {
                Text(stringResource(id = R.string.confirm_attendance), Modifier.padding(8.dp))
                IconButton(onClick = {
                    eventViewModel.confirmAttendance()
                }) {
                    ElevatedCard(shape = CircleShape) {
                        Icon(Icons.Filled.TaskAlt, "accept", Modifier.padding(8.dp))
                    }
                }
            }
        }


        val confirmedUsers = eventViewModel.confirmedUsers.value

        Text(
            stringResource(id = R.string.confirmed_users) + " (${confirmedUsers.size}):",
            Modifier.padding(8.dp)
        )

        LazyColumn(
            Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            items(confirmedUsers) { user ->
                EventUserRow(user)
            }
        }


    }
}

@Preview(showBackground = true)
@Composable
fun EventScreenPreview() {
    EventScreen(rememberNavController(), viewModel())
}

@Composable
fun StartHour(event: Event, modifier: Modifier) {
    Text(
        text = buildAnnotatedString {
            append(stringResource(id = R.string.start_time) + ": ")
            pushStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.SemiBold
                )
            )
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
