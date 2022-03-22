package com.djessyczaplicki.groupcalendar.ui.screen


sealed class AppScreensVariables(val variable: String) {
    object GroupId: AppScreensVariables("/{group_id}")
    object GroupIds: AppScreensVariables("/{group_ids}")
    object EventId: AppScreensVariables("/{event_id}")
}
