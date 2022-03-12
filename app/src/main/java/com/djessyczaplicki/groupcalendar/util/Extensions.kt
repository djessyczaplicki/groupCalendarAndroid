package com.djessyczaplicki.groupcalendar.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

fun Double.format(digits: Int) = "%.${digits}f".format(this)
fun Int.formatMinute(): String {
    if (this > 9) return this.toString()
    return "0$this"
}
fun DayOfWeek.formatted() = this.getDisplayName(TextStyle.FULL, Locale.getDefault())
    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }