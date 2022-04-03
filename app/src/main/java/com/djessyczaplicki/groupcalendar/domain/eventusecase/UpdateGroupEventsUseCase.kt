package com.djessyczaplicki.groupcalendar.domain.eventusecase

import com.djessyczaplicki.groupcalendar.data.network.Repository
import com.djessyczaplicki.groupcalendar.data.remote.model.Event
import com.djessyczaplicki.groupcalendar.data.remote.model.Group
import javax.inject.Inject

class UpdateGroupEventsUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(group: Group): List<Event> = repository.updateGroupEvents(group)
}