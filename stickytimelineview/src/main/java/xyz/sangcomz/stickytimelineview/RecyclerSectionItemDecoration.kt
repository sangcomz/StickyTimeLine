package xyz.sangcomz.stickytimelineview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
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


/**
 *  let me acknowledge @tim.paetz whose post inspired me to set off to a journey of implementing my own sticky headers using ItemDecorations.
 *  I borrowed some parts of his code in my implementation.
 *  https://github.com/paetztm/recycler_view_headers
 */
class RecyclerSectionItemDecoration(context: Context,
                                    private val sticky: Boolean,
                                    private val sectionCallback: SectionCallback,
                                    private val recyclerViewAttr: RecyclerViewAttr?) : RecyclerView.ItemDecoration() {

    private var headerView: View? = null
    private var headerTitle: TextView? = null
    private var headerSubTitle: TextView? = null
    private var defaultOffset: Int = 8.DP(context).toInt()
    private var headerOffset = defaultOffset * 8


    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        super.getItemOffsets(outRect,
                view,
                parent,
                state)

        val pos = parent.getChildAdapterPosition(view)

        if (getIsSection(pos)) {
            outRect.top = headerOffset
        }
    }


    override fun onDraw(c: Canvas?, parent: RecyclerView?, state: RecyclerView.State?) {
        super.onDraw(c, parent, state)
        c?.let {
            parent?.let {
                val leftMargin = defaultOffset * 7
                val rightMargin = defaultOffset * 2
                val topMargin = defaultOffset
                (0 until parent.childCount)
                        .map { parent.getChildAt(it) }
                        .forEach {
                            val params = it.layoutParams
                            if (params is RecyclerView.LayoutParams && params.leftMargin != leftMargin) {
                                params.setMargins(leftMargin, 0, rightMargin, topMargin)
                                it.layoutParams = params
                            }
                        }
            }
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        super.onDrawOver(c,
                parent,
                state)

        drawLine(c, parent)

        if (headerView == null) {
            headerView = inflateHeaderView(parent)
            headerView?.let { headerView ->
                headerTitle = headerView.findViewById(R.id.list_item_section_title)
                headerSubTitle = headerView.findViewById(R.id.list_item_section_sub_title)
                val dot: ImageView = headerView.findViewById(R.id.dot)
                dot.background = getOvalDrawable()
                recyclerViewAttr?.let { attrs ->
                    headerTitle?.apply {
                        setTextColor(attrs.sectionTitleTextColor)
                        setBackgroundColor(attrs.sectionBackgroundColor)
                        setTextSize(TypedValue.COMPLEX_UNIT_PX, attrs.sectionTitleTextSize)
                    }
                    headerSubTitle?.apply {
                        setTextColor(attrs.sectionSubTitleTextColor)
                        setBackgroundColor(attrs.sectionBackgroundColor)
                        setTextSize(TypedValue.COMPLEX_UNIT_PX, attrs.sectionSubTitleTextSize)
                    }
                }
                fixLayoutSize(headerView, parent)
            }
        }

        val childInContact = getChildInContact(parent, headerOffset * 2)
        val contractPosition = parent.getChildAdapterPosition(childInContact)
        var previousHeader = SectionInfo("", "")

        if (getIsSection(contractPosition)) {
            childInContact?.let {
                val topChild = parent.getChildAt(0) ?: return
                val topChildPosition = parent.getChildAdapterPosition(topChild)
                headerView?.let {
                    val sectionInfo = sectionCallback.getSectionHeader(topChildPosition)
                    previousHeader = sectionInfo
                    setHeaderView(sectionInfo)
                    val offset =
                            if (topChildPosition == 0) 0f
                            else (childInContact.top - (headerOffset * 2)).toFloat()
                    moveHeader(c, it, offset)
                }
            }
        }

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)
            val sectionInfo = sectionCallback.getSectionHeader(position)
            setHeaderView(sectionInfo)
            if (previousHeader != sectionInfo) {
                headerView?.let {
                    drawHeader(c, child, it)
                    previousHeader = sectionInfo
                }

            }
        }
    }

    private fun setHeaderView(sectionInfo: SectionInfo) {
        headerTitle?.text = sectionInfo.title
        headerSubTitle?.text = sectionInfo.subTitle
    }

    private fun drawLine(c: Canvas, parent: RecyclerView) {
        val paint = Paint()
        paint.color = recyclerViewAttr?.sectionLineColor ?: Color.BLACK
        paint.strokeWidth = defaultOffset.toFloat()
        c.drawLines(floatArrayOf(defaultOffset * 3f, 0f, defaultOffset * 3f, parent.height.toFloat()), paint)
    }

    private fun getChildInContact(parent: RecyclerView, contactPoint: Int): View? =
            (0 until parent.childCount)
                    .map {
                        parent.getChildAt(it)
                    }
                    .firstOrNull {
                        it.top in contactPoint / 2..contactPoint
                    }

    private fun getOvalDrawable(): Drawable {
        val strokeWidth = defaultOffset / 2
        val roundRadius = defaultOffset * 2
        val strokeColor = recyclerViewAttr?.sectionStrokeColor ?: Color.WHITE
        val fillColor = recyclerViewAttr?.sectionCircleColor ?: Color.BLACK

        val gd = GradientDrawable()
        gd.setColor(fillColor)
        gd.cornerRadius = roundRadius.toFloat()
        gd.setStroke(strokeWidth, strokeColor)

        return gd
    }

    private fun moveHeader(c: Canvas, topHeader: View, offset: Float) {
        c.save()
        c.translate(0f, offset)
        topHeader.draw(c)
        c.restore()
    }

    private fun drawHeader(c: Canvas, child: View, headerView: View) {
        c.save()
        if (sticky) {
            c.translate(0f,
                    Math.max(0,
                            child.top - headerView.height).toFloat())
        } else {
            c.translate(0f,
                    (child.top - headerView.height).toFloat())
        }
        headerView.draw(c)
        c.restore()
    }

    private fun inflateHeaderView(parent: RecyclerView): View {
        return LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_section_header,
                        parent,
                        false)
    }

    /**
     * Measures the headerTitle view to make sure its size is greater than 0 and will be drawn
     * https://yoda.entelect.co.za/view/9627/how-to-android-recyclerview-item-decorations
     */
    private fun fixLayoutSize(view: View, parent: ViewGroup) {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width,
                View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(parent.height,
                View.MeasureSpec.UNSPECIFIED)

        val childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
                parent.paddingLeft + parent.paddingRight,
                view.layoutParams.width)
        val childHeight = ViewGroup.getChildMeasureSpec(heightSpec,
                parent.paddingTop + parent.paddingBottom,
                view.layoutParams.height)

        view.measure(childWidth,
                childHeight)

        view.layout(0,
                0,
                view.measuredWidth,
                view.measuredHeight)
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

    interface SectionCallback {

        fun isSection(position: Int): Boolean

        fun getSectionHeader(position: Int): SectionInfo
    }
}


