package com.djessyczaplicki.groupcalendar.data.remote.model

import java.util.*

data class Event(
    var id: String = "0",
    var recurrentId: String? = null,
    var name: String = "default",
    var description: String? = null,
    var date: Long = Date().time,
    var duration: Int = 30,
    var color: String = "#433456",
    var requireConfirmation: Boolean = false,
    var confirmedUsers: List<String> = emptyList()
)