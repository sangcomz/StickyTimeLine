package io.github.sangcomz.stickytimelineview.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

@Composable
fun <T, G> StickyTimeLineLazyRow(
    modifier: Modifier = Modifier,
    items: List<T>,
    groupBy: (T) -> String,
    makeHeaderItem: (key: String, items: List<T>) -> G,
    generateItemKey: ((T) -> Any) = { it.hashCode() },
    headerContent: @Composable (headerItem: G) -> Unit,
    itemContent: @Composable (item: T) -> Unit,
    dotContent: @Composable (group: String) -> Unit = {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(Color.Blue, CircleShape)
        )
    },
    lineColor: Color = Color.Blue,
    lineWidth: Dp = 2.dp,
    horizontalSpaceBy: Dp = 12.dp,
    contentPaddingValues: PaddingValues = PaddingValues(horizontal = 8.dp),
) {
    val state = rememberLazyListState()

    val groupedMap = remember(items, groupBy) {
        val map = linkedMapOf<String, MutableList<T>>()
        for (item in items) {
            val key = groupBy(item)
            val list = map.getOrPut(key) { mutableListOf() }
            list.add(item)
        }
        map
    }

    val headerItemMap = remember(groupedMap, makeHeaderItem) {
        groupedMap.mapValues { (key, value) -> makeHeaderItem(key, value) }
    }

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            HeaderWithTimeLineOptimized(
                state = state,
                groupedMap = groupedMap,
                headerItemMap = headerItemMap,
                headerContent = headerContent,
                dotContent = dotContent,
                lineColor = lineColor,
                lineWidth = lineWidth
            )
            LazyRow(
                state = state,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = contentPaddingValues,
                horizontalArrangement = Arrangement.spacedBy(horizontalSpaceBy)
            ) {
                items(items) { item ->
                    key(generateItemKey(item)) {
                        itemContent(item)
                    }
                }
            }
        }
    }
}

@Composable
fun <T, G> HeaderWithTimeLineOptimized(
    state: LazyListState,
    groupedMap: Map<String, List<T>>,
    headerItemMap: Map<String, G>,
    headerContent: @Composable (headerItem: G) -> Unit,
    dotContent: @Composable (group: String) -> Unit,
    lineColor: Color,
    lineWidth: Dp
) {
    SubcomposeLayout { constraints ->
        val visibleItems = state.layoutInfo.visibleItemsInfo
        val verticalPadding = 8.dp.toPx()

        if (visibleItems.isEmpty()) return@SubcomposeLayout layout(0, 0) {}

        val visibleGroups = visibleItems
            .mapNotNull { itemInfo ->
                val idx = itemInfo.index
                groupedMap.entries.firstOrNull { entry ->
                    idx >= 0 && groupedMap.values.takeWhile { it !== entry.value }.sumOf { it.size } <= idx
                            && idx < groupedMap.values.takeWhile { it !== entry.value }.sumOf { it.size } + entry.value.size
                }?.key
            }
            .distinct()

        if (visibleGroups.isEmpty()) return@SubcomposeLayout layout(0, 0) {}

        val firstGroup = visibleGroups.first()
        val nextGroup = visibleGroups.getOrNull(1)

        val headers = visibleGroups.map { groupId ->
            val header = subcompose("StickyHeader-$groupId") {
                headerContent(headerItemMap[groupId]!!)
            }.map { it.measure(constraints) }

            val firstItem = visibleItems.firstOrNull {
                val idx = it.index
                val items = groupedMap[groupId] ?: emptyList()
                val groupStart = groupedMap.values.takeWhile { it !== items }.sumOf { it.size }
                idx in groupStart until (groupStart + items.size)
            }
            val offsetX = firstItem?.offset ?: 0

            groupId to (header to offsetX)
        }

        val dots = visibleGroups.map { groupId ->
            val dot = subcompose("StickyDot-$groupId") {
                dotContent(groupId)
            }.map { it.measure(constraints) }

            val firstItem = visibleItems.firstOrNull {
                val idx = it.index
                val items = groupedMap[groupId] ?: emptyList()
                val groupStart = groupedMap.values.takeWhile { it !== items }.sumOf { it.size }
                idx in groupStart until (groupStart + items.size)
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
                val idx = it.index
                val items = groupedMap[next] ?: emptyList()
                val groupStart = groupedMap.values.takeWhile { it !== items }.sumOf { it.size }
                idx in groupStart until (groupStart + items.size)
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
                val x = if (groupId == firstGroup) pushOffset else max(itemOffset, 0)
                placeables.forEach {
                    it.placeRelative(
                        x = x,
                        y = 0
                    )
                }
            }

            dots.forEach { (groupId, pair) ->
                val (placeables, itemOffset) = pair
                val x = if (groupId == firstGroup) pushOffset else max(itemOffset, 0)
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