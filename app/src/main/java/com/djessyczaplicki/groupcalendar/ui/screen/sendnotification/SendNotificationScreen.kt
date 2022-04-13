package com.djessyczaplicki.groupcalendar.ui.screen.sendnotification

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.djessyczaplicki.groupcalendar.R
import com.djessyczaplicki.groupcalendar.data.remote.model.NotificationData

@Composable
fun SendNotificationScreen(
    navController: NavController,
    sendNotificationViewModel: SendNotificationViewModel
) {
    var title by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    val group = sendNotificationViewModel.group.value

    Column {
        Text(text = group?.name ?: "")
        Text(text = stringResource(id = R.string.send_a_message_to_group))
        TextField(
            value = title,
            onValueChange = { title = it },
            label = {
                Text(stringResource(id = R.string.title))
            }
        )
        TextField(
            value = text,
            onValueChange = { text = it },
            label = {
                Text(stringResource(id = R.string.text))
            }
        )

        Button(onClick = {
            sendNotificationViewModel.sendNotification(NotificationData(title, text))
        }) {
            Text(text = "Enviar")
        }
    }

}
