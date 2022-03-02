package com.djessyczaplicki.groupcalendar.ui.screen.timetable

import android.util.Log.e
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djessyczaplicki.groupcalendar.data.remote.model.Group
import com.djessyczaplicki.groupcalendar.domain.groupusecase.GetGroupsUseCase
import com.orhanobut.logger.Logger
import kotlinx.coroutines.launch

class TimetableViewModel: ViewModel() {
    val getGroupsUseCase = GetGroupsUseCase()
    var groups by mutableStateOf(listOf<Group>())

    fun loadGroups() {
        viewModelScope.launch {
            val result = getGroupsUseCase()
            if (result.isNotEmpty()) {
               groups = result.toMutableList().sortedBy { it.name }

            }
        }
    }
}