package com.djessyczaplicki.groupcalendar.ui.screen.editgroup

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.djessyczaplicki.groupcalendar.R
import com.djessyczaplicki.groupcalendar.data.remote.model.Group
import com.djessyczaplicki.groupcalendar.data.remote.model.User
import com.djessyczaplicki.groupcalendar.util.fullNameYou

@Composable
fun GroupUserRow(
    user: User,
    group: Group,
    editGroupViewModel: EditGroupViewModel
) {
    val context = LocalContext.current
    val isAdmin = group.admins.contains(user.id)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(horizontal = 15.dp, vertical = 8.dp)
            .clickable {
                Toast
                    .makeText(context, user.fullNameYou(user.id), Toast.LENGTH_SHORT)
                    .show()
            }
    ) {
        Text(
            text = user.fullNameYou(user.id),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth(0.6f)
        )


        Row {
            Text(
                text = stringResource(id = if (isAdmin) R.string.admin else R.string.user),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.width(20.dp))
            var isExpanded by remember { mutableStateOf(false) }
            IconButton(
                onClick = {
                    isExpanded = true
                },
                modifier = Modifier
                    .size(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = stringResource(id = R.string.menu),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            DropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
                if (isAdmin) {
                    DropdownMenuItem(
                        onClick = {
                            isExpanded = false
                            editGroupViewModel.dismissAsAdmin(user)
                        },
                        text = {
                            Text(stringResource(R.string.dismiss_as_admin))
                        })
                } else {
                    DropdownMenuItem(
                        onClick = {
                            isExpanded = false
                            editGroupViewModel.makeUserAdmin(user)
                        },
                        text = {
                            Text(stringResource(R.string.make_admin))
                        })
                }
                DropdownMenuItem(
                    onClick = {
                        isExpanded = false
                        editGroupViewModel.removeUserFromGroup(user)
                    },
                    text = {
                        Text(stringResource(R.string.remove_user))
                    })
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun GroupUserRowPreview() {
    GroupUserRow(user = User(name = "djessy", surname = "czaplicki"), group = Group(), viewModel())
}
