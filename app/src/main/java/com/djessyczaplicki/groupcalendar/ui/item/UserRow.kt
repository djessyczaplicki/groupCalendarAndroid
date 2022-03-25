package com.djessyczaplicki.groupcalendar.ui.item

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.djessyczaplicki.groupcalendar.R
import com.djessyczaplicki.groupcalendar.data.remote.model.Group
import com.djessyczaplicki.groupcalendar.data.remote.model.User
import com.djessyczaplicki.groupcalendar.ui.screen.editgroupscreen.EditGroupViewModel
import com.djessyczaplicki.groupcalendar.util.fullName

@Composable
fun GroupUserRow(
    user: User,
    group: Group,
    editGroupViewModel: EditGroupViewModel
) {
    val isAdmin = group.admins.contains(user.id)
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

        Box {
            var isExpanded by remember { mutableStateOf(false) }
            IconButton(
                onClick = {
                    isExpanded = true
                },
                modifier = Modifier
                    .size(20.dp)
            ) {
                Icon(imageVector =  Icons.Filled.MoreVert, contentDescription = stringResource(id = R.string.menu))
            }
            DropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
                if (isAdmin) {
                    DropdownMenuItem(onClick = {
                        isExpanded = false
                        editGroupViewModel.dismissAsAdmin(user)
                    }) {
                        Text(stringResource(R.string.dismiss_as_admin))
                    }
                } else {
                    DropdownMenuItem(onClick = {
                        isExpanded = false
                        editGroupViewModel.makeUserAdmin(user)
                    }) {
                        Text(stringResource(R.string.make_admin))
                    }
                }
                DropdownMenuItem(onClick = {
                    isExpanded = false
                    editGroupViewModel.removeUserFromGroup(user)
                }) {
                    Text(stringResource(R.string.remove_user))
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun UserRowPreview() {
    GroupUserRow(user = User(name = "djessy", surname = "czaplicki"), group = Group(), EditGroupViewModel())
}