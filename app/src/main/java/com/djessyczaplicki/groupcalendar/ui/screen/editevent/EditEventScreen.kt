package com.djessyczaplicki.groupcalendar.ui.screen.editevent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.djessyczaplicki.groupcalendar.R
import com.djessyczaplicki.groupcalendar.data.local.CustomColor
import com.djessyczaplicki.groupcalendar.data.local.WeekDayCheck
import com.djessyczaplicki.groupcalendar.data.remote.model.Event
import com.djessyczaplicki.groupcalendar.ui.item.ColorPicker
import com.djessyczaplicki.groupcalendar.ui.item.DatePickerPopup
import com.djessyczaplicki.groupcalendar.ui.item.LabelledSwitch
import com.djessyczaplicki.groupcalendar.ui.item.TimePickerPopup
import com.google.accompanist.flowlayout.FlowRow
import java.time.LocalDateTime
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventScreen(
    navController: NavController,
    editEventViewModel: EditEventViewModel
) {
    val isEditing = editEventViewModel.isEditing.value
    val group = editEventViewModel.group.value
    val event = editEventViewModel.event.value
    val days = editEventViewModel.days
    var name by rememberSaveable { mutableStateOf(event.name) }
    var description by rememberSaveable { mutableStateOf(event.description ?: "") }
    var color by remember { mutableStateOf(event.color.toComposeColor().value) }
    var startDate by rememberSaveable { mutableStateOf(event.localStart) }
    var startHour by rememberSaveable { mutableStateOf(event.localStart.hour.toLong()) }
    var startMinute by rememberSaveable { mutableStateOf(event.localStart.minute.toLong()) }
    var startTimeIsSet by rememberSaveable { mutableStateOf(isEditing) }
    var endHour by rememberSaveable { mutableStateOf(event.localEnd.hour.toLong()) }
    var endMinute by rememberSaveable { mutableStateOf(event.localEnd.minute.toLong()) }
    var endTimeIsSet by rememberSaveable { mutableStateOf(isEditing) }
    var isRecurrent by rememberSaveable { mutableStateOf(event.recurrenceId != null) }
    var lastDate by rememberSaveable { mutableStateOf(event.localStart) }
    var requireConfirmation by rememberSaveable { mutableStateOf(false) }

    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 10.dp)
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
            placeholder = { Text(stringResource(id = R.string.event_name)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(4.dp)
        )
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(stringResource(id = R.string.description)) },
            placeholder = { Text(stringResource(id = R.string.event_description)) },
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

        FlowRow(
            modifier = Modifier
                .wrapContentHeight()
                .height(100.dp)
        ) {
            DatePickerPopup(
                defaultText = if (isRecurrent) stringResource(R.string.pick_start_date)
                else stringResource(R.string.pick_a_date),
                modifier = Modifier
                    .padding(4.dp)
                    .weight(2f)
                    .fillMaxHeight()
            ) {
                startDate = it.atStartOfDay()
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
            AnimatedVisibility(visible = isRecurrent) {
                DatePickerPopup(
                    defaultText = stringResource(id = R.string.pick_end_date),
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(2f)
                        .fillMaxHeight()
                ) {
                    lastDate = it.plusDays(1).atStartOfDay()
                }
            }
        }


        AnimatedVisibility(visible = isRecurrent) {
            FlowRow(
                Modifier
                    .padding(4.dp)
                    .height(100.dp)
            ) {
                days.forEach { day ->
                    FilterChip(
                        selected = day.isChecked,
                        onClick = {
                            days[days.indexOf(day)] =
                                days[days.indexOf(day)].copy(isChecked = !day.isChecked)
                        },
                        label = {
                            Text(day.day)
                        }
                    )
                    Spacer(
                        Modifier.padding(4.dp)
                    )
                }
            }
        }
        var isDialogOpen by remember { mutableStateOf(false) }
        LabelledSwitch(
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

        LabelledSwitch(
            checked = requireConfirmation,
            onCheckedChange = { requireConfirmation = it },
            label = stringResource(R.string.require_confirmation)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            enabled = startTimeIsSet && endTimeIsSet && name.isNotBlank() && ((lastDate > startDate && days.any(
                WeekDayCheck::isChecked
            )) || !isRecurrent),
            modifier = Modifier
                .padding(4.dp)
                .height(60.dp)
                .align(Alignment.CenterHorizontally),
            onClick = {
                if (!isRecurrent) {
                    val startDateTime =
                        startDate.withHour(startHour.toInt()).withMinute(startMinute.toInt())
                    val endDateTime =
                        startDate.withHour(endHour.toInt()).withMinute(endMinute.toInt())
                    val newEvent = Event(
                        name = name,
                        description = description,
                        color = CustomColor.from(color),
                        requireConfirmation = requireConfirmation
                    )
                    newEvent.localStart = startDateTime
                    newEvent.localEnd = endDateTime

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

                    if (isEditing) {
                        val eventTemplate = Event(
                            name = name,
                            description = description,
                            color = CustomColor.from(color),
                            recurrenceId = event.recurrenceId,
                            requireConfirmation = requireConfirmation
                        )
                        eventTemplate.localStart = startDate.withHour(startHour.toInt())
                            .withMinute(startMinute.toInt())
                        eventTemplate.localEnd = startDate.withHour(endHour.toInt())
                            .withMinute(endMinute.toInt())

                        editEventViewModel.editRecurrentEvent(
                            eventTemplate = eventTemplate
                        ) {
                            navController.popBackStack()
                        }
                    } else {
                        val recurrentId = UUID.randomUUID().toString()
                        val newEvents = mutableListOf<Event>()
                        var actualDay = LocalDateTime.from(startDate)
                        while (actualDay < lastDate) {
                            if (days.find { day -> day.dayOfWeek == actualDay.dayOfWeek }!!.isChecked) {
                                val startDateTime =
                                    actualDay.withHour(startHour.toInt())
                                        .withMinute(startMinute.toInt())
                                val endDateTime =
                                    actualDay.withHour(endHour.toInt())
                                        .withMinute(endMinute.toInt())
                                val newEvent = Event(
                                    name = name,
                                    description = description,
                                    color = CustomColor.from(color),
                                    recurrenceId = recurrentId,
                                    requireConfirmation = requireConfirmation,
                                )
                                newEvent.localStart = startDateTime
                                newEvent.localEnd = endDateTime

                                newEvents.add(
                                    newEvent
                                )
                            }
                            actualDay = actualDay.plusDays(1)
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


@Preview(showBackground = true)
@Composable
fun AddEventScreenPreview() {
    EditEventScreen(
        rememberNavController(),
        viewModel()
    )
}
