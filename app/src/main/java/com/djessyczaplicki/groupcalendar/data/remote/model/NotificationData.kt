package com.djessyczaplicki.groupcalendar.data.remote.model

data class NotificationData(
    var title: String,
    var body: String,
    val sound: String = "default",
    val content_available: Boolean = true,
    val priority: String = "high"
)
