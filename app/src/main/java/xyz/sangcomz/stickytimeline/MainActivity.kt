package xyz.sangcomz.stickytimeline

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import xyz.sangcomz.stickytimelineview.RecyclerSectionItemDecoration
import xyz.sangcomz.stickytimelineview.TimeLineRecyclerView
import xyz.sangcomz.stickytimelineview.model.SectionInfo

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: TimeLineRecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,
                false)

        val singerList = getSingerList()

        recyclerView.addItemDecoration(getSectionCallback(singerList))
        recyclerView.adapter = SingerAdapter(layoutInflater,
                singerList,
                R.layout.recycler_row)
    }

    private fun getSingerList(): List<Singer> = SingerRepo().singerList


    private fun getSectionCallback(singerList: List<Singer>): RecyclerSectionItemDecoration.SectionCallback {
        return object : RecyclerSectionItemDecoration.SectionCallback {
            override fun isSection(position: Int): Boolean =
                    singerList[position].debuted != singerList[position - 1].debuted

            override fun getSectionHeader(position: Int): SectionInfo
                    = SectionInfo(singerList[position].debuted,
                    singerList[position].group)
        }
    }

}
