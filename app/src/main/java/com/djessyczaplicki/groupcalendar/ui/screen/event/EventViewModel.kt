package com.djessyczaplicki.groupcalendar.ui.screen.event

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djessyczaplicki.groupcalendar.data.remote.model.Event
import com.djessyczaplicki.groupcalendar.data.remote.model.Group
import com.djessyczaplicki.groupcalendar.domain.groupusecase.GetGroupByIdUseCase
import kotlinx.coroutines.launch

class EventViewModel : ViewModel() {
    lateinit var groupId: String

    val getGroupByIdUseCase = GetGroupByIdUseCase()

    var event = mutableStateOf(Event())
    var group = mutableStateOf(Group())

    fun loadEvent(groupId: String, eventId: String) {
        this.groupId = groupId
        viewModelScope.launch {
            group.value = getGroupByIdUseCase(groupId)
            event.value = group.value.events.find{ it.id == eventId } ?: Event()
        }
    }
}