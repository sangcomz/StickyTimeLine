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
import xyz.sangcomz.stickytimelineview.ext.shouldUseLayoutRtl
import xyz.sangcomz.stickytimelineview.model.RecyclerViewAttr
import xyz.sangcomz.stickytimelineview.model.SectionInfo
import kotlin.math.roundToInt

/**
 * Copyright 2017 Timothy Paetz
 * Copyright 2017 SeokWon Jeong
 *  thanks to @tim.paetz
 *  I was inspired by his code. And I used some of his code in the library.
 *  https://github.com/paetztm/recycler_view_headers
 */
class VerticalSectionItemDecoration(
    context: Context,
    private val sectionCallback: SectionCallback,
    private val recyclerViewAttr: RecyclerViewAttr
) : RecyclerView.ItemDecoration() {

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

    private var defaultOffset: Int = 8.DP(context).toInt()
    private var sectionHeight: Int = 64.DP(context).toInt()
    private var dotRadius: Int =
        (recyclerViewAttr.sectionDotSize + recyclerViewAttr.sectionDotStrokeSize).roundToInt()
    private var headerOffset = defaultOffset * 8


    /**
     * Get the offset for each Item.
     * There is a difference in top offset between sections and not sections.
     */
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

        val pos = parent.getChildAdapterPosition(view)

        if (getIsSection(pos)) {
            outRect.top = headerOffset
        } else {
            outRect.top = defaultOffset / 2
        }

        val isLayoutRtl = parent.shouldUseLayoutRtl()

        val leftMargin = if (isLayoutRtl) defaultOffset * 2 else defaultOffset * 6
        val rightMargin = if (isLayoutRtl) defaultOffset * 6 else defaultOffset * 2

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
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(
            c,
            parent,
            state
        )
        var previousHeader = SectionInfo("")
        drawAllLine(c, parent)

        val childInContact = getChildInContact(parent, headerOffset * 2)

        childInContact?.let {
            val contractPosition = parent.getChildAdapterPosition(childInContact)
            if (getIsSection(contractPosition) && recyclerViewAttr.isSticky) {
                val topChild = parent.getChildAt(0) ?: return
                val topChildPosition = parent.getChildAdapterPosition(topChild)
                sectionCallback.getSectionHeader(topChildPosition)?.let { sectionInfo ->
                    previousHeader = sectionInfo
                    val offset =
                        if (topChildPosition == 0
                            && childInContact.top - (headerOffset * 2) == (-1 * headerOffset)
                        ) 0f
                        else
                            (childInContact.top - (headerOffset * 2)).toFloat()

                    drawHeader(c, parent, it, sectionInfo, offset)
                }
            }
        }

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)
            sectionCallback.getSectionHeader(position)?.let { sectionInfo ->
                if (previousHeader.title != sectionInfo.title) {
                    drawHeader(c, parent, child, sectionInfo)
                    previousHeader = sectionInfo
                }
            }
        }
    }

    /**
     * Draw a line in the timeline.
     */
    private fun drawAllLine(c: Canvas, parent: RecyclerView) {
        val offset = getLineOffset(parent)

        c.drawLines(
            floatArrayOf(
                offset,
                0f,
                offset,
                parent.height.toFloat()
            ), linePaint
        )
    }

    /**
     * Draw a line in the timeline.
     */
    private fun drawLine(c: Canvas, parent: RecyclerView) {
        val offset = getLineOffset(parent)

        c.drawLines(
            floatArrayOf(
                offset,
                0f,
                offset,
                sectionHeight.toFloat()
            ), linePaint
        )
    }

    private fun getChildInContact(parent: RecyclerView, contactPoint: Int): View? =
        (0 until parent.childCount)
            .map {
                parent.getChildAt(it)
            }
            .firstOrNull {
                it.top in contactPoint / 2..contactPoint
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
        gd.cornerRadius = dotRadius * 2.toFloat()
        gd.setStroke(strokeWidth, strokeColor)

        return gd
    }

    /**
     * Draw a header
     */
    private fun drawHeader(
        canvas: Canvas,
        parent: RecyclerView,
        child: View,
        sectionInfo: SectionInfo,
        offset: Float = 0f
    ) {
        canvas.save()
        if (recyclerViewAttr.isSticky) {
            if (offset != 0f) {
                canvas.translate(0f, offset)
            } else {
                canvas.translate(0f, 0.coerceAtLeast(child.top - sectionHeight).toFloat())
            }
        } else {
            canvas.translate(0f, (child.top - sectionHeight).toFloat())
        }

        drawBackground(canvas, parent)
        drawLine(canvas, parent)
        drawDotDrawable(canvas, parent, sectionInfo)
        drawHeaderTitle(canvas, parent, sectionInfo)
        drawHeaderSubTitle(canvas, parent, sectionInfo)
        canvas.restore()
    }

    private fun drawBackground(canvas: Canvas, parent: RecyclerView) {
        val rect = Rect(0, 0, parent.width, sectionHeight)
        canvas.drawRect(rect, headerSectionBackgroundPaint)
    }

    private fun drawDotDrawable(canvas: Canvas, parent: RecyclerView, sectionInfo: SectionInfo) {
        val dotDrawable =
            sectionInfo.dotDrawable ?: recyclerViewAttr.customDotDrawable ?: getOvalDrawable()
        canvas.save()
        canvas.translate(
            getDotTranslateX(parent, dotRadius),
            (sectionHeight / 2).toFloat() - dotRadius
        )
        dotDrawable.setBounds(0, 0, dotRadius * 2, dotRadius * 2)
        dotDrawable.draw(canvas)
        canvas.restore()
    }

    private fun drawHeaderTitle(canvas: Canvas, parent: RecyclerView, sectionInfo: SectionInfo) {
        canvas.drawText(
            sectionInfo.title,
            getTitleTranslateX(parent, sectionInfo.title),
            (sectionHeight / 2) + (recyclerViewAttr.sectionTitleTextSize / 4),
            headerTitlePaint
        )
    }

    private fun drawHeaderSubTitle(canvas: Canvas, parent: RecyclerView, sectionInfo: SectionInfo) {
        val subTitle = sectionInfo.subTitle ?: return

        canvas.drawText(
            subTitle,
            getSubTitleTranslateX(parent, sectionInfo.subTitle),
            (sectionHeight / 2) + (recyclerViewAttr.sectionTitleTextSize) + (recyclerViewAttr.sectionSubTitleTextSize / 4),
            headerSubTitlePaint
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

    private fun getLineOffset(parent: View): Float {
        return if (parent.shouldUseLayoutRtl()) {
            parent.width - defaultOffset * 3f
        } else {
            defaultOffset * 3f
        }
    }

    private fun getDotTranslateX(parent: View, radius: Int): Float {
        return if (parent.shouldUseLayoutRtl()) {
            parent.width - ((defaultOffset * 3f) + radius)
        } else {
            (defaultOffset * 3f) - radius
        }
    }

    private fun getTitleTranslateX(parent: View, title: String): Float {
        return if (parent.shouldUseLayoutRtl()) {
            parent.width - (defaultOffset * 6).toFloat() - headerTitlePaint.measureText(title)
        } else {
            (defaultOffset * 6).toFloat()
        }
    }

    private fun getSubTitleTranslateX(parent: View, subtitle: String): Float {
        return if (parent.shouldUseLayoutRtl()) {
            parent.width - (defaultOffset * 6).toFloat() - headerSubTitlePaint.measureText(subtitle)
        } else {
            (defaultOffset * 6).toFloat()
        }
    }

    companion object {
        private const val FONT_FAMILY = "sans-serif-light"
    }
}


