package com.djessyczaplicki.groupcalendar.ui.item

import android.content.Context
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.djessyczaplicki.groupcalendar.core.RetrofitHelper
import com.djessyczaplicki.groupcalendar.ui.screen.AppScreens
import com.djessyczaplicki.groupcalendar.util.UserPreferences
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import com.djessyczaplicki.groupcalendar.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope

@Composable
fun TopBar(
    title: String = "",
    color: Color = MaterialTheme.colors.primaryVariant,
    buttonIcon: ImageVector = Icons.Filled.Menu,
    navController: NavController,
    onButtonClicked: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title
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
            IconButton(onClick = { disconnect(context, navController) }) {
                Icon(Icons.Filled.Logout, contentDescription = "Logout", tint = colorResource(id = R.color.lighter_grey))
            }
        },
        backgroundColor = color
    )
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