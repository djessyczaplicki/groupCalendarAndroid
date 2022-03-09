package com.djessyczaplicki.groupcalendar.domain.eventusecase

import com.djessyczaplicki.groupcalendar.data.network.Service
import com.djessyczaplicki.groupcalendar.data.remote.model.Event
import com.djessyczaplicki.groupcalendar.data.remote.model.Group

class UpdateGroupEventsUseCase {
    private val api = Service()
    suspend operator fun invoke(group: Group): List<Event> = api.updateGroupEvents(group)
}