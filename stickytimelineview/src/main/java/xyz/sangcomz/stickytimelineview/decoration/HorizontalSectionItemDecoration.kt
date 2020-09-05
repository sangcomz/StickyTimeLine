package xyz.sangcomz.stickytimelineview.decoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import xyz.sangcomz.stickytimelineview.callback.SectionCallback
import xyz.sangcomz.stickytimelineview.ext.DP
import xyz.sangcomz.stickytimelineview.model.RecyclerViewAttr
import xyz.sangcomz.stickytimelineview.model.SectionInfo

class HorizontalSectionItemDecoration(
    context: Context,
    private val sectionCallback: SectionCallback,
    private val recyclerViewAttr: RecyclerViewAttr
) : RecyclerView.ItemDecoration() {

    private var defaultOffset: Int = 8.DP(context).toInt()

    private val headerTitlePaint = Paint().apply {
        isAntiAlias = true
        textSize = recyclerViewAttr.sectionTitleTextSize
        color = recyclerViewAttr.sectionTitleTextColor
        typeface = Typeface.create(FONT_FAMILY, Typeface.BOLD)
    }

    private val headerSubTitlePaint = Paint().apply {
        isAntiAlias = true
        textSize = recyclerViewAttr.sectionSubTitleTextSize
        color = recyclerViewAttr.sectionSubTitleTextColor
        typeface = Typeface.create(FONT_FAMILY, Typeface.NORMAL)
    }

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
        outRect.top = getTopSpace().toInt()

        val leftMargin = defaultOffset
        val rightMargin = defaultOffset

        outRect.bottom = defaultOffset / 2
        outRect.left = leftMargin
        outRect.right = rightMargin
    }


    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        drawLine(c, parent)
        var previousHeader: SectionInfo? = null

        if (recyclerViewAttr.isSticky) {
            val topChild = parent.getChildAt(0)
            val topHeaderSectionInfo = getCurrentTopSectionInfo(parent) ?: return

            val currentHeaderWidth = getCurrentHeaderViewWidth(parent)
            val nextHeaderView = getNextHeaderView(parent)

            val isContact =
                nextHeaderView?.left in 0..currentHeaderWidth.toInt() + defaultOffset * 2
            val defaultLeftOffset = -(topChild.left).toFloat() + defaultOffset

            previousHeader = topHeaderSectionInfo

            if (isContact) {
                val offset = currentHeaderWidth - ((nextHeaderView?.left ?: 0) - defaultOffset * 2)

                drawHeader(
                    c,
                    topChild,
                    topHeaderSectionInfo,
                    defaultLeftOffset - offset
                )
            } else {
                drawHeader(
                    c,
                    topChild,
                    topHeaderSectionInfo,
                    defaultLeftOffset
                )
            }
        }

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)

            sectionCallback.getSectionHeader(position)?.let { sectionInfo ->
                if (previousHeader?.title != sectionInfo.title) {
                    if (getIsSection(position)) {
                        drawHeader(c, child, sectionInfo)
                    }
                    previousHeader = sectionInfo
                }
            }
        }
    }

    /**
     * Draw a line in the timeline.
     */
    private fun drawLine(c: Canvas, parent: RecyclerView) {
        val paint = Paint().apply {
            isAntiAlias = true
            color = recyclerViewAttr.sectionLineColor
            strokeWidth = recyclerViewAttr.sectionLineWidth
        }

        val yValue = getTopSpace() - (defaultOffset * 2) - (defaultOffset / 4)

        c.drawLines(floatArrayOf(0f, yValue, parent.width.toFloat(), yValue), paint)
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
     * Draw a header
     */
    private fun drawHeader(c: Canvas, child: View, sectionInfo: SectionInfo, offset: Float = 0f) {
        c.save()
        c.translate((child.left).toFloat() + offset, 0f)
        drawDotDrawable(c, sectionInfo)
        drawHeaderTitle(c, sectionInfo)
        drawHeaderSubTitle(c, sectionInfo)
        c.restore()
    }

    private fun drawDotDrawable(canvas: Canvas, sectionInfo: SectionInfo) {
        val dotDrawable =
            sectionInfo.dotDrawable ?: recyclerViewAttr.customDotDrawable ?: getOvalDrawable()
        canvas.save()
        canvas.translate(
            0f,
            recyclerViewAttr.sectionTitleTextSize + recyclerViewAttr.sectionSubTitleTextSize + defaultOffset
        )
        dotDrawable.draw(canvas)
        canvas.restore()
    }

    private fun drawHeaderTitle(canvas: Canvas, sectionInfo: SectionInfo) {
        val subTitleHeight =
            if (sectionInfo.subTitle.isNullOrEmpty()) recyclerViewAttr.sectionSubTitleTextSize else 0f
        canvas.drawText(
            sectionInfo.title,
            0f,
            recyclerViewAttr.sectionTitleTextSize - subTitleHeight, headerTitlePaint
        )
    }

    private fun drawHeaderSubTitle(canvas: Canvas, sectionInfo: SectionInfo) {
        val subTitle = sectionInfo.subTitle ?: return
        canvas.drawText(
            subTitle,
            0f,
            recyclerViewAttr.sectionTitleTextSize + recyclerViewAttr.sectionSubTitleTextSize,
            headerSubTitlePaint
        )
    }

    private fun getCurrentHeaderViewWidth(parent: RecyclerView): Float {
        val prevHeaderSectionInfo = getCurrentTopSectionInfo(parent) ?: return 0f

        val titleWidth = headerTitlePaint.measureText(prevHeaderSectionInfo.title)
        val subTitleWidth = headerSubTitlePaint.measureText(prevHeaderSectionInfo.subTitle ?: "")
        return titleWidth.coerceAtLeast(subTitleWidth)
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

    private fun getTopSpace(): Float {
        return recyclerViewAttr.sectionTitleTextSize +
                recyclerViewAttr.sectionSubTitleTextSize +
                (defaultOffset * 4) +
                (defaultOffset / 2)
    }

    private fun getCurrentTopSectionInfo(parent: RecyclerView): SectionInfo? {
        return parent.getChildAt(0)
            ?.let { sectionCallback.getSectionHeader(parent.getChildAdapterPosition(it)) }
    }

    private fun getNextHeaderView(parent: RecyclerView): View? {
        val currentTopSectionInfo = getCurrentTopSectionInfo(parent)

        return (0 until parent.childCount)
            .map {
                parent.getChildAt(it)
            }
            .firstOrNull {
                sectionCallback.getSectionHeader(parent.getChildAdapterPosition(it)) != currentTopSectionInfo
            }
    }

    companion object {
        private const val FONT_FAMILY = "sans-serif-light"
    }
}