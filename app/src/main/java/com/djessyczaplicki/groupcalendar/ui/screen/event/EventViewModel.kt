package com.djessyczaplicki.groupcalendar.ui.screen.event

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djessyczaplicki.groupcalendar.GroupCalendarApp
import com.djessyczaplicki.groupcalendar.R
import com.djessyczaplicki.groupcalendar.core.AlarmReceiver
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
import java.time.ZoneOffset
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val getGroupByIdUseCase: GetGroupByIdUseCase,
    private val updateGroupEventsUseCase: UpdateGroupEventsUseCase,
    private val getUsersUseCase: GetUsersUseCase
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
        event.value = group.value.events.find { it.id == eventId } ?: Event()
        loadUsers()
    }

    private suspend fun loadUsers() {
        confirmedUsers.value = getUsersUseCase(event.value.confirmedUsers)
    }

    fun delete(onSuccessCallback: () -> Unit) {
        viewModelScope.launch {
            reloadEvent()
            val updatedGroup = group.value
            updatedGroup.events.removeAll { it.id == event.value.id }
            group.value.events = updateGroupEventsUseCase(updatedGroup).toMutableList()
            onSuccessCallback()
        }
    }

    fun deleteAll(onSuccessCallback: () -> Unit) {
        viewModelScope.launch {
            reloadEvent()
            val updatedGroup = group.value
            updatedGroup.events.removeAll { it.recurrenceId == event.value.recurrenceId!! }
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

    fun setAlarm(minutes: Int = 5) {
        val context = GroupCalendarApp.applicationContext()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("group_id", group.value.id)
        intent.putExtra("event_id", event.value.id)
        intent.putExtra("group_name", group.value.name)
        intent.putExtra("title", event.value.name)
        intent.putExtra("description", event.value.description)
        val num = (0..10000000).random()
        val pendingIntent = PendingIntent.getBroadcast(context, num, intent, 0)
        val calendar = Calendar.getInstance()

        calendar.time =
            Date.from(
                event.value.localStart.minusMinutes(minutes.toLong()).toInstant(ZoneOffset.ofHours(2))
            )
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
        Toast.makeText(
            context,
            calendar.time.toString(),
            Toast.LENGTH_SHORT
        ).show()

        Toast.makeText(
            context,
            context.getString(R.string.alarm_set_successfully),
            Toast.LENGTH_SHORT
        ).show()
    }

    fun share(context: Context) {
        var link = context.getString(R.string.invite_base_url)
        link += "/event/${groupId}/${eventId}"
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
}
