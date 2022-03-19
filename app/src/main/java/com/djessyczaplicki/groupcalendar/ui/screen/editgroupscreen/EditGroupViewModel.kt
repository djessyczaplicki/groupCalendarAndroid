package com.djessyczaplicki.groupcalendar.ui.screen.editgroupscreen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djessyczaplicki.groupcalendar.data.remote.model.Group
import com.djessyczaplicki.groupcalendar.domain.groupusecase.GetGroupByIdUseCase
import kotlinx.coroutines.launch

class EditGroupViewModel : ViewModel() {
    var groupId: String? = null

    val getGroupByIdUseCase = GetGroupByIdUseCase()

    val group = mutableStateOf(Group())

    fun loadGroup(groupId: String) {
        this.groupId = groupId
        viewModelScope.launch {
            group.value = getGroupByIdUseCase(groupId)
        }
    }

}