package com.djessyczaplicki.groupcalendar.ui.screen.invite

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.djessyczaplicki.groupcalendar.R
import com.djessyczaplicki.groupcalendar.data.remote.model.Group
import com.djessyczaplicki.groupcalendar.ui.screen.AppScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteScreen(
    navController: NavController,
    inviteViewModel: InviteViewModel
) {
    val context = LocalContext.current
    val group = inviteViewModel.group.value
    Column {
        Text(
            stringResource(R.string.invite_screen) + " ${group.name}",
            modifier = Modifier
                .height(30.dp)
                .padding(4.dp),
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            ElevatedCard(
                modifier = Modifier
                    .padding(horizontal = 50.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(group.image.ifEmpty { stringResource(id = R.string.image_placeholder_url) })
                            .crossfade(true)
                            .transformations(CircleCropTransformation())
                            .build(),
                        loading = {
                            CircularProgressIndicator()
                        },
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = getGroupInviteText(group),
                        modifier = Modifier.padding(10.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            inviteViewModel.joinGroup(
                                onSuccessCallback = {
                                    goToGroupsTimetable(navController, group.id)
                                },
                                userAlreadyInGroup = {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.user_already_in_group),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    goToGroupsTimetable(navController, group.id)
                                }
                            )
                        },
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Text(text = stringResource(id = R.string.join_group) + " ${group.name}")
                    }
                }
            }
        }

    }

}

fun goToGroupsTimetable(navController: NavController, groupId: String) {
    navController.navigate(AppScreens.TimetableScreen.route + "/${groupId}") {
        popUpTo(0)
    }
}

@Composable
fun getGroupInviteText(group: Group): AnnotatedString {
    val text = stringResource(R.string.invite_text).split("{0}")
    val annotatedString = buildAnnotatedString {
        append(text[0])
        withStyle(
            style = SpanStyle(fontWeight = FontWeight.Bold)
        ) {
            append("${group.name}: ${group.description}")
        }
        append(text[1])
    }
    return annotatedString
}
