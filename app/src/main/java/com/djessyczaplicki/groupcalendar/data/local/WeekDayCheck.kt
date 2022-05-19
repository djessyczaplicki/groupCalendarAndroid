package com.djessyczaplicki.groupcalendar.data.local

import com.djessyczaplicki.groupcalendar.util.formatted
import java.time.DayOfWeek

data class WeekDayCheck(
    var dayOfWeek: DayOfWeek,
    var day: String = dayOfWeek.formatted(),
    var isChecked: Boolean = false
)
