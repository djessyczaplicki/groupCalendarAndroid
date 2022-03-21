package com.djessyczaplicki.groupcalendar.domain.userusecase

import com.djessyczaplicki.groupcalendar.data.network.Service
import com.djessyczaplicki.groupcalendar.data.remote.model.Group
import com.djessyczaplicki.groupcalendar.data.remote.model.User

class GetUserByIdUseCase {
    private val api = Service()
    suspend operator fun invoke(id: String): User = api.getUserById(id)
}