package com.djessyczaplicki.groupcalendar.data.remote.model

data class Group(
    var id: String = "0",
    var admins: List<String> = emptyList(),
    var events: MutableList<Event> = mutableListOf(),
    var image: String = "",
    var name: String = "default",
    var users: List<String> = emptyList(),
    var notifications: List<Notification> = emptyList()
)