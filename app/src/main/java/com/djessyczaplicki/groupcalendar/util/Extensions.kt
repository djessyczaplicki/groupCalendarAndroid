package com.djessyczaplicki.groupcalendar.util

import com.djessyczaplicki.groupcalendar.GroupCalendarApp
import com.djessyczaplicki.groupcalendar.R
import com.djessyczaplicki.groupcalendar.data.remote.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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
fun User.fullNameYou(userId: String): String {
    val context = GroupCalendarApp.applicationContext()
    val uid = Firebase.auth.currentUser!!.uid
    var output = this.fullName()
    if (uid == userId) {
        output += " " + context.getString(R.string.you)
    }
    return output
}
