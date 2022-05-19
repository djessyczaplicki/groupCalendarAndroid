package com.djessyczaplicki.groupcalendar.ui.screen.timetable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.djessyczaplicki.groupcalendar.ui.theme.GroupCalendarTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val DayFormatter = DateTimeFormatter.ofPattern("EE, d MMM yy")


@Composable
fun BasicDayHeader(
    day: LocalDate,
    modifier: Modifier = Modifier
) {
    val isToday by remember {
        mutableStateOf(day == LocalDate.now())
    }
    Text(
        text = day.format(DayFormatter),
        textAlign = TextAlign.Center,
        fontWeight = if (isToday) FontWeight.Bold else FontWeight.SemiBold,
        textDecoration = if (isToday) TextDecoration.Underline else TextDecoration.None,
        fontSize = 12.sp,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier
            .fillMaxWidth()
            .padding(2.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun BasicDayHeaderPreview() {
    GroupCalendarTheme {
        BasicDayHeader(day = LocalDate.now())
    }
}
