package com.djessyczaplicki.groupcalendar.domain.groupusecase

import com.djessyczaplicki.groupcalendar.data.network.Service
import com.djessyczaplicki.groupcalendar.data.remote.model.Group

class GetGroupsUseCase {
    private val api = Service()
    suspend operator fun invoke(): List<Group> = api.getAllGroups()
}