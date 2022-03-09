package com.djessyczaplicki.groupcalendar.ui.screen.addevent

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djessyczaplicki.groupcalendar.data.remote.model.Event
import com.djessyczaplicki.groupcalendar.data.remote.model.Group
import com.djessyczaplicki.groupcalendar.domain.eventusecase.UpdateGroupEventsUseCase
import com.djessyczaplicki.groupcalendar.domain.groupusecase.GetGroupByIdUseCase
import com.djessyczaplicki.groupcalendar.domain.groupusecase.GetGroupsUseCase
import kotlinx.coroutines.launch

class AddEventViewModel : ViewModel() {
    lateinit var groupId: String

    val updateGroupEventsUseCase = UpdateGroupEventsUseCase()
    val getGroupByIdUseCase = GetGroupByIdUseCase()

    val group = mutableStateOf(Group())

    fun loadGroup() {
        viewModelScope.launch {
            group.value = getGroupByIdUseCase(groupId)
        }
    }

    fun createEvent(event: Event) {
        viewModelScope.launch {
            group.value = getGroupByIdUseCase(groupId)
            event.id = group.value.events.size.toString() /* TODO change this */
            group.value.events += event
            updateGroupEventsUseCase(group.value)
        }
    }
}