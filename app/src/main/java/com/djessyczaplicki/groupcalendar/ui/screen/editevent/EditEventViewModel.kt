package com.djessyczaplicki.groupcalendar.ui.screen.editevent

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djessyczaplicki.groupcalendar.data.remote.model.Event
import com.djessyczaplicki.groupcalendar.data.remote.model.Group
import com.djessyczaplicki.groupcalendar.domain.eventusecase.UpdateGroupEventsUseCase
import com.djessyczaplicki.groupcalendar.domain.groupusecase.GetGroupByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditEventViewModel @Inject constructor(
    private val updateGroupEventsUseCase: UpdateGroupEventsUseCase,
    private val getGroupByIdUseCase: GetGroupByIdUseCase
) : ViewModel() {
    lateinit var groupId: String
    lateinit var eventId: String

    val group = mutableStateOf(Group())
    val event = mutableStateOf(Event())
    val isEditing = mutableStateOf(false)

    fun loadEvent(groupId: String, eventId: String) {
        this.groupId = groupId
        this.eventId = eventId
        viewModelScope.launch {
            group.value = getGroupByIdUseCase(groupId) ?: return@launch
            event.value = group.value.events.find { it.id == eventId } ?: Event()
        }
    }

    fun loadGroup() {
        viewModelScope.launch {
            group.value = getGroupByIdUseCase(groupId) ?: return@launch
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
            val event = group.value.events.find { it.id == eventId } ?: Event()
            overwriteEventData(event, editedEvent)
            updateGroupEventsUseCase(group.value)
            onSuccessCallback()
        }

    }

    private fun overwriteEventData(
        event: Event,
        editedEvent: Event
    ) {
        event.name = editedEvent.name
        event.description = editedEvent.description
        event.color = editedEvent.color
        event.recurrenceId = editedEvent.recurrenceId
        event.localStart = editedEvent.localStart
        event.requireConfirmation = editedEvent.requireConfirmation
        event.localEnd = editedEvent.localEnd
    }

    fun editRecurrentEvent(eventTemplate: Event, onSuccessCallback: () -> Unit) {
        viewModelScope.launch {
            group.value = getGroupByIdUseCase(groupId)!!
            val allEvents = group.value.events
            val recurrentEvents = allEvents.filter { recurrentEvent ->
                recurrentEvent.recurrenceId == eventTemplate.recurrenceId
            }
            recurrentEvents.forEach { event ->
                overwriteRecurrentEventData(event, eventTemplate)
            }
            updateGroupEventsUseCase(group.value)
            onSuccessCallback()
        }
    }

    private fun overwriteRecurrentEventData(
        event: Event,
        eventTemplate: Event
    ) {
        event.localStart =
            event.localStart.with(eventTemplate.localStart.dayOfWeek)
                .withHour(eventTemplate.localStart.hour)
                .withMinute(eventTemplate.localStart.minute)
        event.localEnd =
            event.localEnd.with(eventTemplate.localEnd.dayOfWeek)
                .withHour(eventTemplate.localEnd.hour)
                .withMinute(eventTemplate.localEnd.minute)
        event.color = eventTemplate.color
        event.name = eventTemplate.name
        event.requireConfirmation = eventTemplate.requireConfirmation
        event.description = eventTemplate.description
    }


}
