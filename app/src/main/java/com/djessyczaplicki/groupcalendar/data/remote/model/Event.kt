package com.djessyczaplicki.groupcalendar.data.remote.model

import com.djessyczaplicki.groupcalendar.data.local.CustomColor
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

data class Event(
    var id: String = UUID.randomUUID().toString(),
    var recurrenceId: String? = null,
    var name: String = "",
    var description: String? = null,
    var start: Date = Date(),
    var end: Date = Date(),
    var color: CustomColor = CustomColor(83, 109, 254),
    var requireConfirmation: Boolean = false,
    var confirmedUsers: List<String> = emptyList()
) {
    var localStart: LocalDateTime
        get() = LocalDateTime.from(start.toInstant().atZone(ZoneId.systemDefault()))
        set(value) {
            start = Date.from(value.atZone(ZoneId.systemDefault()).toInstant())
        }

    var localEnd: LocalDateTime
        get() = LocalDateTime.from(end.toInstant().atZone(ZoneId.systemDefault()))
        set(value) {
            end = Date.from(value.atZone(ZoneId.systemDefault()).toInstant())
        }
}
