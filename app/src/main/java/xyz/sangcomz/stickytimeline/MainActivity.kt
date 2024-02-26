package xyz.sangcomz.stickytimeline

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import xyz.sangcomz.stickytimelineview.TimeLineRecyclerView
import xyz.sangcomz.stickytimelineview.callback.SectionCallback
import xyz.sangcomz.stickytimelineview.model.SectionInfo

class MainActivity : AppCompatActivity() {

    val icFinkl: Drawable? by lazy {
        AppCompatResources.getDrawable(this@MainActivity, R.drawable.ic_finkl)
    }
    val icBuzz: Drawable? by lazy {
        AppCompatResources.getDrawable(this@MainActivity, R.drawable.ic_buzz)
    }
    val icWannaOne: Drawable? by lazy {
        AppCompatResources.getDrawable(this@MainActivity, R.drawable.ic_wannaone)
    }
    val icGirlsGeneration: Drawable? by lazy {
        AppCompatResources.getDrawable(this@MainActivity, R.drawable.ic_girlsgeneration)
    }
    val icSolo: Drawable? by lazy {
        AppCompatResources.getDrawable(this@MainActivity, R.drawable.ic_solo)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initVerticalRecyclerView()
        initHorizontalRecyclerView()
    }

    private fun initVerticalRecyclerView() {
        val singerList = getSingerList()
        findViewById<TimeLineRecyclerView>(R.id.vertical_recycler_view).apply {
            adapter = SingerAdapter(
                layoutInflater,
                singerList,
                R.layout.recycler_vertical_row
            )
            layoutManager = LinearLayoutManager(
                this@MainActivity,
                RecyclerView.VERTICAL,
                false
            )
            addItemDecoration(getSectionCallback(singerList))
        }
    }

    private fun initHorizontalRecyclerView() {
        val singerList = getSingerList()
        findViewById<TimeLineRecyclerView>(R.id.horizontal_recycler_view).apply {
            adapter = SingerAdapter(
                layoutInflater,
                singerList,
                R.layout.recycler_horizontal_row
            )
            layoutManager = LinearLayoutManager(
                this@MainActivity,
                RecyclerView.HORIZONTAL,
                false
            )
            addItemDecoration(getSectionCallback(singerList))
        }

        findViewById<TimeLineRecyclerView>(R.id.horizontal_recycler_view2).apply {
            adapter = SingerAdapter(
                layoutInflater,
                singerList,
                R.layout.recycler_horizontal_row
            )
            layoutManager = LinearLayoutManager(
                this@MainActivity,
                RecyclerView.HORIZONTAL,
                false
            )
            addItemDecoration(getSectionCallbackWithDrawable(singerList))
        }
    }

    //Get data method
    private fun getSingerList(): List<Singer> = SingerRepo().singerList


    //Get SectionCallback method
    private fun getSectionCallback(singerList: List<Singer>): SectionCallback {
        return object : SectionCallback {
            //In your data, implement a method to determine if this is a section.
            override fun isSection(position: Int): Boolean =
                singerList[position].debuted != singerList[position - 1].debuted

            //Implement a method that returns a SectionHeader.
            override fun getSectionHeader(position: Int): SectionInfo? {
                val singer = singerList[position]
                return SectionInfo(singer.debuted, singer.group)
            }

        }
    }

    private fun getSectionCallbackWithDrawable(singerList: List<Singer>): SectionCallback {
        return object : SectionCallback {
            //In your data, implement a method to determine if this is a section.
            override fun isSection(position: Int): Boolean =
                singerList[position].debuted != singerList[position - 1].debuted

            //Implement a method that returns a SectionHeader.
            override fun getSectionHeader(position: Int): SectionInfo? {
                val singer = singerList[position]
                val dot: Drawable? = when (singer.group) {
                    "FIN.K.L" -> {
                        icFinkl
                    }
                    "Girls' Generation" -> {
                        icGirlsGeneration
                    }
                    "Buzz" -> {
                        icBuzz
                    }
                    "Wanna One" -> {
                        icWannaOne
                    }
                    else -> icSolo
                }
                return SectionInfo(singer.debuted, singer.group, dot)
            }

        }
    }
}
