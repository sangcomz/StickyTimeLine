package io.github.sangcomz.stickytimeline.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.sangcomz.stickytimeline.compose.ui.theme.StickyTimeLineTheme
import io.github.sangcomz.stickytimeline.data.Singer
import io.github.sangcomz.stickytimeline.data.SingerRepo
import io.github.sangcomz.stickytimelineview.compose.StickyTimeLineView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StickyTimeLineTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val singers = SingerRepo().singerList
                    StickyTimeLineView(
                        groupedItems = singers.groupBy { it.debuted }.toSortedMap(),
                        sectionHeader = { year, firstSinger ->
                            SectionHeader(year = year, group = firstSinger.group)
                        },
                        itemContent = { singer ->
                            SingerItem(singer = singer)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    year: String,
    group: String,
) {
    Surface(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.wrapContentHeight().padding(8.dp)
        ) {
            Column() {
                Text(
                    text = year,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF414FCA)
                    )
                )
                Text(
                    text = group,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color(0xFFD16767)
                    )
                )
            }
        }
    }
}

@Composable
fun SingerItem(singer: Singer) {
    Row(
        modifier = Modifier
            .wrapContentSize()
            .padding(top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Card(
            modifier = Modifier.wrapContentSize(),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = singer.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StickyTimeLineTheme {

    }
}