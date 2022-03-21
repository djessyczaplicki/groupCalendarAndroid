package com.djessyczaplicki.groupcalendar.domain.groupusecase

import com.djessyczaplicki.groupcalendar.data.network.Service
import com.djessyczaplicki.groupcalendar.data.remote.model.Event
import com.djessyczaplicki.groupcalendar.data.remote.model.Group

class UpdateGroupUseCase {
    private val api = Service()
    suspend operator fun invoke(group: Group): Group = api.updateGroup(group)
}