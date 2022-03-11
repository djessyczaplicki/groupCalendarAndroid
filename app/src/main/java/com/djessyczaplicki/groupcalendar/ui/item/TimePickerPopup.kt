package com.djessyczaplicki.groupcalendar.ui.item

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.djessyczaplicki.groupcalendar.R
import java.lang.Integer.max
import java.util.*


@Composable
fun TimePickerPopup(
    text: String,
    isSet: Boolean,
    hour: Long,
    minute: Long,
    modifier: Modifier = Modifier,
    onSelection: (hour: Long, minute: Long) -> Unit
){
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val mHour = if (hour > 0) hour.toInt() else calendar[Calendar.HOUR_OF_DAY]
    val mMinute = if (minute > 0) minute.toInt() else calendar[Calendar.MINUTE]

    val time = rememberSaveable { mutableStateOf("") }
    val timePickerDialog = TimePickerDialog(
        context,
        {_, hour : Int, minute: Int ->
            onSelection(hour.toLong(), minute.toLong())
            time.value = "$hour:${minute.formatMinute()}"
        }, mHour, mMinute, true
    )

    if (isSet) time.value = "$hour:${minute.toInt().formatMinute()}"

    OutlinedButton(
        modifier = modifier,
        onClick = {
        timePickerDialog.show()
    }) {
        Text(text = if (time.value != "") time.value else text)
    }

}

private fun Int.formatMinute(): String {
    if (this > 9) return this.toString()
    return "0$this"
}
