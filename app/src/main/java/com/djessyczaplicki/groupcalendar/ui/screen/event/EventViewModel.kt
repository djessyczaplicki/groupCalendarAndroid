package com.djessyczaplicki.groupcalendar.ui.screen.event

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djessyczaplicki.groupcalendar.data.remote.model.Event
import com.djessyczaplicki.groupcalendar.data.remote.model.Group
import com.djessyczaplicki.groupcalendar.data.remote.model.User
import com.djessyczaplicki.groupcalendar.domain.eventusecase.UpdateGroupEventsUseCase
import com.djessyczaplicki.groupcalendar.domain.groupusecase.GetGroupByIdUseCase
import com.djessyczaplicki.groupcalendar.domain.userusecase.GetUsersUseCase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val getGroupByIdUseCase : GetGroupByIdUseCase,
    private val updateGroupEventsUseCase : UpdateGroupEventsUseCase,
    private val getUsersUseCase : GetUsersUseCase
) : ViewModel() {
    private lateinit var groupId: String
    private lateinit var eventId: String



    var group = mutableStateOf(Group())
    var event = mutableStateOf(Event())
    var confirmedUsers = mutableStateOf(listOf<User>())

    fun loadEvent(groupId: String, eventId: String) {
        this.groupId = groupId
        this.eventId = eventId
        viewModelScope.launch {
            reloadEvent()
        }
    }

    private suspend fun reloadEvent() {
        group.value = getGroupByIdUseCase(groupId)!!
        event.value = group.value.events.find{ it.id == eventId } ?: Event()
        loadUsers()
    }

    private suspend fun loadUsers() {
        confirmedUsers.value = getUsersUseCase(event.value.confirmedUsers)
    }

    fun delete(onSuccessCallback: () -> Unit) {
        viewModelScope.launch {
            reloadEvent()
            val updatedGroup = group.value
            updatedGroup.events.removeAll{ it.id == event.value.id }
            group.value.events = updateGroupEventsUseCase(updatedGroup).toMutableList()
            onSuccessCallback()
        }
    }

    fun deleteAll(onSuccessCallback: () -> Unit) {
        viewModelScope.launch {
            reloadEvent()
            val updatedGroup = group.value
            updatedGroup.events.removeAll{ it.recurrenceId == event.value.recurrenceId!! }
            group.value.events = updateGroupEventsUseCase(updatedGroup).toMutableList()
            onSuccessCallback()
        }
    }

    fun confirmAttendance() {
        viewModelScope.launch {
            reloadEvent()
            val userId = Firebase.auth.currentUser!!.uid
            if (!event.value.confirmedUsers.contains(userId)) {
                event.value.confirmedUsers += userId
                updateGroupEventsUseCase(group.value)
                loadUsers()
            }
        }
    }

    fun denyAttendance() {
        viewModelScope.launch {
            reloadEvent()
            val userId = Firebase.auth.currentUser!!.uid
            if (event.value.confirmedUsers.contains(userId)) {
                event.value.confirmedUsers -= userId
                updateGroupEventsUseCase(group.value)
                loadUsers()
            }
        }
    }
}