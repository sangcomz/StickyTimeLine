# StickyTimeLine

[![](https://jitpack.io/v/sangcomz/StickyTimeLine.svg)](https://jitpack.io/#sangcomz/StickyTimeLine)

StickyTimeLine is timeline view for android.

## How to Use

### Gradle
```
    repositories {
        maven { url 'https://jitpack.io' }
    }

    dependencies {
        compile 'com.github.sangcomz:StickyTimeLine:v0.0.12'
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
```
        val recyclerView: TimeLineRecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,
                false)

        recyclerView.addItemDecoration(getSectionCallback(singerList))
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


## Result Screen

| Project Name | Result Screen   |
|-------|---|
| Sample |  <img src="/pic/sample_result.gif"> |
| AlleysMap | <img src="/pic/alleys_result.gif">  |

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
