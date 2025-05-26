package io.github.sangcomz.stickytimelineview.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import kotlin.math.roundToInt

@Composable
fun <T, G> StickyTimeLineLazyColumn(
    modifier: Modifier = Modifier,
    items: List<T>,
    groupBy: (T) -> String,
    makeHeaderItem: (key: String, items: List<T>) -> G,
    generateItemKey: ((T) -> Any) = { it.hashCode() },
    contentBackgroundColor: Color = Color.White,
    lineColor: Color = Color.Blue,
    lineWidth: Dp = 2.dp,
    verticalSpaceBy: Dp = 12.dp,
    timeLineHorizontalPadding: Dp = 0.dp,
    timeLineDot: @Composable () -> Unit,
    sectionHeader: @Composable (headerItem: G) -> Unit,
    itemContent: @Composable (item: T) -> Unit,
) {
    val density = LocalDensity.current
    val listState = rememberLazyListState()
    var stickyHeaderHeight by remember { mutableIntStateOf(0) }
    val dotWidthPx = remember { mutableIntStateOf(0) }

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

    val headerIndexList = remember(groupedMap) {
        val list = mutableListOf<Pair<String, Int>>()
        var index = 0
        groupedMap.forEach { (key, items) ->
            list.add(key to index)
            index += 1 + items.size
        }
        list
    }

    val timeLineTotalWidth by remember {
        derivedStateOf {
            with(density) {
                timeLineHorizontalPadding * 2 + max(dotWidthPx.intValue.toDp(), lineWidth)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(contentBackgroundColor)
            .clipToBounds()
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxHeight()
                .width(timeLineTotalWidth)
                .align(Alignment.TopStart)
        ) {
            val centerX = size.width / 2
            drawLine(
                color = lineColor,
                start = Offset(centerX, 0f),
                end = Offset(centerX, size.height),
                strokeWidth = lineWidth.toPx()
            )
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(verticalSpaceBy)
        ) {
            groupedMap.forEach { (key, items) ->
                val headerItem = headerItemMap[key]!!
                item {
                    HeaderItem(
                        headerItem = headerItem,
                        dotWidthPx = dotWidthPx,
                        timeLineDot = timeLineDot,
                        sectionHeader = sectionHeader,
                        contentBackgroundColor = contentBackgroundColor,
                        lineColor = lineColor,
                        lineWidth = lineWidth
                    )
                }
                itemsIndexed(items) { _, item ->
                    key(generateItemKey(item)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Spacer(Modifier.width(timeLineTotalWidth))
                            itemContent(item)
                        }
                    }
                }
            }
        }

        val currentStickyHeaderKey = remember {
            derivedStateOf {
                headerIndexList
                    .takeWhile { it.second <= listState.firstVisibleItemIndex }
                    .lastOrNull()?.first ?: headerIndexList.firstOrNull()?.first
            }
        }

        currentStickyHeaderKey.value?.let { key ->
            val nextHeaderIndex = headerIndexList.indexOfFirst { it.first == key } + 1
            val nextHeaderOffset = listState.layoutInfo.visibleItemsInfo
                .firstOrNull { it.index == headerIndexList.getOrNull(nextHeaderIndex)?.second }
                ?.offset ?: 0

            val yOffset = if (nextHeaderOffset in 1 until stickyHeaderHeight) {
                nextHeaderOffset - stickyHeaderHeight
            } else {
                0
            }

            val headerItem = headerItemMap[key]!!

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(0, yOffset) }
                    .onGloballyPositioned { coordinates ->
                        stickyHeaderHeight = coordinates.size.height
                    }
            ) {
                HeaderItem(
                    headerItem = headerItem,
                    dotWidthPx = dotWidthPx,
                    timeLineDot = timeLineDot,
                    sectionHeader = sectionHeader,
                    contentBackgroundColor = contentBackgroundColor,
                    lineColor = lineColor,
                    lineWidth = lineWidth
                )
            }
        }
    }
}

@Composable
private fun <G> HeaderItem(
    headerItem: G,
    dotWidthPx: MutableIntState,
    timeLineDot: @Composable () -> Unit,
    sectionHeader: @Composable (G) -> Unit,
    contentBackgroundColor: Color,
    lineColor: Color,
    lineWidth: Dp,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val horizontalPadding = 8.dp
    val horizontalPaddingPx = with(density) { horizontalPadding.roundToPx() }
    val lineWidthPx = with(density) { lineWidth.toPx().roundToInt() }

    SubcomposeLayout(
        modifier = modifier.background(contentBackgroundColor)
    ) { constraints ->

        val dotPlaceable = subcompose("dot", timeLineDot).first().measure(constraints)

        val timeLineWidthPx = maxOf(dotPlaceable.width, lineWidthPx) + horizontalPaddingPx * 2

        val linePlaceable = subcompose("line") {
            Box(
                modifier = Modifier
                    .width(with(density) { timeLineWidthPx.toDp() })
                    .fillMaxHeight()
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val centerX = size.width / 2
                    drawLine(
                        color = lineColor,
                        start = Offset(centerX, 0f),
                        end = Offset(centerX, size.height),
                        strokeWidth = lineWidth.toPx()
                    )
                }
            }
        }.first().measure(constraints)

        val headerPlaceable = subcompose("header") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Spacer(Modifier.width(with(density) { timeLineWidthPx.toDp() }))
                sectionHeader(headerItem)
            }
        }.first().measure(constraints)

        val height = maxOf(dotPlaceable.height, headerPlaceable.height)

        dotWidthPx.intValue = timeLineWidthPx

        layout(constraints.maxWidth, height) {
            linePlaceable.placeRelative(0, 0)
            val dotX = (timeLineWidthPx - dotPlaceable.width) / 2
            val dotY = (height - dotPlaceable.height) / 2
            dotPlaceable.placeRelative(dotX, dotY)
            headerPlaceable.placeRelative(0, 0)
        }
    }
}