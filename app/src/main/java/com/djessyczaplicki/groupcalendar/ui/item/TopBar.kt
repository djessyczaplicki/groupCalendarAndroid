package com.djessyczaplicki.groupcalendar.ui.item

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ViewDay
import androidx.compose.material.icons.filled.ViewWeek
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.djessyczaplicki.groupcalendar.R

@Composable
fun TopBar(
    title: String = "",
    color: Color = MaterialTheme.colors.primaryVariant,
    buttonIcon: ImageVector = Icons.Filled.Menu,
    navController: NavController,
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
                    }
            )
        },
        navigationIcon = {
            IconButton(onClick = { onButtonClicked() } ) {
                Icon(
                    imageVector = buttonIcon,
                    contentDescription = "menu icon",
                    modifier = Modifier.size(30.dp),
                    tint = colorResource( id = R.color.lighter_grey)
                )
            }
        },
        actions = {
            val context = LocalContext.current
            IconButton(onClick = { onIconClicked() }) {
                Icon(if (isDailyViewEnabled) Icons.Filled.ViewWeek else Icons.Filled.ViewDay, contentDescription = "DailyViewToggle", tint = colorResource(id = R.color.lighter_grey))
            }
        },
        backgroundColor = color
    )
}
