package com.djessyczaplicki.groupcalendar.domain.userusecase

import com.djessyczaplicki.groupcalendar.data.network.Repository
import com.djessyczaplicki.groupcalendar.data.remote.model.User
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(user: User): User = repository.updateUser(user)
}