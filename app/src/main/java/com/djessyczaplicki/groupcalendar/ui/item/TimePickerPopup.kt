package com.djessyczaplicki.groupcalendar.ui.item

import android.app.TimePickerDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.djessyczaplicki.groupcalendar.util.formatMinute
import java.util.*


@Composable
fun TimePickerPopup(
    text: String,
    isSet: Boolean,
    hour: Long,
    minute: Long,
    modifier: Modifier = Modifier,
    onSelection: (hour: Long, minute: Long) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val mHour = if (hour > 0) hour.toInt() else calendar[Calendar.HOUR_OF_DAY]
    val mMinute = if (minute > 0) minute.toInt() else calendar[Calendar.MINUTE]

    val time = rememberSaveable { mutableStateOf("") }
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour: Int, minute: Int ->
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
