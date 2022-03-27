package com.djessyczaplicki.groupcalendar.ui.screen.invite

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.djessyczaplicki.groupcalendar.data.remote.model.Group
import com.djessyczaplicki.groupcalendar.data.remote.model.User
import com.djessyczaplicki.groupcalendar.domain.groupusecase.GetGroupByIdUseCase
import com.djessyczaplicki.groupcalendar.domain.groupusecase.UpdateGroupUseCase
import com.djessyczaplicki.groupcalendar.domain.userusecase.GetUserByIdUseCase
import com.djessyczaplicki.groupcalendar.domain.userusecase.UpdateUserUseCase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class InviteViewModel : ViewModel() {
    val user = mutableStateOf(User())
    val group = mutableStateOf(Group())
    val groupId = mutableStateOf("")

    val getGroupByIdUseCase = GetGroupByIdUseCase()
    val getUserByIdUseCase = GetUserByIdUseCase()
    val updateGroupUseCase = UpdateGroupUseCase()
    val updateUserUseCase = UpdateUserUseCase()


    fun load() {
        viewModelScope.launch {
            val userId = Firebase.auth.currentUser!!.uid
            group.value = getGroupByIdUseCase(groupId.value) ?: return@launch
            user.value = getUserByIdUseCase(userId)
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