# StickyTimeLine

[![](https://jitpack.io/v/sangcomz/StickyTimeLine.svg)](https://jitpack.io/#sangcomz/StickyTimeLine)

StickyTimeLine is timeline view for android.

## What's New in 0.0.19? :tada:
- change DotDrawable for each row of items(#16)

## How to Use

### Gradle
```groovy
    repositories {
        maven { url 'https://jitpack.io' }
    }

    dependencies {
        compile 'com.github.sangcomz:StickyTimeLine:v0.0.19'
    }
```
### Usage
#### activity_main.xml
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

|        Method Name       | Description                                    | Default Value |
|:------------------------:|------------------------------------------------|:-------------:|
|  sectionBackgroundColor  | To change section section background color     |    #f9f9f9    |
|   sectionTitleTextColor  | To change section title color                  |    #414fca    |
| sectionSubTitleTextColor | To change section sub title color              |    #d16767    |
|     timeLineColor        | To change line color in timeline               |    #51ae45    |
|    timeLineCircleColor   | To change circle color in timeline             |    #51ae45    |
| timeLineCircleStrokeColor| To change circle stroke color in timeline      |    #f9f9f9    |
|   sectionTitleTextSize   | To change section title text size              |      14sp     |
|  sectionSubTitleTextSize | To change section sub title text size          |      12sp     |
|     timeLineWidth        | To change line width in timeline               |      4dp      |
|     isSticky             | To change Sticky functionality in the Timeline |      true     |
|    customDotDrawable     | To change the circle to custom drawable        |      null     |

## Result Screen

| Project Name | Result Screen   |
|:---------:|---|
| Sample  <p style="float:left;"> <a href="https://play.google.com/store/apps/details?id=xyz.sangcomz.stickytimeline"> <img HEIGHT="40" WIDTH="135" alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/apps/en-play-badge.png" /></a></p> |  <img src="/pic/sample_result.gif"> |
| AlleysMap  <p style="float:left;"> <a href="https://play.google.com/store/apps/details?id=co.alleys.android"> <img HEIGHT="40" WIDTH="135" alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/apps/en-play-badge.png" /></a></p> | <img src="/pic/alleys_result.gif">  |

# Contribute
We welcome any contributions.

# Inspired by
 * [tim.paetz](https://github.com/paetztm), I was inspired by his code. And I used some of his code in the library.

# License

    Copyright 2018 Jeong Seok-Won

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
