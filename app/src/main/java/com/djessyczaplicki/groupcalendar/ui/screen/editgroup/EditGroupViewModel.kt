package com.djessyczaplicki.groupcalendar.ui.screen.editgroup

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djessyczaplicki.groupcalendar.R
import com.djessyczaplicki.groupcalendar.data.remote.model.Group
import com.djessyczaplicki.groupcalendar.data.remote.model.User
import com.djessyczaplicki.groupcalendar.domain.groupusecase.DeleteGroupUseCase
import com.djessyczaplicki.groupcalendar.domain.groupusecase.GetGroupByIdUseCase
import com.djessyczaplicki.groupcalendar.domain.groupusecase.StoreGroupImageUseCase
import com.djessyczaplicki.groupcalendar.domain.groupusecase.UpdateGroupUseCase
import com.djessyczaplicki.groupcalendar.domain.userusecase.GetUserByIdUseCase
import com.djessyczaplicki.groupcalendar.domain.userusecase.GetUsersUseCase
import com.djessyczaplicki.groupcalendar.domain.userusecase.UpdateUserUseCase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditGroupViewModel @Inject constructor(
    private val getGroupByIdUseCase: GetGroupByIdUseCase,
    private val updateGroupUseCase: UpdateGroupUseCase,
    private val getUsersUseCase: GetUsersUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val storeGroupImageUseCase: StoreGroupImageUseCase,
    private val deleteGroupUseCase: DeleteGroupUseCase
) : ViewModel() {
    var groupId: String? = null

    val users = mutableStateOf(listOf<User>())
    val group = mutableStateOf(Group())
    var isLoading by mutableStateOf(false)
    val isEditing = mutableStateOf(false)

    fun loadGroup(onGroupLoaded: (group: Group) -> Unit) {
        viewModelScope.launch {
            group.value = getGroupByIdUseCase(groupId!!) ?: return@launch
            onGroupLoaded(group.value)
        }
    }

    fun loadUsers() {
        viewModelScope.launch {
            users.value = getUsersUseCase(group.value.users)
        }
    }

    fun editGroup(editedGroup: Group, image: String, onSuccessCallback: () -> Unit) {
        isLoading = true
        if (image.isNotEmpty() && !image.contains("https://firebasestorage.googleapis.com")) {
            storeGroupImageUseCase(editedGroup.id, image) { imageUri ->
                editedGroup.image = imageUri.toString()
                updateGroup(editedGroup) {
                    onSuccessCallback()
                }
            }
        } else {
            updateGroup(editedGroup) {
                onSuccessCallback()
            }
        }
    }

    private fun updateGroup(group: Group, onSuccessCallback: () -> Unit) {
        viewModelScope.launch {
            val uid = Firebase.auth.currentUser!!.uid
            if (!group.users.contains(uid)) {
                group.users += uid
            }
            if (!group.admins.contains(uid)) {
                group.admins += uid
            }
            updateGroupUseCase(group)
            // add the group to creator/editor's groups
            val user = getUserByIdUseCase(uid) ?: throw Exception("User not found")
            if (!user.groups.contains(group.id)) {
                user.groups += group.id
                updateUserUseCase(user)
            }
            onSuccessCallback()
            isLoading = false
        }
    }

    fun makeUserAdmin(user: User) {
        viewModelScope.launch {
            if (group.value.admins.contains(user.id)) return@launch

            group.value.admins += user.id
            group.value = updateGroupUseCase(group.value)
        }
    }

    fun dismissAsAdmin(user: User) {
        viewModelScope.launch {
            if (!group.value.admins.contains(user.id)) return@launch

            val admins = group.value.admins.filterNot { it == user.id }
            group.value.admins = admins
            group.value = updateGroupUseCase(group.value)
        }
    }

    fun removeUserFromGroup(user: User) {
        viewModelScope.launch {
            if (!group.value.users.contains(user.id)) return@launch

            val groupUsers = group.value.users.filterNot { it == user.id }
            val admins = group.value.users.filterNot { it == user.id }
            group.value.users = groupUsers
            group.value.admins = admins
            group.value = updateGroupUseCase(group.value)

            val groups = user.groups.filterNot { it == groupId }
            user.groups = groups
            updateUserUseCase(user)
            loadUsers()
        }
    }

    fun sendInviteLink(context: Context) {
        var link = context.getString(R.string.invite_base_url)
        link += "/invite/${groupId}"
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, link)

        context.startActivity(
            Intent.createChooser(
                intent,
                context.getString(R.string.share_group_invite)
            )
        )
    }

    fun deleteGroup(onSuccessCallback: () -> Unit) {
        viewModelScope.launch {
            deleteGroupUseCase(groupId!!)
            onSuccessCallback()
        }
    }


}
