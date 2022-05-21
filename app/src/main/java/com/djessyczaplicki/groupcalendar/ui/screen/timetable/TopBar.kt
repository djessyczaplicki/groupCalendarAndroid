package com.djessyczaplicki.groupcalendar.ui.screen.timetable

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ViewDay
import androidx.compose.material.icons.filled.ViewWeek
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun TopBar(
    title: String = "",
    color: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    buttonIcon: ImageVector = Icons.Filled.Menu,
    isDailyViewEnabled: Boolean,
    onIconClicked: () -> Unit,
    onButtonClicked: () -> Unit
) {
    val context = LocalContext.current
    TopAppBar(
        title = {
            Text(
                text = title,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                modifier = Modifier
                    .clickable {
                        Toast.makeText(context, title, Toast.LENGTH_SHORT).show()
                    },
                color = contentColor
            )

        },
        navigationIcon = {
            IconButton(onClick = { onButtonClicked() }) {
                Icon(
                    imageVector = buttonIcon,
                    contentDescription = "menu icon",
                    modifier = Modifier.size(30.dp),
                    tint = contentColor
                )
            }
        },
        actions = {
            IconButton(onClick = { onIconClicked() }) {
                Icon(
                    if (isDailyViewEnabled) Icons.Filled.ViewWeek else Icons.Filled.ViewDay,
                    contentDescription = "DailyViewToggle",
                    tint = contentColor
                )
            }
        },
        backgroundColor = color
    )
}
