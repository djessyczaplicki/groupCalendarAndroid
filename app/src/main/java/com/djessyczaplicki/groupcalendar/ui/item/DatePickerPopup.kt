package com.djessyczaplicki.groupcalendar.ui.item

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import com.djessyczaplicki.groupcalendar.R

@Composable
fun DatePickerPopup(
    modifier: Modifier = Modifier,
    onSelection: (LocalDate) -> Unit
) {
    val context = LocalContext.current
    val mYear: Int
    val mMonth: Int
    val mDay: Int
    val now = Calendar.getInstance()
    mYear = now.get(Calendar.YEAR)
    mMonth = now.get(Calendar.MONTH)
    mDay = now.get(Calendar.DAY_OF_MONTH)
    now.time = Date()
    val date = rememberSaveable { mutableStateOf("") }
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val cal = Calendar.getInstance()
            cal.set(year, month, dayOfMonth)
            val localDate = getLocalDate(cal.time)
            onSelection(localDate)
            date.value = getFormattedDate(localDate, "dd MMMM yyy")
        }, mYear, mMonth, mDay
    )
    OutlinedButton(
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
        modifier = modifier,
        onClick = {
            datePickerDialog.show()
        }
    ) {
        Row{
            Text(text = if (date.value != "") date.value else stringResource(id = R.string.pick_a_date))
            Spacer(Modifier.width(5.dp))
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp, 20.dp),
                tint = MaterialTheme.colors.onSurface
            )
        }
    }
}

fun getLocalDate(
    date: Date
): LocalDate {
    return LocalDate.from(date.toInstant().atZone(ZoneId.systemDefault()))
}

fun getFormattedDate(
    localDate: LocalDate,
    pattern: String
): String {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return localDate.format(formatter)
}