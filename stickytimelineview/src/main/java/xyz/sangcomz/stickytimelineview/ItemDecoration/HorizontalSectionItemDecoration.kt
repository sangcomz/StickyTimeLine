package xyz.sangcomz.stickytimelineview.ItemDecoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import xyz.sangcomz.stickytimelineview.R
import xyz.sangcomz.stickytimelineview.ext.DP
import xyz.sangcomz.stickytimelineview.model.RecyclerViewAttr
import xyz.sangcomz.stickytimelineview.model.SectionInfo

class HorizontalSectionItemDecoration(context: Context,
                                      private val sectionCallback: VerticalSectionItemDecoration.SectionCallback,
                                      private val recyclerViewAttr: RecyclerViewAttr) : RecyclerView.ItemDecoration() {
    private lateinit var headerView: View
    private lateinit var headerBackground: LinearLayout
    private lateinit var headerTitle: TextView
    private lateinit var headerSubTitle: TextView
    private lateinit var dot: AppCompatImageView
    private lateinit var previousHeader: SectionInfo

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
        if (!::headerView.isInitialized) getHeaderView(parent)
        drawLine(c, parent)

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)

            if (!recyclerViewAttr.isSticky) {
                if (getIsSection(position)) {
                    sectionCallback.getSectionHeader(position)?.let { sectionInfo ->
                        setHeaderView(sectionInfo)
                        drawHeader(c, child, headerView)
                        previousHeader = sectionInfo
                    }
                }
            } else {
                sectionCallback.getSectionHeader(position)?.let { sectionInfo ->
                    if (sectionInfo.title == previousHeader.title) {
                        isSameTitle = true
                        setHeaderView(sectionInfo)
                        drawHeader(c, child, headerView)
                        previousHeader = sectionInfo
                    } else {
                        isSameTitle = false
                        setHeaderView(sectionInfo)
                        drawHeader(c, child, headerView)
                        previousHeader = sectionInfo
                    }
                }
            }
        }
    }

    /**
     * Set a header view for section info.
     */
    private fun setHeaderView(sectionInfo: SectionInfo) {
        headerTitle.text = sectionInfo.title
        setHeaderSubTitle(sectionInfo.subTitle)
        setDotDrawable(sectionInfo.dotDrawable)
    }

    private fun setHeaderSubTitle(sectionSubTitle: String?) {
        headerSubTitle.apply {
            sectionSubTitle?.let {
                visibility = View.VISIBLE
                text = it
            } ?: kotlin.run {
                visibility = View.GONE
            }
        }

    }

    private fun setDotDrawable(sectionDotDrawable: Drawable?) {
        val dotDrawable = sectionDotDrawable ?: recyclerViewAttr.customDotDrawable
        dot.background = dotDrawable ?: getOvalDrawable()
    }

    /**
     * Draw a line in the timeline.
     */
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

    /**
     * Draw a header & Moving a header
     */
    private fun drawHeader(c: Canvas, child: View, headerView: View) {
        c.save()
        val leftMargin = defaultOffset * 6
        if (recyclerViewAttr.isSticky) {
            val headerTitleWidth = headerTitle.left + headerTitle.length() * headerTitle.textSize
            if (!isSameTitle) {
                if (child.left - leftMargin < 0) {
                    if (child.right + leftMargin < headerTitleWidth)
                        c.translate((child.right + leftMargin - headerTitleWidth), 0f)
                    else
                        c.translate(0f, 0f)
                } else
                    c.translate((child.left - leftMargin).toFloat(), 0f)
            } else c.translate(0f, 0f)
        } else
            c.translate((child.left - leftMargin).toFloat(), 0f)
        headerView.draw(c)
        c.restore()
    }

    private fun getHeaderView(parent: RecyclerView) {
        headerView = inflateHeaderView(parent)
        headerView.apply {
            headerBackground = findViewById(R.id.v_item_background)
            headerTitle = findViewById(R.id.list_item_section_title)
            headerSubTitle = findViewById(R.id.list_item_section_sub_title)
            dot = findViewById(R.id.dot)
            recyclerViewAttr.let { attrs ->
                headerBackground.apply {
                    setBackgroundColor(attrs.sectionBackgroundColor)
                    val titleTransparentLine = TextView(context)
                    val titleBackgroundLine = TextView(context)
                    titleTransparentLine.setBackgroundColor(attrs.sectionLineColor)
                    val width = (headerTitle.textSize * headerTitle.length().toFloat()).toInt()
                    addView(titleBackgroundLine, width, headerTitle.measuredHeight)
                    addView(titleTransparentLine, ViewGroup.LayoutParams.MATCH_PARENT, attrs.sectionLineWidth.toInt())
                }
                headerTitle.apply {
                    setPadding(defaultOffset / 2, 0, defaultOffset / 2, 0)
                    setTextColor(attrs.sectionTitleTextColor)
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, attrs.sectionTitleTextSize)
                }
                headerSubTitle.apply {
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

    /**
     * Measures the headerTitle view to make sure its size is greater than 0 and will be drawn
     * https://yoda.entelect.co.za/view/9627/how-to-android-recyclerview-item-decorations
     */
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