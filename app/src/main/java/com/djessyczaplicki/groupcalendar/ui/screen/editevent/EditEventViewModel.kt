package com.djessyczaplicki.groupcalendar.ui.screen.editevent

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djessyczaplicki.groupcalendar.data.remote.model.Event
import com.djessyczaplicki.groupcalendar.data.remote.model.Group
import com.djessyczaplicki.groupcalendar.domain.eventusecase.UpdateGroupEventsUseCase
import com.djessyczaplicki.groupcalendar.domain.groupusecase.GetGroupByIdUseCase
import kotlinx.coroutines.launch

class EditEventViewModel : ViewModel() {
    lateinit var groupId: String

    val updateGroupEventsUseCase = UpdateGroupEventsUseCase()
    val getGroupByIdUseCase = GetGroupByIdUseCase()

    val group = mutableStateOf(Group())
    val event = mutableStateOf(Event())
    val isEditing = mutableStateOf(false)

    fun loadEvent(groupId: String, eventId: String) {
        this.groupId = groupId
        viewModelScope.launch {
            group.value = getGroupByIdUseCase(groupId)
            event.value = group.value.events.find{ it.id == eventId } ?: Event()
        }
    }

    fun createEvent(event: Event, onSuccessCallback: () -> Unit) {
        viewModelScope.launch {
            group.value = getGroupByIdUseCase(groupId)
            group.value.events += event
            updateGroupEventsUseCase(group.value)
            onSuccessCallback()
        }
    }

    fun createRecurrentEvent(newEvents: MutableList<Event>, onSuccessCallback: () -> Unit) {
        viewModelScope.launch {
            group.value = getGroupByIdUseCase(groupId)
            group.value.events += newEvents
            updateGroupEventsUseCase(group.value)
            onSuccessCallback()
        }
    }


}