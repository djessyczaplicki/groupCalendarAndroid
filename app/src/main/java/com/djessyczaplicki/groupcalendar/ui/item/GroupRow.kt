package com.djessyczaplicki.groupcalendar.ui.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.djessyczaplicki.groupcalendar.R
import com.djessyczaplicki.groupcalendar.data.remote.model.Group
import com.djessyczaplicki.groupcalendar.ui.screen.AppScreens


@Composable
fun GroupRow(
    onDestinationClicked: (route: String) -> Unit,
    group: Group,
    icon: ImageVector? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .clickable { onDestinationClicked(AppScreens.TimetableScreen.route + "/${group.id}") }
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 8.dp).fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 8.dp)

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                    .size(50.dp)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = group.name,
                fontWeight = FontWeight.SemiBold,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
            )
        }
        if (icon != null) {
            IconButton(
                onClick = {
                    onDestinationClicked(AppScreens.EditGroupScreen.route + "/${group.id}")
                },
                modifier = Modifier
                    .size(20.dp)
            ) {
                Icon(imageVector = icon, contentDescription = stringResource(id = R.string.edit))
            }
        }
    }
}