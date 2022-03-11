package com.djessyczaplicki.groupcalendar.ui.screen.addevent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.djessyczaplicki.groupcalendar.R
import com.djessyczaplicki.groupcalendar.data.remote.model.Event
import com.djessyczaplicki.groupcalendar.ui.item.ColorPicker
import com.djessyczaplicki.groupcalendar.ui.item.DatePickerPopup
import com.djessyczaplicki.groupcalendar.ui.item.LabelledCheckbox
import com.djessyczaplicki.groupcalendar.ui.item.TimePickerPopup
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.*

@Composable
fun AddEventScreen(
    navController: NavController,
    addEventViewModel: AddEventViewModel
) {
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var color by remember { mutableStateOf(0x0UL) }
    var startDate by rememberSaveable { mutableStateOf(LocalDateTime.now()) }
    var startHour by rememberSaveable { mutableStateOf(0L)}
    var startMinute by rememberSaveable { mutableStateOf(0L)}
    var startTimeIsSet by rememberSaveable { mutableStateOf(false) }
    var endHour by rememberSaveable { mutableStateOf(0L)}
    var endMinute by rememberSaveable { mutableStateOf(0L)}
    var endTimeIsSet by rememberSaveable { mutableStateOf(false) }
    var isRecurrent by rememberSaveable { mutableStateOf(false) }
    var daysExpanded by rememberSaveable { mutableStateOf(false) }
    var selectedDay by rememberSaveable { mutableStateOf("") }
    var requireConfirmation by rememberSaveable { mutableStateOf(false) }

    Column(
        Modifier.verticalScroll(rememberScrollState())
    ) {

        Text(
            text = stringResource(id = R.string.add_event_screen),
            modifier = Modifier
                .height(30.dp)
                .padding(4.dp),
            fontWeight = FontWeight.SemiBold
        )
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(id = R.string.title)) },
            placeholder = { Text(stringResource(id = R.string.event_title))},
            modifier = Modifier
                .fillMaxWidth()
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
                                selectedDay = day.getDisplayName(TextStyle.FULL, Locale.getDefault())
                                daysExpanded = false
                            }
                        ) {
                            Text(day.getDisplayName(TextStyle.FULL, Locale.getDefault()))
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
                    if (startHour == 23L) {
                        endHour = startHour
                    } else {
                        endHour = startHour + 1
                    }
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

        LabelledCheckbox(
            checked = isRecurrent,
            onCheckedChange = { isRecurrent = it },
            label = stringResource(R.string.is_recurrent)
        )

        LabelledCheckbox(
            checked = requireConfirmation,
            onCheckedChange = { requireConfirmation = it },
            label = stringResource(R.string.require_confirmation)
        )

        Spacer(modifier = Modifier.size(16.dp))

        Button(
            modifier = Modifier
                .padding(4.dp)
                .height(60.dp)
                .align(Alignment.CenterHorizontally),
            onClick = {
                if (!isRecurrent) {
                    val startDateTime = startDate.plusHours(startHour).plusMinutes(startMinute)
                    val endDateTime = startDate.plusHours(endHour).plusMinutes(endMinute)
                    addEventViewModel.createEvent(
                        Event(
                            name = title,
                            description = description,
                            color = Color(color),
                            start = startDateTime,
                            end = endDateTime
                        )
                    ) {
                        navController.popBackStack()
                    }
                } else {
                    val recurrentId = UUID.randomUUID().toString()
                    val newEvents = mutableListOf<Event>()
                    val dayOfWeek = DayOfWeek.values().find{ it.getDisplayName(TextStyle.FULL, Locale.getDefault()) == selectedDay }
                    startDate = LocalDate.now().with(dayOfWeek).atStartOfDay()
                    for (i in 0 until 5) {
                        val startDateTime = startDate.plusWeeks(i.toLong()).plusHours(startHour).plusMinutes(startMinute)
                        val endDateTime = startDate.plusWeeks(i.toLong()).plusHours(endHour).plusMinutes(endMinute)
                        newEvents.add(Event(
                            name = title,
                            description = description,
                            color = Color(color),
                            start = startDateTime,
                            end = endDateTime,
                            recurrenceId = recurrentId,
                            requireConfirmation = requireConfirmation
                        ))
                    }
                    addEventViewModel.createRecurrentEvent(newEvents){
                        navController.popBackStack()
                    }
                }

            }
        ) {
            Text(stringResource(id = R.string.create_event))
        }


    }
}


@Preview(showBackground = true)
@Composable
fun AddEventScreenPreview() {
    AddEventScreen(
        rememberNavController(),
        AddEventViewModel()
    )
}