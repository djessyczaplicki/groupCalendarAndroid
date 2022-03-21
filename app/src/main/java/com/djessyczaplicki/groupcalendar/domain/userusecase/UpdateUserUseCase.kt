package com.djessyczaplicki.groupcalendar.domain.userusecase

import com.djessyczaplicki.groupcalendar.data.network.Service
import com.djessyczaplicki.groupcalendar.data.remote.model.Event
import com.djessyczaplicki.groupcalendar.data.remote.model.Group
import com.djessyczaplicki.groupcalendar.data.remote.model.User

class UpdateUserUseCase {
    private val api = Service()
    suspend operator fun invoke(user: User): User = api.updateUser(user)
}