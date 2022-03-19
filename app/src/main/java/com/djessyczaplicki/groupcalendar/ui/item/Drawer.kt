package com.djessyczaplicki.groupcalendar.ui.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Text
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.djessyczaplicki.groupcalendar.data.remote.model.Group
import com.djessyczaplicki.groupcalendar.ui.screen.AppScreens
import com.djessyczaplicki.groupcalendar.R


@Composable
fun DrawerContent(
    groups: List<Group>,
    onDestinationClicked: (route: String) -> Unit
) {
    LazyColumn() {
        item {
            Text(
                text = stringResource(id = R.string.my_groups),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(12.dp)
            )
        }
        groups.forEach { group ->
            item {
                Text(
                    text = group.name,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clickable { onDestinationClicked(AppScreens.TimetableScreen.route + "/${group.id}") }
                        .padding(horizontal = 15.dp, vertical = 8.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}
