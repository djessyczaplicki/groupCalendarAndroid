package com.djessyczaplicki.groupcalendar.domain.groupusecase

import com.djessyczaplicki.groupcalendar.data.network.Repository
import com.djessyczaplicki.groupcalendar.data.remote.model.Group
import javax.inject.Inject

class UpdateGroupUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(group: Group): Group = repository.updateGroup(group)
}