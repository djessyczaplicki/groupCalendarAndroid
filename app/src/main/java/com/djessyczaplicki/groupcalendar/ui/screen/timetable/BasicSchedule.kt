package com.djessyczaplicki.groupcalendar.ui.screen.timetable

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.djessyczaplicki.groupcalendar.data.remote.model.Event
import com.djessyczaplicki.groupcalendar.ui.item.BasicEvent
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

private fun Modifier.eventData(event: Event) = this.then(EventDataModifier(event))

private class EventDataModifier(
    val event: Event
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = event
}


@Composable
fun BasicSchedule(
    events: List<Event>?,
    modifier: Modifier = Modifier,
    eventContent: @Composable (event: Event) -> Unit = {
        BasicEvent(
            event = it
        )
    },
    minDate: LocalDate,
    maxDate: LocalDate,
    dayWidth: Dp,
    hourHeight: Dp
) {
    val numDays = ChronoUnit.DAYS.between(minDate, maxDate).toInt()
    val dividerColor = Color.LightGray
    Layout(
        content = {
            events?.sortedBy(Event::localStart)?.forEach { event ->
                Box(modifier = Modifier.eventData(event)) {
                    eventContent(event)
                }
            }
        },
        modifier = modifier
            .drawBehind {
                repeat(23) {
                    drawLine(
                        dividerColor,
                        start = Offset(0f, (it + 1) * hourHeight.toPx()),
                        end = Offset(size.width, (it + 1) * hourHeight.toPx()),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                repeat(numDays - 1) {
                    drawLine(
                        dividerColor,
                        start = Offset((it + 1) * dayWidth.toPx(), 0f),
                        end = Offset((it + 1) * dayWidth.toPx(), size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                val time = LocalTime.now()
                drawLine(
                    Color.Cyan,
                    start = Offset(
                        0f,
                        time.hour * hourHeight.toPx() + time.minute * hourHeight.toPx() / 60
                    ),
                    end = Offset(
                        size.width,
                        time.hour * hourHeight.toPx() + time.minute * hourHeight.toPx() / 60
                    ),
                    strokeWidth = 3.dp.toPx()
                )
            }
    ) { measurables, constraints ->
        val height = hourHeight.roundToPx() * 24
        val width = dayWidth.roundToPx() * numDays
        val placeablesWithEvents = measurables.map { measurable ->
            val event = measurable.parentData as Event
            val eventDurationMinutes = ChronoUnit.MINUTES.between(event.localStart, event.localEnd)
            val eventHeight = Integer.max(
                ((eventDurationMinutes.div(60f)) * hourHeight.toPx()).roundToInt(),
                50
            )
            val placeable = measurable.measure(
                constraints.copy(
                    minWidth = dayWidth.roundToPx(),
                    maxWidth = dayWidth.roundToPx(),
                    minHeight = eventHeight,
                    maxHeight = eventHeight
                )
            )
            Pair(placeable, event)
        }
        layout(width, height) {
            placeablesWithEvents.forEach { (placeable, event) ->
                val eventOffsetMinutes =
                    ChronoUnit.MINUTES.between(LocalTime.MIN, event.localStart.toLocalTime())
                val eventY = ((eventOffsetMinutes.div(60f)) * hourHeight.toPx()).roundToInt()
                val eventOffsetDays =
                    ChronoUnit.DAYS.between(minDate, event.localStart.toLocalDate()).toInt()
                val eventX = eventOffsetDays * dayWidth.roundToPx()
                placeable.place(eventX, eventY)
            }
        }
    }
}
