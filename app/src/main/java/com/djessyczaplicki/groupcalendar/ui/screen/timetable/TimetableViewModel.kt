package com.djessyczaplicki.groupcalendar.ui.screen.timetable

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djessyczaplicki.groupcalendar.data.local.exception.UserNotFoundException
import com.djessyczaplicki.groupcalendar.data.remote.model.Event
import com.djessyczaplicki.groupcalendar.data.remote.model.Group
import com.djessyczaplicki.groupcalendar.domain.eventusecase.UpdateGroupEventsUseCase
import com.djessyczaplicki.groupcalendar.domain.groupusecase.GetGroupByIdUseCase
import com.djessyczaplicki.groupcalendar.domain.userusecase.GetUserByIdUseCase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimetableViewModel @Inject constructor(
    private val updateGroupEventsUseCase: UpdateGroupEventsUseCase,
    private val getGroupByIdUseCase: GetGroupByIdUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase
) : ViewModel() {
    val events = mutableStateOf(listOf<Event>())
    val groups = mutableStateOf(listOf<Group>())
    val shownGroups = mutableStateOf(listOf<Group>())
    val adminGroups = mutableStateOf(listOf<Group>())
    val group = mutableStateOf(Group())
    val groupId = mutableStateOf("")


    fun loadGroups() {
        viewModelScope.launch {
            groups.value = listOf()
            val uid = Firebase.auth.currentUser!!.uid
            val user = getUserByIdUseCase(uid) ?: throw UserNotFoundException("User not found")
            user.groups.forEach { groupId ->
                val group: Group? = getGroupByIdUseCase(groupId)
                if (group != null) {
                    groups.value += group
                }
            }
            if (groups.value.isNotEmpty()) {
                groups.value = groups.value.toMutableList().sortedBy { it.name }
            }
            subscribeToGroups()
        }
    }

    private fun subscribeToGroups() {
        val firebaseMessaging = FirebaseMessaging.getInstance()
        groups.value.forEach { group ->
            firebaseMessaging.subscribeToTopic(group.id)
        }
    }

    fun loadGroup(groupId: String) {
        this.groupId.value = groupId
        viewModelScope.launch {
            val result = getGroupByIdUseCase(groupId)!!
            group.value = result
        }
    }

    fun removeEvent(event: Event, group: Group) {
        viewModelScope.launch {
            group.events.remove(event)
            updateGroupEventsUseCase(group)
        }
    }

    fun loadShownGroups(groupIds: List<String>) {
        val groups = mutableListOf<Group>()
        viewModelScope.launch {
            groupIds.forEach { groupId ->
                val group = getGroupByIdUseCase(groupId)
                if (group != null) {
                    groups += group
                }
            }
            shownGroups.value = groups
            updateAdminGroups()
            loadEvents()
        }
    }

    private fun loadEvents() {
        events.value = listOf()
        events.value = shownGroups.value.fold(listOf()) { acc, group ->
            acc + group.events
        }
    }

    fun findParentGroup(event: Event): Group? {
        return shownGroups.value.find { group ->
            group.events.contains(event)
        }
    }

    private fun updateAdminGroups() {
        val uid = Firebase.auth.uid
        adminGroups.value = shownGroups.value.filter { group -> group.admins.contains(uid) }
    }
}
