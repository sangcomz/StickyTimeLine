# StickyTimeLine

[![Maven Central](https://img.shields.io/maven-central/v/io.github.sangcomz/stickytimeline-compose)](https://search.maven.org/artifact/io.github.sangcomz/stickytimeline-compose)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.sangcomz/StickyTimeLine)](https://search.maven.org/artifact/io.github.sangcomz/StickyTimeLine)

StickyTimeLine is timeline view for android. Now supports both View system and Jetpack Compose!

## What's New? :tada:
- [New] Jetpack Compose version released! 🎉

 ## Result Screen
 
 Feel free to send me a pull request with your app and I'll link you here:
 ### Jetpack Compose
|                                                                                                                                                   Sample                                                                                                                                                   |
 |:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
|                                                                                                                          <img width="auto" height="500px" src="/pic/compose.gif">                                                                                                                          |

### View System
 | Sample <p style="float:left;">  <a href="https://play.google.com/store/apps/details?id=xyz.sangcomz.stickytimeline">  <img HEIGHT="40" WIDTH="135" alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/apps/en-play-badge.png" /></a></p>   | AlleysMap <p style="float:left;"> <a href="https://play.google.com/store/apps/details?id=co.alleys.android"> <img HEIGHT="40" WIDTH="135" alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/apps/en-play-badge.png" /> </a></p> | StockRoom <p style="float:left;"> <a href="https://play.google.com/store/apps/details?id=com.thecloudsite.stockroom"> <img HEIGHT="40" WIDTH="135" alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/apps/en-play-badge.png" /></a></p>   |
 |:---------------------------------:|:--------------------------------:|:--------------------------------:|
 | <img src="/pic/sample_result.gif">|<img src="/pic/alleys_result.gif">|<img width="auto" height="500px" src="/pic/stockroom_result.gif">|

## How to Use

### Gradle

#### View System
```groovy
    dependencies {
        implementation 'io.github.sangcomz:StickyTimeLine:x.x.x'
    }
```

#### Jetpack Compose
```groovy
    dependencies {
        implementation 'io.github.sangcomz:stickytimeline-compose:0.0.1'
    }
```
### Usage

#### Jetpack Compose Usage

##### Basic Usage - LazyColumn
```kotlin
@Composable
fun TimeLineExample() {
    val musicList = remember { MusicRepo().musicList }
    val sortedMusicList = musicList.sortedWith(
        compareBy(
            { it.year.toIntOrNull() ?: 0 },
            { it.month.toIntOrNull() ?: 0 }
        )
    )
    
    StickyTimeLineLazyColumn(
        items = sortedMusicList,
        groupBy = { it.year },
        makeHeaderItem = { key, _ -> key },
        sectionHeader = { year ->
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = year,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF414FCA)
                    )
                )
                Text(
                    text = "Popular Music",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color(0xFFD16767)
                    )
                )
            }
        },
        itemContent = { music ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(music.title, style = MaterialTheme.typography.titleMedium)
                    Text(music.artist, style = MaterialTheme.typography.bodyMedium)
                }
            }
        },
        timeLineDot = {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        Color.Gray,
                        shape = CircleShape
                    )
            )
        }
    )
}
```

##### Basic Usage - LazyRow
```kotlin
@Composable
fun TimeLineRowExample() {
    val musicList = remember { MusicRepo().musicList }
    val sortedMusicList = musicList.sortedWith(
        compareBy(
            { it.year.toIntOrNull() ?: 0 },
            { it.month.toIntOrNull() ?: 0 }
        )
    )
    
    StickyTimeLineLazyRow(
        items = sortedMusicList,
        lineColor = Color(0xFF51ae45),
        lineWidth = 2.dp,
        groupBy = { it.year },
        makeHeaderItem = { key, _ -> key },
        headerContent = { year ->
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = year,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF414FCA)
                    )
                )
                Text(
                    text = "Popular Music",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color(0xFFD16767)
                    )
                )
            }
        },
        itemContent = { music ->
            Card(
                modifier = Modifier.wrapContentWidth(),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(music.title, style = MaterialTheme.typography.titleMedium)
                    Text(music.artist, style = MaterialTheme.typography.bodyMedium)
                }
            }
        },
        dotContent = { _ ->
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        Color.Gray,
                        shape = CircleShape
                    )
            )
        }
    )
}
```

##### Parameters - StickyTimeLineLazyColumn

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `modifier` | `Modifier` | `Modifier` | Modifier for the component |
| `items` | `List<T>` | - | List of items to display in the timeline |
| `groupBy` | `(T) -> String` | - | Function to group items by section (returns section key) |
| `makeHeaderItem` | `(key: String, items: List<T>) -> G` | - | Function to create header item from section key and items |
| `generateItemKey` | `(T) -> Any` | `{ it.hashCode() }` | Function to generate unique key for each item (for recomposition optimization) |
| `contentBackgroundColor` | `Color` | `Color.White` | Background color of the content area |
| `lineColor` | `Color` | `Color.Blue` | Color of the timeline line |
| `lineWidth` | `Dp` | `2.dp` | Width of the timeline line |
| `verticalSpaceBy` | `Dp` | `12.dp` | Vertical spacing between items |
| `timeLineHorizontalPadding` | `Dp` | `0.dp` | Horizontal padding for the timeline |
| `timeLineDot` | `@Composable () -> Unit` | - | Composable for timeline dot |
| `sectionHeader` | `@Composable (headerItem: G) -> Unit` | - | Composable for section header |
| `itemContent` | `@Composable (item: T) -> Unit` | - | Composable content for each item |

##### Parameters - StickyTimeLineLazyRow

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `modifier` | `Modifier` | `Modifier` | Modifier for the component |
| `items` | `List<T>` | - | List of items to display in the timeline |
| `groupBy` | `(T) -> String` | - | Function to group items by section (returns section key) |
| `makeHeaderItem` | `(key: String, items: List<T>) -> G` | - | Function to create header item from section key and items |
| `generateItemKey` | `(T) -> Any` | `{ it.hashCode() }` | Function to generate unique key for each item (for recomposition optimization) |
| `headerContent` | `@Composable (headerItem: G) -> Unit` | - | Composable for section header |
| `itemContent` | `@Composable (item: T) -> Unit` | - | Composable content for each item |
| `dotContent` | `@Composable (group: String) -> Unit` | Default blue circle | Composable for timeline dot |
| `lineColor` | `Color` | `Color.Blue` | Color of the timeline line |
| `lineWidth` | `Dp` | `2.dp` | Width of the timeline line |
| `horizontalSpaceBy` | `Dp` | `12.dp` | Horizontal spacing between items |
| `contentPaddingValues` | `PaddingValues` | `PaddingValues(horizontal = 8.dp)` | Padding values for the content |

#### View System Usage

##### activity_main.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context="xyz.sangcomz.stickytimeline.MainActivity">

    <xyz.sangcomz.stickytimelineview.TimeLineRecyclerView
        android:id="@+id/recycler_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
</android.support.constraint.ConstraintLayout>
```
#### MainActivity.kt
```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        val recyclerView: TimeLineRecyclerView = findViewById(R.id.recycler_view)
        
        //Currently only LinearLayoutManager is supported.
        recyclerView.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,
                false)

        //Get data
        val singerList = getSingerList()


        //Add RecyclerSectionItemDecoration.SectionCallback
        recyclerView.addItemDecoration(getSectionCallback(singerList))
        
        //Set Adapter
        recyclerView.adapter = SingerAdapter(layoutInflater,
                singerList,
                R.layout.recycler_row)
    }

    //Get data method
    private fun getSingerList(): List<Singer> = SingerRepo().singerList


    //Get SectionCallback method
    private fun getSectionCallback(singerList: List<Singer>): RecyclerSectionItemDecoration.SectionCallback {
        return object : RecyclerSectionItemDecoration.SectionCallback {
            //In your data, implement a method to determine if this is a section.
            override fun isSection(position: Int): Boolean =
                    singerList[position].debuted != singerList[position - 1].debuted

            //Implement a method that returns a SectionHeader.
            override fun getSectionHeader(position: Int): SectionInfo? =
                    SectionInfo(singerList[position].debuted, singerList[position].group)
        }
    }
}
```

#### JavaExampleActivity.java
```java
public class JavaExampleActivity extends AppCompatActivity {

    private Drawable icFinkl, icBuzz, icWannaOne, icGirlsGeneration, icSolo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDrawable();

        TimeLineRecyclerView recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                RecyclerView.VERTICAL,
                false));

        List<Singer> singerList = getSingerList();

        recyclerView.addItemDecoration(getSectionCallback(singerList));

        recyclerView.setAdapter(new SingerAdapter(getLayoutInflater(), singerList, R.layout.recycler_row));
    }

    private RecyclerSectionItemDecoration.SectionCallback getSectionCallback(final List<Singer> singerList) {
        return new RecyclerSectionItemDecoration.SectionCallback() {

            @Nullable
            @Override
            public SectionInfo getSectionHeader(int position) {
                Singer singer = singerList.get(position);
                Drawable dot;
                switch (singer.getGroup()) {
                    case "FIN.K.L": {
                        dot = icFinkl;
                        break;
                    }
                    case "Girls' Generation": {
                        dot = icGirlsGeneration;
                        break;
                    }
                    case "Buzz": {
                        dot = icBuzz;
                        break;
                    }
                    case "Wanna One": {
                        dot = icWannaOne;
                        break;
                    }
                    default: {
                        dot = icSolo;
                    }
                }
                return new SectionInfo(singer.getDebuted(), singer.getGroup(), dot);
            }

            @Override
            public boolean isSection(int position) {
                return !singerList.get(position).getDebuted().equals(singerList.get(position - 1).getDebuted());
            }
        };
    }

    private List<Singer> getSingerList() {
        return new SingerRepo().getSingerList();
    }

    private void initDrawable() {
        icFinkl = AppCompatResources.getDrawable(this, R.drawable.ic_finkl);
        icBuzz = AppCompatResources.getDrawable(this, R.drawable.ic_buzz);
        icWannaOne = AppCompatResources.getDrawable(this, R.drawable.ic_wannaone);
        icGirlsGeneration = AppCompatResources.getDrawable(this, R.drawable.ic_girlsgeneration);
        icSolo = AppCompatResources.getDrawable(this, R.drawable.ic_wannaone);
    }
}
```
##### caution
- *Currently only LinearLayoutManager is supported.*

#### recycler_row.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<!--Don't set margin value in the parent view-->
<android.support.v7.widget.CardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

    <TextView
        android:id="@+id/full_name_tv"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:padding="10dp" />

</android.support.v7.widget.CardView>
```

##### caution
- *Don't set margin value in the parent view.*

#### attribute

|        Method Name       | Description                                           | Default Value |
|:------------------------:|-------------------------------------------------------|:-------------:|
|sectionBackgroundColor    | To change section section background color            |    #f9f9f9    |
|sectionTitleTextColor     | To change section title color                         |    #414fca    |
|sectionSubTitleTextColor  | To change section sub title color                     |    #d16767    |
|timeLineColor             | To change line color in timeline                      |    #51ae45    |
|timeLineDotColor          | To change dot color in timeline                       |    #51ae45    |
|timeLineCircleStrokeColor | To change dot stroke color in timeline                |    #f9f9f9    |
|sectionTitleTextSize      | To change section title text size                     |      14sp     |
|sectionSubTitleTextSize   | To change section sub title text size                 |      12sp     |
|timeLineWidth             | To change line width in timeline                      |      4dp      |
|isSticky                  | To change Sticky functionality in the Timeline        |      true     |
|customDotDrawable         | To change the circle to custom drawable               |      null     |
|sectionBackgroundColorMode| To change section background area(for horizontal mode)|    MODE_FULL  |
|timeLineDotRadius         | To change dot radius                                  |      8dp      |
|timeLineDotStrokeSize     | To change dot stroke size                             |      4dp      |

# Contribute
We welcome any contributions.

# Inspired by
 * [tim.paetz](https://github.com/paetztm), I was inspired by his code. And I used some of his code in the library.

# License

    Copyright 2019 Jeong Seok-Won

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
