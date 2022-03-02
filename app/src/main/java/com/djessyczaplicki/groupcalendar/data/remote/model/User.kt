package com.djessyczaplicki.groupcalendar.data.remote.model

data class User(
    var id: String = "0",
    var username: String = "user",
    var name: String = "Name",
    var surname: String = "Surname",
    var age: Int? = null,
    var groups: List<String> = emptyList(),
    var followedEvents: List<String> = emptyList()
)