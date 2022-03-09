package com.djessyczaplicki.groupcalendar.domain.groupusecase

import com.djessyczaplicki.groupcalendar.data.network.Service
import com.djessyczaplicki.groupcalendar.data.remote.model.Group

class GetGroupByIdUseCase {
    private val api = Service()
    suspend operator fun invoke(id: String): Group = api.getGroupById(id)
}