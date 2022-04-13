package com.djessyczaplicki.groupcalendar.ui.screen.sendnotification

import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djessyczaplicki.groupcalendar.GroupCalendarApp
import com.djessyczaplicki.groupcalendar.R
import com.djessyczaplicki.groupcalendar.data.network.NotificationApi
import com.djessyczaplicki.groupcalendar.data.remote.model.Group
import com.djessyczaplicki.groupcalendar.data.remote.model.NotificationData
import com.djessyczaplicki.groupcalendar.domain.groupusecase.GetGroupByIdUseCase
import com.djessyczaplicki.groupcalendar.domain.notificationusecase.SendNotificationToGroupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SendNotificationViewModel @Inject constructor(
    private val notificationApi: NotificationApi,
    private val sendNotificationToGroupUseCase: SendNotificationToGroupUseCase,
    private val getGroupByIdUseCase: GetGroupByIdUseCase
) : ViewModel() {
    var groupId = ""
    val group = mutableStateOf<Group?>(null)

    fun loadGroup() {
        viewModelScope.launch {
            group.value = getGroupByIdUseCase(groupId)
        }
    }

    fun sendNotification(notificationData: NotificationData) {
        viewModelScope.launch {
            val context = GroupCalendarApp.applicationContext()
            if (group.value != null) {
                val response = sendNotificationToGroupUseCase(notificationData, group.value!!)
                if (!response?.message_id.isNullOrBlank()) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.notification_sent),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_sending_notification),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.group_not_found_exception),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
