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
import xyz.sangcomz.stickytimelineview.TimeLineRecyclerView.Companion.MODE_TO_DOT
import xyz.sangcomz.stickytimelineview.TimeLineRecyclerView.Companion.MODE_TO_TIME_LINE
import xyz.sangcomz.stickytimelineview.callback.SectionCallback
import xyz.sangcomz.stickytimelineview.ext.DP
import xyz.sangcomz.stickytimelineview.ext.shouldUseLayoutRtl
import xyz.sangcomz.stickytimelineview.model.RecyclerViewAttr
import xyz.sangcomz.stickytimelineview.model.SectionInfo
import kotlin.math.roundToInt

class HorizontalSectionItemDecoration(
    context: Context,
    private val sectionCallback: SectionCallback,
    private val recyclerViewAttr: RecyclerViewAttr
) : RecyclerView.ItemDecoration() {

    private val defaultOffset = 8.DP(context).toInt()
    private val sideOffset = 8.DP(context).toInt()
    private val bottomOffset = 4.DP(context).toInt()

    private var dotRadius: Int =
        (recyclerViewAttr.sectionDotSize + recyclerViewAttr.sectionDotStrokeSize).roundToInt()

    private val headerSectionBackgroundPaint = Paint().apply {
        isAntiAlias = true
        color = recyclerViewAttr.sectionBackgroundColor
    }

    private val linePaint = Paint().apply {
        isAntiAlias = true
        color = recyclerViewAttr.sectionLineColor
        strokeWidth = recyclerViewAttr.sectionLineWidth
    }

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

        val leftMargin = sideOffset
        val rightMargin = sideOffset

        outRect.bottom = bottomOffset
        outRect.left = leftMargin
        outRect.right = rightMargin
    }


    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)

        drawBackground(canvas, parent)
        drawLine(canvas, parent)

        var previousHeader: SectionInfo? = null

        if (recyclerViewAttr.isSticky) {
            val topChild = parent.getChildAt(0)
            val topHeaderSectionInfo = getCurrentTopSectionInfo(parent) ?: return
            val currentHeaderWidth = getHeaderViewWidth(topHeaderSectionInfo)
            val nextHeaderView = getNextHeaderView(parent)

            val isContact = isContact(currentHeaderWidth, nextHeaderView, parent)
            val startOffset = getStartOffset(topChild, parent)

            previousHeader = topHeaderSectionInfo

            val offset = getHeaderDrawOffset(currentHeaderWidth, nextHeaderView, parent)

            drawHeader(
                canvas,
                parent,
                topChild,
                topHeaderSectionInfo,
                if (isContact) startOffset - offset else startOffset
            )
        }

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)

            sectionCallback.getSectionHeader(position)?.let { sectionInfo ->
                if (previousHeader?.title != sectionInfo.title) {
                    if (getIsSection(position)) {
                        drawHeader(canvas, parent, child, sectionInfo)
                    }
                    previousHeader = sectionInfo
                }
            }
        }
    }

    /**
     * Draw a line in the timeline.
     */
    private fun drawLine(canvas: Canvas, parent: RecyclerView) {
        val yValue = getTopSpace() - (defaultOffset * 2) - (defaultOffset / 4)

        canvas.drawLines(floatArrayOf(0f, yValue, parent.width.toFloat(), yValue), linePaint)
    }

    private fun drawBackground(canvas: Canvas, parent: RecyclerView) {
        var bottom = getTopSpace() - sideOffset

        when (recyclerViewAttr.sectionBackgroundColorMode) {
            MODE_TO_DOT -> {
                bottom -= (defaultOffset * 2 + defaultOffset / 2)
            }
            MODE_TO_TIME_LINE -> {
                bottom -= if (recyclerViewAttr.sectionLineWidth > defaultOffset * 2 + defaultOffset / 2) {
                    recyclerViewAttr.sectionLineWidth / 2
                } else {
                    (defaultOffset + defaultOffset / 4) - (recyclerViewAttr.sectionLineWidth / 2)
                }
            }
        }

        val rect = Rect(parent.left, 0, parent.width, bottom.toInt())

        canvas.drawRect(rect, headerSectionBackgroundPaint)
    }

    /**
     * Returns the oval dotDrawable of the timeline.
     */
    private fun getOvalDrawable(): Drawable {
        val strokeWidth = recyclerViewAttr.sectionDotStrokeSize.toInt()
        val strokeColor = recyclerViewAttr.sectionDotStrokeColor
        val fillColor = recyclerViewAttr.sectionDotColor

        val gd = GradientDrawable()
        gd.shape = GradientDrawable.OVAL
        gd.setColor(fillColor)
        gd.cornerRadius = dotRadius * 2f
        gd.setStroke(strokeWidth, strokeColor)

        return gd
    }

    /**
     * Draw a header
     */
    private fun drawHeader(
        c: Canvas,
        parent: RecyclerView,
        child: View,
        sectionInfo: SectionInfo,
        offset: Float = 0f
    ) {
        c.save()
        c.translate(getHeaderTranslate(parent, child, offset), 0f)
        drawDotDrawable(c, parent, sectionInfo)
        drawHeaderTitle(c, parent, sectionInfo)
        drawHeaderSubTitle(c, parent, sectionInfo)
        c.restore()
    }

    private fun drawDotDrawable(canvas: Canvas, parent: RecyclerView, sectionInfo: SectionInfo) {

        val dx = getDotTranslate(parent, dotRadius)

        val dotDrawable =
            sectionInfo.dotDrawable ?: recyclerViewAttr.customDotDrawable ?: getOvalDrawable()
        canvas.save()
        canvas.translate(
            dx,
            getTopSpace() - (defaultOffset * 2) - (defaultOffset / 4) - dotRadius
        )
        dotDrawable.setBounds(0, 0, dotRadius * 2, dotRadius * 2)
        dotDrawable.draw(canvas)
        canvas.restore()
    }

    private fun drawHeaderTitle(canvas: Canvas, parent: RecyclerView, sectionInfo: SectionInfo) {

        val x = getTitleTranslate(parent, sectionInfo.title)

        val subTitleHeight =
            if (sectionInfo.subTitle.isNullOrEmpty()) recyclerViewAttr.sectionSubTitleTextSize else 0f
        canvas.drawText(
            sectionInfo.title,
            x,
            recyclerViewAttr.sectionTitleTextSize - subTitleHeight,
            headerTitlePaint
        )
    }

    private fun drawHeaderSubTitle(canvas: Canvas, parent: RecyclerView, sectionInfo: SectionInfo) {
        val subTitle = sectionInfo.subTitle ?: return

        val x = getSubTitleTranslate(parent, sectionInfo.subTitle)

        canvas.drawText(
            subTitle,
            x,
            recyclerViewAttr.sectionTitleTextSize + recyclerViewAttr.sectionSubTitleTextSize,
            headerSubTitlePaint
        )
    }

    private fun getHeaderViewWidth(sectionInfo: SectionInfo): Float {
        val titleWidth = headerTitlePaint.measureText(sectionInfo.title)
        val subTitleWidth = headerSubTitlePaint.measureText(sectionInfo.subTitle ?: "")
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

    private fun isContact(
        currentHeaderWidth: Float,
        nextHeaderView: View?,
        parent: View
    ): Boolean {
        return if (parent.shouldUseLayoutRtl()) {
            nextHeaderView?.right in parent.width - currentHeaderWidth.toInt() - defaultOffset * 2..parent.width
        } else {
            nextHeaderView?.left in 0..currentHeaderWidth.toInt() + defaultOffset * 2
        }
    }

    private fun getStartOffset(topChild: View, parent: View): Float {
        return if (parent.shouldUseLayoutRtl()) {
            -(parent.width - topChild.right.toFloat() - sideOffset)
        } else {
            -(topChild.left).toFloat() + sideOffset
        }
    }

    private fun getHeaderDrawOffset(
        currentHeaderWidth: Float,
        nextHeaderView: View?,
        parent: View
    ): Float {
        return if (parent.shouldUseLayoutRtl()) {
            currentHeaderWidth - ((parent.width - (nextHeaderView?.right
                ?: 0)) - defaultOffset * 2)
        } else {
            currentHeaderWidth - ((nextHeaderView?.left ?: 0) - defaultOffset * 2)
        }
    }

    private fun getHeaderTranslate(parent: View, child: View, offset: Float): Float {
        return if (parent.shouldUseLayoutRtl()) {
            child.right.toFloat() - offset - sideOffset
        } else {
            child.left.toFloat() + offset
        }
    }

    private fun getDotTranslate(parent: View, radius: Int): Float {
        return if (parent.shouldUseLayoutRtl()) {
            (-radius).toFloat()
        } else {
            0f
        }
    }

    private fun getTitleTranslate(parent: View, title: String): Float {
        return if (parent.shouldUseLayoutRtl()) {
            -headerTitlePaint.measureText(title) + defaultOffset
        } else {
            0f
        }
    }

    private fun getSubTitleTranslate(parent: View, subtitle: String): Float {
        return if (parent.shouldUseLayoutRtl()) {
            -headerSubTitlePaint.measureText(subtitle) + defaultOffset
        } else {
            0f
        }
    }

    companion object {
        private const val FONT_FAMILY = "sans-serif-light"
    }
}