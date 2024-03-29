package xyz.sangcomz.github.stickytimelineview.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun StickyTimeLineView() {
    Text(
        text = "Hello",
        modifier = Modifier
    )
    LazyColumn {
        items(100) {
            Box {
                Text("Item $it")
            }
        }
    }
    Button(onClick = { /*TODO*/ }) {

    }
}