package xyz.sangcomz.stickytimelineview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import xyz.sangcomz.stickytimelineview.ext.DP
import xyz.sangcomz.stickytimelineview.model.RecyclerViewAttr
import xyz.sangcomz.stickytimelineview.model.SectionInfo

class HorizontalSectionItemDecoration (context: Context,
                                       private val sectionCallback: RecyclerSectionItemDecoration.SectionCallback,
                                       private val recyclerViewAttr: RecyclerViewAttr) : RecyclerView.ItemDecoration() {

    private var headerView: View? = null
    private var headerBackground: View? = null
    private var headerTitle: TextView? = null
    private var headerSubTitle: TextView? = null
    private var dot: AppCompatImageView? = null
    private var defaultOffset: Int = 8.DP(context).toInt()
    private var headerOffset = defaultOffset * 8

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
            outRect.top = headerOffset
        }

        val leftMargin = defaultOffset * 6
        val rightMargin = defaultOffset * 2

        outRect.bottom = defaultOffset / 2
        outRect.left = leftMargin
        outRect.right = rightMargin

    }


    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_section_header,parent,false)
//        fixLayoutSize(view,parent)

        var previousHeader = SectionInfo("")
        if (headerView == null) getHeaderView(parent)


        val childInContact = getChildInContact(parent, headerOffset * 2)

        childInContact?.let {
            val contractPosition = parent.getChildAdapterPosition(childInContact)
            if (getIsSection(contractPosition) && recyclerViewAttr.isSticky) {
                val topChild = parent.getChildAt(0) ?: return
                val topChildPosition = parent.getChildAdapterPosition(topChild)
                headerView?.let {
                    sectionCallback.getSectionHeader(topChildPosition)?.let { sectionInfo ->
                        //previousHeader = sectionInfo
                        //setHeaderView(sectionInfo)
                        val offset =
                                if (topChildPosition == 0
                                        && childInContact.top - (headerOffset * 2) == (-1 * headerOffset)
                                ) 0f
                                else
                                    (childInContact.top - (headerOffset * 2)).toFloat()

                        //moveHeader(c, it, offset)
                    }
                }
            }
        }

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)
            sectionCallback.getSectionHeader(position)?.let { sectionInfo ->
                setHeaderView(sectionInfo)
                if (previousHeader.title != sectionInfo.title) {
                    headerView?.let {
                        drawHeader(c, child, it)
                        previousHeader = sectionInfo
                    }

                }
            }
        }

        c.save()
//        val child = parent.getChildAt(0)
//        val height = view.measuredHeight
//        val top = height-child.top
//        c.translate(0f,top.toFloat())
        //view.draw(c)
        c.restore()
    }

    private fun setHeaderView(sectionInfo: SectionInfo) {
        headerTitle?.text = sectionInfo.title
//        setHeaderSubTitle(sectionInfo.subTitle)
//        setDotDrawable(sectionInfo.dotDrawable)
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


    private fun getChildInContact(parent: RecyclerView, contactPoint: Int): View? =
            (0 until parent.childCount)
                    .map {
                        parent.getChildAt(it)
                    }
                    .firstOrNull {
                        it.top in contactPoint / 2..contactPoint
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