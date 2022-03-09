package com.djessyczaplicki.groupcalendar.ui.screen.timetable

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djessyczaplicki.groupcalendar.data.remote.model.Event
import com.djessyczaplicki.groupcalendar.data.remote.model.Group
import com.djessyczaplicki.groupcalendar.domain.eventusecase.UpdateGroupEventsUseCase
import com.djessyczaplicki.groupcalendar.domain.groupusecase.GetGroupsUseCase
import kotlinx.coroutines.launch

class TimetableViewModel: ViewModel() {
    val getGroupsUseCase = GetGroupsUseCase()
    val groups = mutableStateOf(listOf<Group>())

    val updateGroupEventsUseCase = UpdateGroupEventsUseCase()

    fun loadGroups() {
        viewModelScope.launch {
            val result = getGroupsUseCase()
            if (result.isNotEmpty()) {
               groups.value = result.toMutableList().sortedBy { it.name }
            }
        }
    }

    fun removeEvent(event: Event, group: Group) {
        viewModelScope.launch {
            group.events.remove(event)
            updateGroupEventsUseCase(group)
            loadGroups()
        }
    }
}