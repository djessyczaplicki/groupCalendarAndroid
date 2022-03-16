package com.djessyczaplicki.groupcalendar.ui.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.djessyczaplicki.groupcalendar.ui.item.CollapsingTopBar.BACK_ID
import com.djessyczaplicki.groupcalendar.ui.item.CollapsingTopBar.EDIT_ID
import com.djessyczaplicki.groupcalendar.ui.item.CollapsingTopBar.SHARE_ID
import com.djessyczaplicki.groupcalendar.ui.item.CollapsingTopBar.TITLE_ID
import kotlin.math.roundToInt

@Composable
fun Collapsing(
    modifier: Modifier = Modifier,
    collapseFactor: Float = 1f, // A value from (0-1) where 0 means fully expanded
    content: @Composable () -> Unit
) {
    val map = mutableMapOf<Placeable, Int>()
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        map.clear()
        val placeables = mutableListOf<Placeable>()
        measurables.map { measurable ->
            when (measurable.layoutId) {
                BACK_ID -> measurable.measure(constraints)
                SHARE_ID -> measurable.measure(constraints)
                TITLE_ID ->
                    measurable.measure(
                        Constraints.fixedWidth(constraints.maxWidth
                            - (collapseFactor * (placeables.first().width * 2)).toInt()))
                EDIT_ID ->
                    measurable.measure(constraints)
                else -> throw IllegalStateException("Id Not found")
            }.also { placeable ->
                map[placeable] = measurable.layoutId as Int
                placeables.add(placeable)
            }
        }

        // Set the size of the layout as big as it can
        layout(constraints.maxWidth, constraints.maxHeight) {
            placeables.forEach { placeable ->
                when (map[placeable]) {
                    BACK_ID -> placeable.placeRelative(0, 0)
                    SHARE_ID -> placeable.run {
                        placeRelative(constraints.maxWidth - width, 0)
                    }
                    TITLE_ID -> placeable.run {
                        val widthOffset = (placeables[0].width * collapseFactor).roundToInt()
                        val heightOffset = (placeables.first().height - placeable.height) / 2
                        placeRelative(
                            widthOffset,
                            if (collapseFactor == 1f) heightOffset else constraints.maxHeight - height
                        )
                    }
                    EDIT_ID -> placeable.run {
                        placeRelative(constraints.maxWidth - width * 2, 0)
                    }
                }
            }
        }
    }
}

object CollapsingTopBar {
    const val BACK_ID = 1001
    const val SHARE_ID = 1002
    const val TITLE_ID = 1003
    const val EDIT_ID = 1004
    const val COLLAPSE_FACTOR = 0.6f
}


@Composable
fun CollapsingTopBar(
    modifier: Modifier = Modifier,
    currentHeight: Dp,
    title: String,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onEdit: () -> Unit
) {

    Box(
        modifier = modifier.height(currentHeight)
    ) {
        Collapsing(
            collapseFactor = 0.1f,
            modifier = Modifier
        ) {
            Icon(
                modifier = Modifier
                    .wrapContentWidth()
                    .layoutId(BACK_ID)
                    .clickable { onBack() }
                    .padding(12.dp),
                imageVector = Icons.Filled.ArrowBack,
                tint = MaterialTheme.colors.onPrimary,
                contentDescription = "back"
            )
            Icon(
                modifier = Modifier
                    .wrapContentSize()
                    .layoutId(EDIT_ID)
                    .clickable { onEdit() }
                    .padding(12.dp),
                imageVector = Icons.Filled.Edit,
                tint = MaterialTheme.colors.onPrimary,
                contentDescription = "edit"
            )
            Icon(
                modifier = Modifier
                    .wrapContentSize()
                    .layoutId(SHARE_ID)
                    .clickable { onShare() }
                    .padding(12.dp),
                imageVector = Icons.Filled.Share,
                tint = MaterialTheme.colors.onPrimary,
                contentDescription = "share"
            )
            Text(
                modifier = Modifier
                    .layoutId(CollapsingTopBar.TITLE_ID)
                    .wrapContentHeight()
                    .padding(horizontal = 4.dp),
                text = title,
                style = MaterialTheme.typography.h4.copy(color = MaterialTheme.colors.onPrimary),
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}