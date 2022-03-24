package com.djessyczaplicki.groupcalendar.ui.item

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.djessyczaplicki.groupcalendar.R
import com.djessyczaplicki.groupcalendar.data.remote.model.User
import com.djessyczaplicki.groupcalendar.ui.screen.AppScreens
import com.djessyczaplicki.groupcalendar.util.fullName

@Composable
fun UserRow(
    user: User,
    isAdmin: Boolean,
    icon: ImageVector?,
    onClick: () -> Unit
) {
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(horizontal = 15.dp, vertical = 8.dp)
    ){
        Text(
            text = user.fullName(),
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = stringResource(id = if (isAdmin) R.string.admin else R.string.user)
        )

        if (icon != null) {
            IconButton(
                onClick = {
                    onClick()
                },
                modifier = Modifier
                    .size(20.dp)
            ) {
                Icon(imageVector = icon, contentDescription = stringResource(id = R.string.menu))
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun UserRowPreview() {
    UserRow(user = User(name = "djessy", surname = "czaplicki"), true, icon = Icons.Filled.MoreVert) {

    }
}