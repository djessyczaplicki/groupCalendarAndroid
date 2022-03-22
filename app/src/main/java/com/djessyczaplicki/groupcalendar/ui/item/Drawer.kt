package com.djessyczaplicki.groupcalendar.ui.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
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
            Text(
                text = stringResource(id = R.string.my_groups),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(12.dp)
            )
        }
        groups.forEach { group ->
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onDestinationClicked(AppScreens.TimetableScreen.route + "/${group.id}") }
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 8.dp)
                ) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(group.image)
                            .crossfade(true)
                            .transformations(CircleCropTransformation())
                            .build(),
                        loading = {
                            CircularProgressIndicator()
                        },
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(
                        text = group.name,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                    )
                }
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
