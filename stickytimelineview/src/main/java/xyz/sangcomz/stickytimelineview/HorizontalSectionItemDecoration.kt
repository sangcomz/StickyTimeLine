package xyz.sangcomz.stickytimelineview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import xyz.sangcomz.stickytimelineview.ext.DP
import xyz.sangcomz.stickytimelineview.model.RecyclerViewAttr
import xyz.sangcomz.stickytimelineview.model.SectionInfo
import kotlin.math.max

class HorizontalSectionItemDecoration(context: Context,
                                      private val sectionCallback: RecyclerSectionItemDecoration.SectionCallback,
                                      private val recyclerViewAttr: RecyclerViewAttr) : RecyclerView.ItemDecoration() {
    private lateinit var previousHeader: SectionInfo

    private var headerView: View? = null
    private var headerBackground: LinearLayout? = null
    private var headerTitle: TextView? = null
    private var headerSubTitle: TextView? = null
    private var dot: AppCompatImageView? = null
    private var defaultOffset: Int = 8.DP(context).toInt()
    private var headerOffset = defaultOffset * 8

    private var isSameTitle = false

    override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
    ) {
        super.getItemOffsets(
                outRect,
                view,
                parent,
                state
        )

        outRect.top = headerOffset

        val leftMargin = defaultOffset * 6
        val rightMargin = defaultOffset * 2

        outRect.bottom = defaultOffset / 2
        outRect.left = leftMargin
        outRect.right = rightMargin

    }


    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        if (!::previousHeader.isInitialized) previousHeader = SectionInfo("")
        if (headerView == null) getHeaderView(parent)
        drawLine(c, parent)

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)
            if(!recyclerViewAttr.isSticky){
            if (getIsSection(position)) {
                sectionCallback.getSectionHeader(position)?.let { sectionInfo ->
                    setHeaderView(sectionInfo)
                    headerView?.let {
                        drawHeader(c, child, it)
                        previousHeader = sectionInfo
                    }
                }}
            }else{
                sectionCallback.getSectionHeader(position)?.let { sectionInfo ->
                    if(sectionInfo.title == previousHeader.title){
                        setHeaderView(sectionInfo)
                        headerView?.let {
                            isSameTitle = true
                            drawHeader(c, child, it)
                            previousHeader = sectionInfo
                        }
                    }else{
                        setHeaderView(sectionInfo)
                        headerView?.let {
                            isSameTitle =false
                            drawHeader(c, child, it)
                            previousHeader = sectionInfo
                        }
                    }
                }

            }
        }
    }


    private fun setHeaderView(sectionInfo: SectionInfo) {
        headerTitle?.text = sectionInfo.title
//        setHeaderSubTitle(sectionInfo.subTitle)
        setDotDrawable(sectionInfo.dotDrawable)
    }

    /**
     * Returns the oval dotDrawable of the timeline.
     */
    private fun getOvalDrawable(): Drawable {
        val strokeWidth = defaultOffset / 2
        val roundRadius = defaultOffset * 2
        val strokeColor = recyclerViewAttr.sectionStrokeColor
        val fillColor = recyclerViewAttr.sectionCircleColor

        val gd = GradientDrawable()
        gd.setColor(fillColor)
        gd.cornerRadius = roundRadius.toFloat()
        gd.setStroke(strokeWidth, strokeColor)

        return gd
    }

    private fun setDotDrawable(sectionDotDrawable: Drawable?) {
        val dotDrawable = sectionDotDrawable ?: recyclerViewAttr.customDotDrawable
        dot?.background = dotDrawable ?: getOvalDrawable()
    }

    private fun getHeaderView(parent: RecyclerView) {
        headerView = inflateHeaderView(parent)
        headerView?.let { headerView ->
            headerBackground = headerView.findViewById(R.id.v_item_background)
            headerTitle = headerView.findViewById(R.id.list_item_section_title)
            headerSubTitle = headerView.findViewById(R.id.list_item_section_sub_title)
            dot = headerView.findViewById(R.id.dot)
            recyclerViewAttr.let { attrs ->
                headerBackground?.apply {
                    setBackgroundColor(attrs.sectionBackgroundColor)
                    val customLine = TextView(context)
                    val customLine2 = TextView(context)
                    customLine.setBackgroundColor(attrs.sectionLineColor)
                    val width = (headerTitle!!.textSize * headerTitle!!.length().toFloat()).toInt()
                    addView(customLine2, width, headerTitle!!.measuredHeight)
                    addView(customLine, ViewGroup.LayoutParams.MATCH_PARENT, attrs.sectionLineWidth.toInt())
                }
                headerTitle?.apply {
                    setPadding(defaultOffset / 2, 0, defaultOffset / 2, 0)
                    setTextColor(attrs.sectionTitleTextColor)
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, attrs.sectionTitleTextSize)
                }
                headerSubTitle?.apply {
                    setPadding(defaultOffset / 2, 0, defaultOffset / 2, 0)
                    setTextColor(attrs.sectionSubTitleTextColor)
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, attrs.sectionSubTitleTextSize)
                }
            }
            fixLayoutSize(headerView, parent)
        }
    }

    private fun inflateHeaderView(parent: RecyclerView): View {
        return LayoutInflater.from(parent.context)
                .inflate(
                        R.layout.recycler_section_header,
                        parent,
                        false
                )
    }

    private fun fixLayoutSize(view: View, parent: ViewGroup) {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(
                parent.width,
                View.MeasureSpec.EXACTLY
        )
        val heightSpec = View.MeasureSpec.makeMeasureSpec(
                parent.height,
                View.MeasureSpec.UNSPECIFIED
        )

        val childWidth = ViewGroup.getChildMeasureSpec(
                widthSpec,
                0,
                view.layoutParams.width
        )
        val childHeight = ViewGroup.getChildMeasureSpec(
                heightSpec,
                0,
                view.layoutParams.height
        )

        view.measure(
                childWidth,
                childHeight
        )

        view.layout(
                0,
                0,
                view.measuredWidth,
                view.measuredHeight
        )
    }

    private fun drawLine(c: Canvas, parent: RecyclerView) {
        val paint = Paint()
        paint.color = recyclerViewAttr.sectionLineColor
        paint.strokeWidth = recyclerViewAttr.sectionLineWidth
        c.drawLines(
                floatArrayOf(
                        0f,
                        defaultOffset * 4f,
                        parent.width.toFloat(),
                        defaultOffset * 4f
                ), paint
        )
    }

    private fun drawHeader(c: Canvas, child: View, headerView: View) {
        c.save()
        if (recyclerViewAttr.isSticky) {
            val leftMargin = defaultOffset * 6
            Log.e("ㅇㅇㅇㅇ","$isSameTitle")
            if(!isSameTitle){
                if(child.left-leftMargin<0){
                    if(child.right - leftMargin < headerTitle!!.left + headerTitle!!.length() + headerTitle!!.textSize){
                        c.translate(
                                (child.right - leftMargin - (headerTitle!!.left + headerTitle!!.length() + headerTitle!!.textSize)) ,
                                0f
                        )
                    }else{
                        c.translate(
                                0f,
                                0f
                        )
                    }
                }
                else{
                    c.translate(
                            (child.left - leftMargin).toFloat(),
                            0f
                    )
                }
            }else{
                c.translate(
                        0f,
                        0f
                )
            }

        } else {
            val leftMargin = defaultOffset * 6
            c.translate(
                    (child.left - leftMargin).toFloat(),
                    0f
            )
        }
        headerView.draw(c)
        c.restore()
    }

    private fun getIsSection(position: Int): Boolean = when (position) {
        0 -> {
            true
        }
        -1 -> {
            false
        }
        else -> {
            sectionCallback.isSection(position)
        }

    }
}