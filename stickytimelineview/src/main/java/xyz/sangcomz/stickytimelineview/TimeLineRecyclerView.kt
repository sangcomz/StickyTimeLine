package xyz.sangcomz.stickytimelineview

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import xyz.sangcomz.stickytimelineview.model.RecyclerViewAttr


/**
 * Created by seokwon.jeong on 16/11/2017.
 */

class TimeLineRecyclerView(context: Context?, attrs: AttributeSet?) : RecyclerView(context, attrs) {

    private var recyclerViewAttr: RecyclerViewAttr? = null

    init {
        attrs?.let {
            val a = context?.theme?.obtainStyledAttributes(
                    attrs,
                    R.styleable.TimeLineRecyclerView,
                    0, 0)

            a?.let {
                recyclerViewAttr =
                        RecyclerViewAttr(it.getColor(R.styleable.TimeLineRecyclerView_sectionBackgroundColor,
                                ContextCompat.getColor(context, R.color.colorDefaultBackground)),
                                it.getColor(R.styleable.TimeLineRecyclerView_sectionTitleTextColor,
                                        ContextCompat.getColor(context, R.color.colorDefaultTitle)),
                                it.getColor(R.styleable.TimeLineRecyclerView_sectionSubTitleTextColor,
                                        ContextCompat.getColor(context, R.color.colorDefaultSubTitle)),
                                it.getColor(R.styleable.TimeLineRecyclerView_timeLineColor,
                                        ContextCompat.getColor(context, R.color.colorDefaultTitle)),
                                it.getColor(R.styleable.TimeLineRecyclerView_timeLineCircleColor,
                                        ContextCompat.getColor(context, R.color.colorDefaultTitle)),
                                it.getColor(R.styleable.TimeLineRecyclerView_timeLineCircleStrokeColor,
                                        ContextCompat.getColor(context, R.color.colorDefaultStroke)),
                                it.getDimension(R.styleable.TimeLineRecyclerView_sectionTitleTextSize,
                                        context.resources.getDimension(R.dimen.title_text_size)),
                                it.getDimension(R.styleable.TimeLineRecyclerView_sectionSubTitleTextSize,
                                        context.resources.getDimension(R.dimen.sub_title_text_size)),
                                it.getDimension(R.styleable.TimeLineRecyclerView_timeLineWidth,
                                        context.resources.getDimension(R.dimen.line_width)))
            }

        }
    }

    fun addItemDecoration(callback: RecyclerSectionItemDecoration.SectionCallback) {
        recyclerViewAttr?.let {
            this.addItemDecoration(RecyclerSectionItemDecoration(context,
                    true,
                    callback,
                    it))
        }


    }

}