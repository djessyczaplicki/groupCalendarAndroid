package com.djessyczaplicki.groupcalendar.ui.screen.invite

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djessyczaplicki.groupcalendar.data.local.exception.UserNotFoundException
import com.djessyczaplicki.groupcalendar.data.remote.model.Group
import com.djessyczaplicki.groupcalendar.data.remote.model.User
import com.djessyczaplicki.groupcalendar.domain.groupusecase.GetGroupByIdUseCase
import com.djessyczaplicki.groupcalendar.domain.groupusecase.UpdateGroupUseCase
import com.djessyczaplicki.groupcalendar.domain.userusecase.GetUserByIdUseCase
import com.djessyczaplicki.groupcalendar.domain.userusecase.UpdateUserUseCase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InviteViewModel @Inject constructor(
    private val getGroupByIdUseCase : GetGroupByIdUseCase,
    private val getUserByIdUseCase : GetUserByIdUseCase,
    private val updateGroupUseCase : UpdateGroupUseCase,
    private val updateUserUseCase : UpdateUserUseCase
) : ViewModel() {
    val user = mutableStateOf(User())
    val group = mutableStateOf(Group())
    val groupId = mutableStateOf("")

    fun load() {
        viewModelScope.launch {
            val userId = Firebase.auth.currentUser!!.uid
            group.value = getGroupByIdUseCase(groupId.value) ?: return@launch
            user.value = getUserByIdUseCase(userId) ?: throw UserNotFoundException("User not found")
        }
    }

    fun joinGroup(onSuccessCallback: () -> Unit, userAlreadyInGroup: () -> Unit) {
        viewModelScope.launch {
            val userId = Firebase.auth.currentUser!!.uid
            if (group.value.users.contains(userId)) {
                userAlreadyInGroup()
                return@launch
            }
            group.value.users += userId
            group.value = updateGroupUseCase(group.value)

            if (user.value.groups.contains(groupId.value)) {
                onSuccessCallback()
                return@launch
            }
            user.value.groups += groupId.value
            updateUserUseCase(user.value)
            onSuccessCallback()
        }
    }
}