package com.djessyczaplicki.groupcalendar.ui.item

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.djessyczaplicki.groupcalendar.data.remote.model.Event
import java.time.format.DateTimeFormatter

val EventTimeFormatter = DateTimeFormatter.ofPattern("H:mm")

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BasicEvent(
    event: Event,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 10.sp,
    showDateOnEachEvent: Boolean = false,
    action: () -> Unit = {}
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxSize()
            .padding(1.dp),
        shape = RoundedCornerShape(4.dp),
        onClick = action,
        colors = CardDefaults.elevatedCardColors(containerColor = event.color.toComposeColor())
    ) {
        val textColor =
            if (event.color.toComposeColor()
                    .luminance() < 0.5
            ) Color.White else Color.Black

        if (showDateOnEachEvent)
            Text(
                text = "${event.localStart.format(EventTimeFormatter)} - ${
                    event.localEnd.format(
                        EventTimeFormatter
                    )
                }",
                fontSize = fontSize,
                style = MaterialTheme.typography.bodySmall,
            )

        Text(
            modifier = Modifier.padding(2.dp),
            text = event.name,
            fontSize = fontSize,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = textColor
        )

        if (event.description != null) {
            Text(
                modifier = Modifier.padding(1.dp),
                text = event.description!!,
                fontSize = fontSize,
                style = MaterialTheme.typography.bodySmall,
                overflow = TextOverflow.Ellipsis,
                color = textColor
            )
        }

    }
}

@Preview
@Composable
fun BasicEventPreview() {
    BasicEvent(
        event = Event(
            name = "Juanito",
            description = "Hola mundo, hoy vamos a comer ensalada con lechuga y tomate"
        )
    )
}
