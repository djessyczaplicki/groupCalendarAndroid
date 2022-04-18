package com.djessyczaplicki.groupcalendar.ui.item

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.djessyczaplicki.groupcalendar.R
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

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
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = modifier,
        onClick = {
            datePickerDialog.show()
        }
    ) {
        Row {
            Text(text = if (date.value != "") date.value else stringResource(id = R.string.pick_a_date))
            Spacer(Modifier.width(5.dp))
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp, 20.dp),
                tint = MaterialTheme.colorScheme.onPrimary
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
