package com.djessyczaplicki.groupcalendar.ui.screen.editgroupscreen

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djessyczaplicki.groupcalendar.data.remote.model.Group
import com.djessyczaplicki.groupcalendar.domain.groupusecase.GetGroupByIdUseCase
import com.djessyczaplicki.groupcalendar.domain.groupusecase.StoreGroupImageUseCase
import com.djessyczaplicki.groupcalendar.domain.groupusecase.UpdateGroupUseCase
import com.djessyczaplicki.groupcalendar.domain.userusecase.GetUserByIdUseCase
import com.djessyczaplicki.groupcalendar.domain.userusecase.UpdateUserUseCase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class EditGroupViewModel : ViewModel() {
    var groupId: String? = null

    //    val createGroupUseCase = CreateGroupUseCase()
    val getGroupByIdUseCase = GetGroupByIdUseCase()
    val updateGroupUseCase = UpdateGroupUseCase()
    val getUserByIdUseCase = GetUserByIdUseCase()
    val updateUserUseCase = UpdateUserUseCase()
    val storeGroupImageUseCase = StoreGroupImageUseCase()

    val group = mutableStateOf(Group())
    var isLoading by mutableStateOf(false)
    val isEditing = mutableStateOf(false)

    fun loadGroup(groupId: String) {
        this.groupId = groupId
        viewModelScope.launch {
            group.value = getGroupByIdUseCase(groupId) ?: return@launch
        }
    }

    fun editGroup(group: Group, imageBitmap: Bitmap?, onSuccessCallback: () -> Unit) {
        isLoading = true
        if (imageBitmap != null) {
            storeGroupImageUseCase(group.id, imageBitmap) { imageUri ->
                group.image = imageUri.toString()
                updateGroup(group) {
                    onSuccessCallback()
                }
            }
        } else {
            updateGroup(group) {
                onSuccessCallback()
            }
        }
    }

    private fun updateGroup(group: Group, onSuccessCallback: () -> Unit) {
        viewModelScope.launch {
            updateGroupUseCase(group)
            val uid = Firebase.auth.currentUser!!.uid
            val user = getUserByIdUseCase(uid)
            user.groups += group.id
            updateUserUseCase(user)
            onSuccessCallback()
            isLoading = false
        }
    }

}