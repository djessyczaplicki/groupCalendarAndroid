package com.djessyczaplicki.groupcalendar.ui.screen.addevent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.djessyczaplicki.groupcalendar.R
import com.djessyczaplicki.groupcalendar.data.remote.model.Event
import com.djessyczaplicki.groupcalendar.ui.item.ColorPicker
import java.time.LocalDateTime

@Composable
fun AddEventScreen(
    navController: NavController,
    addEventViewModel: AddEventViewModel
) {
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var color by remember { mutableStateOf(0UL) }

    Column() {

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(id = R.string.title)) },
            placeholder = { Text(stringResource(id = R.string.event_title))},
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        )
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(stringResource(id = R.string.description)) },
            placeholder = { Text(stringResource(id = R.string.event_description))},
            singleLine = false,
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth()
                .padding(4.dp)
        )

        ColorPicker(
            value = color,
            onValueChange = { color = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        )

        Button(
            modifier = Modifier
                .padding(4.dp)
                .height(60.dp)
                .align(Alignment.CenterHorizontally),
            onClick = { addEventViewModel.createEvent(
                Event(
                    name = title,
                    description = description,
                    color = Color(color),
                    start = LocalDateTime.now(),
                    end = LocalDateTime.now().plusHours(2)
                )
            )}
        ) {
            Text(stringResource(id = R.string.create_event))
        }


    }
}


@Preview(showBackground = true)
@Composable
fun AddEventScreenPreview() {
    AddEventScreen(
        rememberNavController(),
        AddEventViewModel()
    )
}