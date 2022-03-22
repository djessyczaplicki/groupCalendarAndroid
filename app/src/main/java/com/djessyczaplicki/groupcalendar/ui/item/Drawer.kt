package com.djessyczaplicki.groupcalendar.ui.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.djessyczaplicki.groupcalendar.R
import com.djessyczaplicki.groupcalendar.data.remote.model.Group
import com.djessyczaplicki.groupcalendar.ui.screen.AppScreens


@Composable
fun DrawerContent(
    groups: List<Group>,
    onDestinationClicked: (route: String) -> Unit
) {
    LazyColumn() {
        item {
            Spacer(Modifier.height(5.dp))
            val usersGroupIds = groups.joinToString(","){ it.id }
            Text(
                text = stringResource(id = R.string.show_all_events),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colors.primaryVariant,
                modifier = Modifier
                    .clickable {
                        onDestinationClicked(
                            AppScreens.TimetableScreen.route
                                    + "/${usersGroupIds}"
                        )
                    }
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 8.dp)
            )
            Divider(thickness = 0.5.dp)
            Spacer(Modifier.height(5.dp))
        }
        item {
            Text(
                text = stringResource(id = R.string.my_groups),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary,
                fontSize = 18.sp,
                modifier = Modifier.padding(12.dp)
            )
        }
        groups.forEach { group ->
            item {
                GroupRow(onDestinationClicked, group)
                Spacer(Modifier.height(5.dp))
            }
        }

        item {
            TextButton(
                onClick = { onDestinationClicked(AppScreens.EditGroupScreen.route) }
            ) {
                Text(
                    text = stringResource(id = R.string.create_group),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 8.dp)
                )
            }

        }
    }
}

