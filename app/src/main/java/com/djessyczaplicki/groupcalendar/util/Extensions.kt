package com.djessyczaplicki.groupcalendar.util

import com.djessyczaplicki.groupcalendar.data.remote.model.User
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

fun String.capitalize() = this.replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(
        Locale.getDefault()
    ) else it.toString()
}


fun User.fullName(): String = "${this.name.capitalize()} ${this.surname.capitalize()}"