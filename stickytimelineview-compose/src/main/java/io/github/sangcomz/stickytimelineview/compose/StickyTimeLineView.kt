package io.github.sangcomz.stickytimelineview.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun <T> StickyTimeLineView(
    groupedItems: Map<String, List<T>>,
    modifier: Modifier = Modifier,
    timeLineDot: @Composable () -> Unit = { TimelineDot() },
    sectionHeader: @Composable (key: String, firstItem: T) -> Unit,
    itemContent: @Composable (item: T) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxHeight()
                .width(24.dp)
                .align(Alignment.TopStart)
        ) {
            val centerX = size.width / 2
            drawLine(
                color = Color(0xFFB71C1C),
                start = Offset(centerX, 0f),
                end = Offset(centerX, size.height),
                strokeWidth = 4.dp.toPx()
            )
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            groupedItems.forEach { (key, items) ->
                stickyHeader {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        Box {
                            Spacer(Modifier.size(24.dp))
                            timeLineDot()
                        }

                        sectionHeader(key, items.first())
                    }
                }
                itemsIndexed(items) { _, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Spacer(Modifier.size(24.dp))
                        itemContent(item)
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineDot(dotColor: Color = Color(0xFFB71C1C)) {
    Box(
        modifier = Modifier
            .width(24.dp)
            .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(dotColor, shape = androidx.compose.foundation.shape.CircleShape)
        )
    }
}