package com.djessyczaplicki.groupcalendar.data.remote.model

import com.djessyczaplicki.groupcalendar.data.local.CustomColor
import java.time.LocalDateTime
import java.util.*

data class Event(
    var id: String = UUID.randomUUID().toString(),
    var recurrenceId: String? = null,
    var name: String = "",
    var description: String? = null,
    var start: LocalDateTime = LocalDateTime.now(),
    var end: LocalDateTime = LocalDateTime.now().plusHours(1),
    var color: CustomColor = CustomColor(255, 255, 200),
    var requireConfirmation: Boolean = false,
    var confirmedUsers: List<String> = emptyList()
) {
}
