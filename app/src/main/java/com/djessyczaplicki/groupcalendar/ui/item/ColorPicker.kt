package com.djessyczaplicki.groupcalendar.ui.item

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.djessyczaplicki.groupcalendar.R
import com.godaddy.android.colorpicker.harmony.ColorHarmonyMode
import com.godaddy.android.colorpicker.harmony.HarmonyColorPicker

@Composable
fun ColorPicker(
    value: ULong,
    onValueChange: (ULong) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }


    var textColor by remember { mutableStateOf(Color.White) }

    fun updateTextColor() {
        textColor = if (Color(value).luminance() > 0.5) Color.Black else Color.White
    }
    Row {
        OutlinedButton(
            modifier = modifier,
            onClick = { expanded = !expanded },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(value),
                contentColor = textColor
            )
        ) {
            Text(stringResource(id = R.string.color), color = textColor)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            HarmonyColorPicker(
                harmonyMode = ColorHarmonyMode.NONE,
                modifier = Modifier.size(300.dp),
                onColorChanged = { hsvColor ->
                    onValueChange(hsvColor.toColor().value)
                    updateTextColor()
                },
                color = Color(value)
            )
        }
    }

}


@Preview(showBackground = true)
@Composable
fun ColorPickerPreview() {
    ColorPicker(value = Color.Blue.value, onValueChange = { })
}
