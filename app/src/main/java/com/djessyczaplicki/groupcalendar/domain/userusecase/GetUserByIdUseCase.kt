package com.djessyczaplicki.groupcalendar.domain.userusecase

import com.djessyczaplicki.groupcalendar.data.network.Repository
import com.djessyczaplicki.groupcalendar.data.remote.model.User
import javax.inject.Inject

class GetUserByIdUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(id: String): User? = repository.getUserById(id)
}