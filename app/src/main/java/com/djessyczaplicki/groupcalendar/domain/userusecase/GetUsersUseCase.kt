package com.djessyczaplicki.groupcalendar.domain.userusecase

import com.djessyczaplicki.groupcalendar.data.remote.model.User

class GetUsersUseCase {
    private val getUserByIdUseCase = GetUserByIdUseCase()
    suspend operator fun invoke(userIds: List<String>): List<User> {
        val users = mutableListOf<User>()
        userIds.forEach { userId ->
            users += getUserByIdUseCase(userId)
        }
        return users
    }
}