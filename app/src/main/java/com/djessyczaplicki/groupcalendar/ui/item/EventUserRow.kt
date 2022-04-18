package com.djessyczaplicki.groupcalendar.ui.item

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.djessyczaplicki.groupcalendar.data.remote.model.User
import com.djessyczaplicki.groupcalendar.util.fullNameYou

@Composable
fun EventUserRow(
    user: User
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(horizontal = 15.dp, vertical = 8.dp)
    ) {
        Text(
            text = user.fullNameYou(user.id),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
@Preview(showBackground = true)
fun EventUserRowPreview() {
    EventUserRow(user = User(name = "djessy", surname = "czaplicki"))
}
