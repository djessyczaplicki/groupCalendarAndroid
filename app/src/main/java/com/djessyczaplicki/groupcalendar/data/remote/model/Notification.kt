package com.djessyczaplicki.groupcalendar.data.remote.model

data class Notification(
    var id: String = "0",
    var name: String = "default",
    var description: String = "default",
    var event: Event? = null
)