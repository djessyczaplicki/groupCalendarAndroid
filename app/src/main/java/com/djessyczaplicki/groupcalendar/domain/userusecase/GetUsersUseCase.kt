package com.djessyczaplicki.groupcalendar.domain.userusecase

import com.djessyczaplicki.groupcalendar.data.remote.model.User
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(private val getUserByIdUseCase: GetUserByIdUseCase) {
    suspend operator fun invoke(userIds: List<String>): List<User> {
        val users = mutableListOf<User>()
        userIds.forEach { userId ->
            val user = getUserByIdUseCase(userId)
            if (user != null) {
                users += user
            }
        }
        return users
    }
}