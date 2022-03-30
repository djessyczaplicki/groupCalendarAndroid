package com.djessyczaplicki.groupcalendar.ui.item

import android.content.Context
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.djessyczaplicki.groupcalendar.R
import com.djessyczaplicki.groupcalendar.core.RetrofitHelper
import com.djessyczaplicki.groupcalendar.data.remote.model.Group
import com.djessyczaplicki.groupcalendar.ui.screen.AppScreens
import com.djessyczaplicki.groupcalendar.util.UserPreferences
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


@Composable
fun DrawerContent(
    groups: List<Group>,
    navController: NavController,
    onDestinationClicked: (route: String) -> Unit
) {
    LazyColumn() {
        item {
            Spacer(Modifier.height(5.dp))
            val usersGroupIds = groups.joinToString(","){ it.id }
            Text(
                text = stringResource(id = R.string.show_all_events),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colors.primary,
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
        if (groups.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(id = R.string.my_groups),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primaryVariant,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
        groups.forEach { group ->
            item {
                GroupRow(onDestinationClicked, group, Icons.Filled.Edit)
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

        item {
            val context = LocalContext.current
            TextButton(
                onClick = { disconnect(context, navController) }
            ) {
                Text(
                    text = stringResource(id = R.string.disconnect),
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

fun disconnect(context: Context, navController: NavController) {
    runBlocking{
        launch {
            UserPreferences(context).saveAuthToken("")
            RetrofitHelper.setToken("")
            Firebase.auth.signOut()
            navController.navigate(AppScreens.LoginScreen.route) {
                popUpTo(0)
            }
        }
    }
}