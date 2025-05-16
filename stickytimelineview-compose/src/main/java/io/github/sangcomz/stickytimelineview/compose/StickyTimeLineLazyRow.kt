package io.github.sangcomz.stickytimelineview.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

/**
 * A fully generic StickyHeader LazyRow library component.
 *
 * @param items list of data items
 * @param groupBy lambda to extract group key from item
 * @param headerContent composable for group header
 * @param itemContent composable for item
 */
@Composable
fun <T> StickyTimeLineLazyRow(
    modifier: Modifier = Modifier,
    items: List<T>,
    groupBy: (T) -> String,
    lineColor: Color = Color.Blue,
    lineWidth: Dp = 2.dp,
    headerContent: @Composable (group: String) -> Unit,
    itemContent: @Composable (item: T) -> Unit,
    dotContent: @Composable (group: String) -> Unit = {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(Color.Blue, CircleShape)
        )
    },
) {
    val state = rememberLazyListState()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            HeaderWithTimeLine(
                state = state,
                items = items,
                groupBy = groupBy,
                lineColor = lineColor,
                lineWidth = lineWidth,
                headerContent = headerContent,
                dotContent = dotContent
            )
            LazyRow(
                state = state,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items) { item ->
                    itemContent(item)
                }
            }
        }
    }
}

@Composable
fun <T> HeaderWithTimeLine(
    state: LazyListState,
    items: List<T>,
    groupBy: (T) -> String,
    lineColor: Color,
    lineWidth: Dp,
    headerContent: @Composable (group: String) -> Unit,
    dotContent: @Composable (group: String) -> Unit
) {
    SubcomposeLayout { constraints ->
        val visibleItems = state.layoutInfo.visibleItemsInfo
        val verticalPadding = 8.dp.toPx()

        if (visibleItems.isEmpty()) return@SubcomposeLayout layout(0, 0) {}

        val visibleGroups = visibleItems
            .mapNotNull { items.getOrNull(it.index)?.let(groupBy) }
            .distinct()
            .sortedBy { group -> items.indexOfFirst { groupBy(it) == group } }

        val firstGroup = visibleGroups.first()
        val nextGroup = visibleGroups.getOrNull(1)


        val headers = visibleGroups.map { groupId ->
            val header = subcompose("StickyHeader-$groupId") {
                headerContent(groupId)
            }.map { it.measure(constraints) }

            val firstItem = visibleItems.firstOrNull {
                items.getOrNull(it.index)?.let(groupBy) == groupId
            }
            val offsetX = firstItem?.offset ?: 0

            groupId to (header to offsetX)
        }

        val dots = visibleGroups.map { groupId ->
            val dot = subcompose("StickyDot-$groupId") {
                dotContent(groupId)
            }.map { it.measure(constraints) }

            val firstItem = visibleItems.firstOrNull {
                items.getOrNull(it.index)?.let(groupBy) == groupId
            }
            val offsetX = firstItem?.offset ?: 0

            groupId to (dot to offsetX)
        }

        val linePlaceables = subcompose("Line") {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(lineWidth)
                    .background(lineColor)
            )
        }.map { it.measure(constraints) }

        val firstHeader = headers.firstOrNull { it.first == firstGroup }
        val firstHeaderHeight = firstHeader
            ?.second
            ?.let { (placeables, _) ->
                placeables.maxOfOrNull { it.height } ?: 0
            } ?: 0

        val firstHeaderWidth = firstHeader?.second?.first?.maxOfOrNull { it.width } ?: 0
        val pushOffset = nextGroup?.let { next ->
            val nextItem = visibleItems.firstOrNull {
                items.getOrNull(it.index)?.let(groupBy) == next
            }

            nextItem?.let {
                val distanceToNext = it.offset - firstHeaderWidth
                if (distanceToNext < 0) distanceToNext else 0
            } ?: 0
        } ?: 0

        val firstDotHeight = dots.firstOrNull { it.first == firstGroup }
            ?.second
            ?.let { (placeables, _) ->
                placeables.maxOfOrNull { it.height } ?: 0
            } ?: 0

        val lineAndDotYValuePair = if (lineWidth.roundToPx() > firstDotHeight) {
            firstHeaderHeight + verticalPadding.toInt() to firstHeaderHeight + verticalPadding.toInt() + (lineWidth / 2).roundToPx() - (firstDotHeight / 2)
        } else {
            firstHeaderHeight + verticalPadding.toInt() - (lineWidth / 2).roundToPx() + (firstDotHeight / 2) to firstHeaderHeight + verticalPadding.toInt()
        }

        layout(
            constraints.maxWidth,
            firstHeaderHeight + max(
                lineWidth.roundToPx(),
                firstDotHeight
            ) + (verticalPadding * 2).toInt()
        ) {
            linePlaceables.forEach {
                it.placeRelative(
                    x = 0,
                    y = lineAndDotYValuePair.first
                )
            }

            headers.forEach { (groupId, pair) ->
                val (placeables, itemOffset) = pair
                val x = if (groupId == firstGroup) pushOffset else itemOffset
                placeables.forEach {
                    it.placeRelative(
                        x = x,
                        y = 0
                    )
                }
            }

            dots.forEach { (groupId, pair) ->
                val (placeables, itemOffset) = pair
                val x = if (groupId == firstGroup) pushOffset else itemOffset
                placeables.forEach {
                    it.placeRelative(
                        x = x,
                        y = lineAndDotYValuePair.second
                    )
                }
            }
        }
    }
}