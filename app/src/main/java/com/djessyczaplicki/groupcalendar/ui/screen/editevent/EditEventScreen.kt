package com.djessyczaplicki.groupcalendar.ui.screen.editevent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.djessyczaplicki.groupcalendar.R
import com.djessyczaplicki.groupcalendar.data.local.CustomColor
import com.djessyczaplicki.groupcalendar.data.remote.model.Event
import com.djessyczaplicki.groupcalendar.ui.item.ColorPicker
import com.djessyczaplicki.groupcalendar.ui.item.DatePickerPopup
import com.djessyczaplicki.groupcalendar.ui.item.LabelledCheckbox
import com.djessyczaplicki.groupcalendar.ui.item.TimePickerPopup
import com.djessyczaplicki.groupcalendar.util.formatted
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*

@Composable
fun EditEventScreen(
    navController: NavController,
    editEventViewModel: EditEventViewModel
) {
    val isEditing = editEventViewModel.isEditing.value
    val group = editEventViewModel.group.value
    val event = editEventViewModel.event.value
    var name by rememberSaveable { mutableStateOf(event.name) }
    var description by rememberSaveable { mutableStateOf(event.description ?: "") }
    var color by remember { mutableStateOf(event.color.toComposeColor().value) }
    var startDate by rememberSaveable { mutableStateOf(event.start) }
    var startHour by rememberSaveable { mutableStateOf(event.start.hour.toLong())}
    var startMinute by rememberSaveable { mutableStateOf(event.start.minute.toLong())}
    var startTimeIsSet by rememberSaveable { mutableStateOf(isEditing) }
    var endHour by rememberSaveable { mutableStateOf(event.end.hour.toLong())}
    var endMinute by rememberSaveable { mutableStateOf(event.end.minute.toLong())}
    var endTimeIsSet by rememberSaveable { mutableStateOf(isEditing) }
    var isRecurrent by rememberSaveable { mutableStateOf(event.recurrenceId != null) }
    var daysExpanded by rememberSaveable { mutableStateOf(false) }
    var selectedDay by rememberSaveable { mutableStateOf(event.start.dayOfWeek.formatted()) }
    var requireConfirmation by rememberSaveable { mutableStateOf(false) }

    Column(
        Modifier.verticalScroll(rememberScrollState())
    ) {

        Text(
            text = stringResource(id = if (!isEditing) R.string.add_event_to else R.string.edit_event_from) + " ${group.name}",
            modifier = Modifier
                .height(30.dp)
                .padding(4.dp),
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(id = R.string.name)) },
            placeholder = { Text(stringResource(id = R.string.event_name))},
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(4.dp)
        )
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(stringResource(id = R.string.description)) },
            placeholder = { Text(stringResource(id = R.string.event_description))},
            singleLine = false,
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth()
                .padding(4.dp)
        )

        ColorPicker(
            value = color,
            onValueChange = { color = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        )

        Row(
            modifier = Modifier
                .wrapContentHeight()
                .height(60.dp)
        ){
            if (!isRecurrent) {
                DatePickerPopup (
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(2f)
                        .fillMaxHeight()
                ) {
                    startDate = it.atStartOfDay()
                }
            } else {
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(4.dp)
                        .weight(2f),
                    onClick = { daysExpanded = !daysExpanded },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                ) {
                    Text(if (selectedDay == "") stringResource(id = R.string.select_week_day) else selectedDay)
                }

                DropdownMenu(
                    expanded = daysExpanded,
                    onDismissRequest = { daysExpanded = false },
                    modifier = Modifier
                ) {
                    DayOfWeek.values().forEach { day ->
                        DropdownMenuItem(
                            onClick = {
                                selectedDay = day.formatted()
                                daysExpanded = false
                            }
                        ) {
                            Text(day.formatted())
                        }
                    }
                }
            }

            TimePickerPopup(
                text = stringResource(id = R.string.start_time),
                isSet = startTimeIsSet,
                hour = startHour,
                minute = startMinute,
                modifier = Modifier
                    .padding(4.dp)
                    .weight(1f)
                    .fillMaxHeight()

            ) { hour, minute ->
                startHour = hour
                startMinute = minute
                startTimeIsSet = true
                if (!endTimeIsSet) {
                    endHour = startHour
                    if (endHour != 23L) endHour++

                    endMinute = startMinute
                    endTimeIsSet = true
                }
                if ((endHour < startHour) || endHour == startHour && endMinute < startMinute) {
                    endHour = startHour
                    endMinute = startMinute
                }
            }

            TimePickerPopup(
                text = stringResource(id = R.string.end_time),
                isSet = startTimeIsSet,
                hour = endHour,
                minute = endMinute,
                modifier = Modifier
                    .padding(4.dp)
                    .weight(1f)
                    .fillMaxHeight()

            ) { hour, minute ->
                endTimeIsSet = true
                endHour = hour
                endMinute = minute
                if ((startHour > endHour) || startHour == endHour && startMinute > endMinute) {
                    startHour = endHour
                    startMinute = endMinute
                }
            }
        }

        var isDialogOpen by remember { mutableStateOf(false) }
        LabelledCheckbox(
            checked = isRecurrent,
            onCheckedChange = {
                if (isEditing && isRecurrent && event.recurrenceId != null) {
                    isDialogOpen = true
                } else {
                    isRecurrent = it
                }
            },
            label = stringResource(R.string.is_recurrent)
        )
        if (isDialogOpen) {
            AlertDialog(
                onDismissRequest = { isDialogOpen = false },
                title = {
                    Text(stringResource(id = R.string.recurrence_removal))
                },
                text = {
                    Text(stringResource(id = R.string.recurrence_removal_text))
                },
                confirmButton = {
                    TextButton(onClick = {
                        isDialogOpen = false
                        isRecurrent = false
                    }) {
                        Text(stringResource(id = R.string.proceed))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        isDialogOpen = false
                    }) {
                        Text(stringResource(id = R.string.cancel))
                    }
                }
            )
        }

        LabelledCheckbox(
            checked = requireConfirmation,
            onCheckedChange = { requireConfirmation = it },
            label = stringResource(R.string.require_confirmation)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (startTimeIsSet && endTimeIsSet && name.isNotBlank()) {
            Button(
                modifier = Modifier
                    .padding(4.dp)
                    .height(60.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = {
                    if (!isRecurrent) {
                        val startDateTime = startDate.withHour(startHour.toInt()).withMinute(startMinute.toInt())
                        val endDateTime = startDate.withHour(endHour.toInt()).withMinute(endMinute.toInt())
                        val newEvent = Event(
                            name = name,
                            description = description,
                            color = CustomColor.from(color),
                            start = startDateTime,
                            end = endDateTime,
                            requireConfirmation = requireConfirmation
                        )

                        if (isEditing) {
                            val editedEvent = newEvent.also { it.id = event.id }
                            editEventViewModel.editEvent(
                                editedEvent = editedEvent
                            ) {
                                navController.popBackStack()
                            }
                        } else {
                            editEventViewModel.createEvent(
                                newEvent = newEvent
                            ) {
                                navController.popBackStack()
                            }
                        }

                    } else {
                        val dayOfWeek = DayOfWeek.values().find { it.formatted() == selectedDay }
                        startDate = LocalDate.now().with(dayOfWeek).atStartOfDay()

                        if (isEditing) {
                            editEventViewModel.editRecurrentEvent(
                                eventTemplate = Event(
                                    name = name,
                                    description = description,
                                    color = CustomColor.from(color),
                                    start = startDate.withHour(startHour.toInt())
                                        .withMinute(startMinute.toInt()),
                                    end = startDate.withHour(endHour.toInt())
                                        .withMinute(endMinute.toInt()),
                                    recurrenceId = event.recurrenceId,
                                    requireConfirmation = requireConfirmation
                                )
                            ) {
                                navController.popBackStack()
                            }
                        } else {
                            val recurrentId = UUID.randomUUID().toString()
                            val newEvents = mutableListOf<Event>()
                            for (i in 0 until 5) {
                                val startDateTime = startDate.plusWeeks(i.toLong()).withHour(startHour.toInt())
                                    .withMinute(startMinute.toInt())
                                val endDateTime = startDate.plusWeeks(i.toLong()).withHour(endHour.toInt())
                                    .withMinute(endMinute.toInt())
                                newEvents.add(
                                    Event(
                                        name = name,
                                        description = description,
                                        color = CustomColor.from(color),
                                        start = startDateTime,
                                        end = endDateTime,
                                        recurrenceId = recurrentId,
                                        requireConfirmation = requireConfirmation
                                    )
                                )
                            }
                            editEventViewModel.createRecurrentEvent(newEvents) {
                                navController.popBackStack()
                            }
                        }
                    }
                }
            ) {
                Text(stringResource(id = if (isEditing) R.string.edit_event else R.string.create_event))
            }
        }

    }
}


@Preview(showBackground = true)
@Composable
fun AddEventScreenPreview() {
    EditEventScreen(
        rememberNavController(),
        EditEventViewModel()
    )
}