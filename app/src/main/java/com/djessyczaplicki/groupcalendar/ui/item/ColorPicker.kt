package com.djessyczaplicki.groupcalendar.ui.item

import androidx.compose.foundation.background
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.djessyczaplicki.groupcalendar.R

@Composable
fun ColorPicker(
    value: ULong,
    onValueChange: (ULong) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val colors = listOf(
        colorResource(id = R.color.deep_orange),
        colorResource(id = R.color.light_blue),
        colorResource(id = R.color.light_green),
        colorResource(id = R.color.red),
        colorResource(id = R.color.teal),
        colorResource(id = R.color.purple),
        colorResource(id = R.color.yellow),
        colorResource(id = R.color.blue)
    )

    OutlinedButton(
        modifier = modifier,
        onClick = { expanded = !expanded },
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(value))
    ) {
        Text(stringResource(id = R.string.color))
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = modifier
    ) {
        colors.forEach { color ->
            DropdownMenuItem(onClick = { onValueChange(color.value); expanded = false }, modifier = Modifier.background(color = color)) { }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ColorPickerPreview() {
    ColorPicker(value = Color.Blue.value, onValueChange = { })
}