package com.djessyczaplicki.groupcalendar.ui.item

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
    leftButtonIcon: ImageVector = Icons.Filled.ArrowBack,
    rightButtonIcon: ImageVector? = null,
    onLeftButtonClicked: () -> Unit,
    onRightButtonClicked: (() -> Unit)? = null
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
            IconButton(onClick = { onLeftButtonClicked() }) {
                Icon(
                    imageVector = leftButtonIcon,
                    contentDescription = "Back button",
                    modifier = Modifier.size(30.dp),
                    tint = contentColor
                )
            }
        },
        actions = {
            if (rightButtonIcon != null) {
                IconButton(onClick = {
                    if (onRightButtonClicked != null) {
                        onRightButtonClicked()
                    }
                }) {
                    Icon(
                        imageVector = rightButtonIcon,
                        contentDescription = "Right Action",
                        tint = contentColor
                    )
                }
            }
        },
        backgroundColor = color
    )
}
