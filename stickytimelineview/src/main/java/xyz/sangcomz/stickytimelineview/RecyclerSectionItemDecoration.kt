package xyz.sangcomz.stickytimelineview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import xyz.sangcomz.stickytimelineview.ext.DP
import xyz.sangcomz.stickytimelineview.model.RecyclerViewAttr
import xyz.sangcomz.stickytimelineview.model.SectionInfo

/*
 */

/**
 * Copyright 2017 Timothy Paetz
 * Copyright 2017 SeokWon Jeong
 *  thanks to @tim.paetz
 *  I was inspired by his code. And I used some of his code in the library.
 *  https://github.com/paetztm/recycler_view_headers
 */
class RecyclerSectionItemDecoration(
    context: Context,
    private val sectionCallback: SectionCallback,
    private val recyclerViewAttr: RecyclerViewAttr
) : RecyclerView.ItemDecoration() {

    private var headerView: View? = null
    private var headerBackground: View? = null
    private var headerTitle: TextView? = null
    private var headerSubTitle: TextView? = null
    private var defaultOffset: Int = 8.DP(context).toInt()
    private var headerOffset = defaultOffset * 8


    /**
     * Get the offset for each Item.
     * There is a difference in top offset between sections and not sections.
     */
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State?
    ) {
        super.getItemOffsets(
            outRect,
            view,
            parent,
            state
        )

        val pos = parent.getChildAdapterPosition(view)

        if (getIsSection(pos)) outRect.top = headerOffset
        else {
            outRect.top = defaultOffset / 2
        }

        val leftMargin = defaultOffset * 6
        val rightMargin = defaultOffset * 2

        outRect.bottom = defaultOffset / 2
        outRect.left = leftMargin
        outRect.right = rightMargin

    }

    /**
     * Draw any appropriate decorations into the Canvas supplied to the RecyclerView.
     * Any content drawn by this method will be drawn after the item views are drawn
     * and will thus appear over the views.
     *
     * @param c Canvas to draw into
     * @param parent RecyclerView this ItemDecoration is drawing into
     * @param state The current state of RecyclerView.
     */
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        super.onDrawOver(
            c,
            parent,
            state
        )
        var previousHeader = SectionInfo("", "")
        if (headerView == null) getHeaderView(parent)
        drawLine(c, parent)

        val childInContact = getChildInContact(parent, headerOffset * 2)
        val contractPosition = parent.getChildAdapterPosition(childInContact)

        if (getIsSection(contractPosition) && recyclerViewAttr.isSticky) {
            childInContact?.let {
                val topChild = parent.getChildAt(0) ?: return
                val topChildPosition = parent.getChildAdapterPosition(topChild)
                headerView?.let {
                    sectionCallback.getSectionHeader(topChildPosition)?.let { sectionInfo ->
                        previousHeader = sectionInfo
                        setHeaderView(sectionInfo)
                        val offset =
                            if (topChildPosition == 0
                                && childInContact.top - (headerOffset * 2) == (-1 * headerOffset)) 0f
                            else
                                (childInContact.top - (headerOffset * 2)).toFloat()

                        moveHeader(c, it, offset)
                    }
                }
            }
        }

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)

            sectionCallback.getSectionHeader(position)?.let { sectionInfo ->
                setHeaderView(sectionInfo)
                if (previousHeader != sectionInfo) {
                    headerView?.let {
                        drawHeader(c, child, it)
                        previousHeader = sectionInfo
                    }

                }
            }
        }
    }

    /**
     * First create a header view.
     */
    private fun getHeaderView(parent: RecyclerView) {
        headerView = inflateHeaderView(parent)
        headerView?.let { headerView ->
            headerBackground = headerView.findViewById(R.id.v_item_background)
            headerTitle = headerView.findViewById(R.id.list_item_section_title)
            headerSubTitle = headerView.findViewById(R.id.list_item_section_sub_title)
            val dot: ImageView = headerView.findViewById(R.id.dot)
            dot.background = getOvalDrawable()
            recyclerViewAttr.let { attrs ->
                headerBackground?.apply {
                    setBackgroundColor(attrs.sectionBackgroundColor)
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

    /**
     * Set a header view for section info.
     */
    private fun setHeaderView(sectionInfo: SectionInfo) {
        headerTitle?.text = sectionInfo.title
        headerSubTitle?.apply {
            sectionInfo.subTitle?.let {
                visibility = View.VISIBLE
                text = it
            } ?: kotlin.run {
                visibility = View.GONE
            }
        }

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
                defaultOffset * 3f,
                0f,
                defaultOffset * 3f,
                parent.height.toFloat()
            ), paint
        )
    }

    /**
     *
     */
    private fun getChildInContact(parent: RecyclerView, contactPoint: Int): View? =
        (0 until parent.childCount)
            .map {
                parent.getChildAt(it)
            }
            .firstOrNull {
                it.top in contactPoint / 2..contactPoint
            }

    /**
     * Returns the oval drawable of the timeline.
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
     * Moving parts when headers meet
     */
    private fun moveHeader(c: Canvas, topHeader: View, offset: Float) {
        if (!recyclerViewAttr.isSticky) return
        c.save()
        c.translate(0f, offset)
        topHeader.draw(c)
        c.restore()
    }

    /**
     * Draw a header
     */
    private fun drawHeader(c: Canvas, child: View, headerView: View) {
        c.save()
        if (recyclerViewAttr.isSticky) {
            c.translate(
                0f,
                Math.max(
                    0,
                    child.top - headerView.height
                ).toFloat()
            )
        } else {
            c.translate(
                0f,
                (child.top - headerView.height).toFloat()
            )
        }
        headerView.draw(c)
        c.restore()
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
            parent.paddingLeft + parent.paddingRight,
            view.layoutParams.width
        )
        val childHeight = ViewGroup.getChildMeasureSpec(
            heightSpec,
            parent.paddingTop + parent.paddingBottom,
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

    /**
     * To check if section is
     */
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


    /**
     * Section-specific callback interface
     */
    interface SectionCallback {
        /**
         * To check if section is
         */
        fun isSection(position: Int): Boolean

        /**
         * Functions that return a section header in a section
         */
        fun getSectionHeader(position: Int): SectionInfo?
    }
}


