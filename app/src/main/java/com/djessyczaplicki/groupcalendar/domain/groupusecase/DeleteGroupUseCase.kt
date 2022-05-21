package com.djessyczaplicki.groupcalendar.domain.groupusecase

import com.djessyczaplicki.groupcalendar.data.network.Repository
import javax.inject.Inject

class DeleteGroupUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(id: String) = repository.deleteGroupById(id)
}
