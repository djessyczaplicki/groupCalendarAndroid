package com.djessyczaplicki.groupcalendar.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.djessyczaplicki.groupcalendar.data.remote.model.Event
import java.time.format.DateTimeFormatter

val EventTimeFormatter = DateTimeFormatter.ofPattern("H:mm")

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BasicEvent(
    event: Event,
    modifier: Modifier = Modifier,
    showDateOnEachEvent: Boolean = false,
    action: () -> Unit = {}
) {
    Card(
        elevation = 3.dp,
        modifier = modifier
            .fillMaxSize()
            .padding(1.dp),
        onClick = action
    ){
        Column(
            modifier = modifier
                .background(event.color, shape = RoundedCornerShape(4.dp))
        ) {
            val fontSize = 10.sp
            if (showDateOnEachEvent)
                Text(
                    text = "${event.start.format(EventTimeFormatter)} - ${event.end.format(EventTimeFormatter)}",
                    fontSize = fontSize,
                    style = MaterialTheme.typography.caption
                )

            Text(
                modifier = Modifier.padding(2.dp),
                text = event.name,
                fontSize = fontSize,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold
            )

            if (event.description != null) {
                Text(
                    modifier = Modifier.padding(1.dp),
                    text = event.description!!,
                    fontSize = fontSize,
                    style = MaterialTheme.typography.body2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview
@Composable
fun BasicEventPreview() {
    BasicEvent(event = Event(description = "Hola mundo, hoy vamos a comer ensalada con lechuga y tomate"))
}