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
    lateinit var eventId: String

    val updateGroupEventsUseCase = UpdateGroupEventsUseCase()
    val getGroupByIdUseCase = GetGroupByIdUseCase()

    val group = mutableStateOf(Group())
    val event = mutableStateOf(Event())
    val isEditing = mutableStateOf(false)

    fun loadEvent(groupId: String, eventId: String) {
        this.groupId = groupId
        this.eventId = eventId
        viewModelScope.launch {
            group.value = getGroupByIdUseCase(groupId) ?: return@launch
            event.value = group.value.events.find{ it.id == eventId } ?: Event()
        }
    }

    fun createEvent(newEvent: Event, onSuccessCallback: () -> Unit) {
        viewModelScope.launch {
            group.value = getGroupByIdUseCase(groupId)!!
            group.value.events += newEvent
            updateGroupEventsUseCase(group.value)
            onSuccessCallback()
        }
    }

    fun createRecurrentEvent(newEvents: MutableList<Event>, onSuccessCallback: () -> Unit) {
        viewModelScope.launch {
            group.value = getGroupByIdUseCase(groupId)!!
            group.value.events += newEvents
            updateGroupEventsUseCase(group.value)
            onSuccessCallback()
        }
    }

    fun editEvent(editedEvent: Event, onSuccessCallback: () -> Unit) {
        viewModelScope.launch {
            group.value = getGroupByIdUseCase(groupId)!!
            val event = group.value.events.find{ it.id == eventId } ?: Event()
            event.name = editedEvent.name
            event.description = editedEvent.description
            event.color = editedEvent.color
            event.recurrenceId = editedEvent.recurrenceId
            event.start = editedEvent.start
            event.requireConfirmation = editedEvent.requireConfirmation
            event.end = editedEvent.end

            updateGroupEventsUseCase(group.value)
            onSuccessCallback()
        }

    }

    fun editRecurrentEvent(eventTemplate: Event, onSuccessCallback: () -> Unit) {
        viewModelScope.launch {
            group.value = getGroupByIdUseCase(groupId)!!
            val allEvents = group.value.events
            val recurrentEvents = allEvents.filter{ recurrentEvent -> recurrentEvent.recurrenceId == eventTemplate.recurrenceId }
            recurrentEvents.forEach { event ->
                event.start = event.start.with(eventTemplate.start.dayOfWeek).withHour(eventTemplate.start.hour).withMinute(eventTemplate.start.minute)
                event.end = event.end.with(eventTemplate.end.dayOfWeek).withHour(eventTemplate.end.hour).withMinute(eventTemplate.end.minute)
                event.color = eventTemplate.color
                event.name = eventTemplate.name
                event.requireConfirmation = eventTemplate.requireConfirmation
                event.description = eventTemplate.description
            }
            updateGroupEventsUseCase(group.value)
            onSuccessCallback()
        }
    }


}