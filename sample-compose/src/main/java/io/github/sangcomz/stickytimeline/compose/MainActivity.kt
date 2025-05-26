package io.github.sangcomz.stickytimeline.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.sangcomz.stickytimelineview.compose.StickyTimeLineLazyRow
import io.github.sangcomz.stickytimeline.compose.ui.theme.StickyTimeLineTheme
import io.github.sangcomz.stickytimeline.data.Music
import io.github.sangcomz.stickytimeline.data.MusicRepo
import io.github.sangcomz.stickytimelineview.compose.StickyTimeLineLazyColumn

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StickyTimeLineTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeContentPadding(),
                ) {
                    val musicList = MusicRepo().musicList
                    val sortedMusicList = musicList.sortedWith(
                        compareBy(
                            { it.year.toIntOrNull() ?: 0 },
                            { it.month.toIntOrNull() ?: 0 }
                        )
                    )

                    Column(
                        Modifier.background(Color.White)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .background(Color(0xff00118F)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "StickyTimeLineLazyColumn",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = Color.White
                                ),
                                textAlign = TextAlign.Center
                            )
                        }

                        StickyTimeLineLazyColumn(
                            modifier = Modifier.weight(.5f),
                            items = sortedMusicList,
                            makeHeaderItem = { key, _ ->
                                key
                            },
                            groupBy = { it.year },
                            sectionHeader = { year ->
                                SectionHeaderForLazyColumn(year = year)
                            },
                            itemContent = { music ->
                                MusicCardForLazyColumn(music = music)
                            },
                            timeLineDot = {
                                Dot()
                            }
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .background(Color(0xff003E8F)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "StickyTimeLineLazyRow",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = Color.White
                                ),
                                textAlign = TextAlign.Center
                            )
                        }

                        StickyTimeLineLazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(.5f),
                            items = sortedMusicList,
                            lineColor = Color(0xff003E8F),
                            lineWidth = 2.dp,
                            groupBy = { it.year },
                            makeHeaderItem = { key, _ ->
                                key
                            },
                            headerContent = { year ->
                                SectionHeaderForLazyRow(year = year)
                            },
                            itemContent = { music ->
                                MusicCardForLazyRow(music = music)
                            },
                            dotContent = { _ ->
                                DotForLazyRow()
                            },
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun SectionHeaderForLazyColumn(
    year: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color.White)
            .padding(8.dp)
            .wrapContentHeight()
            .fillMaxWidth()
    ) {
        Column {
            Text(
                text = year,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF414FCA)
                )
            )
            Text(
                text = "Popular Songs",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color(0xFFD16767)
                )
            )
        }
    }
}

@Composable
fun SectionHeaderForLazyRow(
    year: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .wrapContentSize()
            .padding(horizontal = 8.dp)
    ) {
        Column {
            Text(
                text = year,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF414FCA)
                )
            )
            Text(
                text = "Popular Songs",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color(0xFFD16767)
                )
            )
        }
    }
}

@Composable
fun MusicCardForLazyRow(music: Music) {
    Card(
        modifier = Modifier.wrapContentWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(music.title, style = MaterialTheme.typography.titleMedium)
            Text(music.artist, style = MaterialTheme.typography.bodyMedium)
            Text(music.album, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "${music.year} ${music.month} · ${music.genre}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(music.duration, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun MusicCardForLazyColumn(music: Music) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(music.title, style = MaterialTheme.typography.titleMedium)
            Text(music.artist, style = MaterialTheme.typography.bodyMedium)
            Text(music.album, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "${music.year} ${music.month} · ${music.genre}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(music.duration, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun Dot() {
    Box(
        modifier = Modifier
            .width(48.dp)
            .wrapContentHeight(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    Color.Gray.copy(alpha = 0.2f),
                    shape = androidx.compose.foundation.shape.CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(24.dp)
                .background(Color.Gray, shape = androidx.compose.foundation.shape.CircleShape)
        )
    }
}

@Composable
fun DotForLazyRow() {
    Box(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .width(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    Color.Gray.copy(alpha = 0.2f),
                    shape = androidx.compose.foundation.shape.CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(12.dp)
                .background(Color.Gray, shape = androidx.compose.foundation.shape.CircleShape)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StickyTimeLineTheme {

    }
}