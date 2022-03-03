package com.djessyczaplicki.groupcalendar.data.remote.model

import androidx.compose.ui.graphics.Color
import java.time.LocalDateTime
import java.util.*

data class Event(
    var id: String = "0",
    var recurrentId: String? = null,
    var name: String = "default",
    var description: String? = null,
    var start: LocalDateTime = LocalDateTime.now(),
    var end: LocalDateTime = LocalDateTime.now().plusHours(1),
    var color: Color = Color(0xFFE1BEE7),
    var requireConfirmation: Boolean = false,
    var confirmedUsers: List<String> = emptyList()
)