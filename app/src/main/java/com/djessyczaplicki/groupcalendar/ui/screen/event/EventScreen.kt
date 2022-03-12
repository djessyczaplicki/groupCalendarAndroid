package com.djessyczaplicki.groupcalendar.ui.screen.event

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import com.djessyczaplicki.groupcalendar.util.formatMinute
import com.djessyczaplicki.groupcalendar.util.formatted
import java.time.format.TextStyle
import java.util.*

@Composable
fun EventScreen(
    navController: NavController,
    eventViewModel: EventViewModel
) {
    val event = eventViewModel.event.value
    Column(
        Modifier.verticalScroll(rememberScrollState())
    ) {
        Text(
            text = event.name,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(4.dp)
        )
        Divider(thickness = 1.dp)
        if (event.description != null) {
            Text(
                text = event.description!!,
                modifier = Modifier.padding(4.dp)
            )
        }

        if (event.recurrenceId != null){
            Text(
                text = stringResource(id = R.string.this_is_a_recurrent_event),
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.padding(4.dp)
            )
        }

        Divider(thickness = 1.dp)

        Text(
            text = stringResource(id = R.string.day)
                    + ": ${event.start.dayOfWeek.formatted()}, ${event.start.dayOfMonth}/${event.start.monthValue}/${event.start.year}",
            modifier = Modifier.padding(4.dp)
        )

        Row(
            Modifier.fillMaxWidth()
        ) {
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
        text = stringResource(id = R.string.end_time)
                + ": ${event.end.hour}:${event.end.minute.formatMinute()}",
        modifier = modifier
            .padding(4.dp)
    )
}