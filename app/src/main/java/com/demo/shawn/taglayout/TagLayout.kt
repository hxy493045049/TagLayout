package com.demo.shawn.taglayout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import java.util.ArrayList

class TagLayout(context: Context, attrs: AttributeSet) : ViewGroup(context, attrs) {
    //记录每一行有多高
    internal var lineHeights: MutableList<Int> = ArrayList()
    internal var views: MutableList<List<View>> = ArrayList()

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        views.clear()
        lineHeights.clear()
        //1.计算
        //该行有多少列数据
        var lineViews: MutableList<View> = ArrayList()
        val width = measuredWidth//容器自己的宽度
        var lineWidth = 0
        var lineHeight = 0//这一行的最大高度
        for (j in 0 until childCount) {
            val child = getChildAt(j)
            val lp = child.layoutParams as ViewGroup.MarginLayoutParams
            val childWidth = child.measuredWidth
            val childHeigh = child.measuredHeight
            if (childWidth + lp.leftMargin + lp.rightMargin + lineWidth > width) {
                //超出,换行
                lineHeights.add(lineHeight)
                views.add(lineViews)
                lineWidth = 0
                lineViews = ArrayList()
            }
            lineWidth += childWidth + lp.leftMargin + lp.rightMargin
            lineHeight = Math.max(lineHeight, childHeigh + lp.topMargin + lp.bottomMargin)
            lineViews.add(child)
        }
        lineHeights.add(lineHeight)
        views.add(lineViews)
        var left = 0
        var top = 0
        //2.摆放
        val size = views.size
        for (i in 0 until size) {
            lineViews = views[i] as MutableList<View>
            lineHeight = lineHeights[i]
            for (j in lineViews.indices) {
                //遍历这一行的所有child
                val child = lineViews[j]
                val lp = child.layoutParams as ViewGroup.MarginLayoutParams
                val lc = left + lp.leftMargin
                val tc = top + lp.topMargin
                val rc = lc + child.measuredWidth
                val bc = tc + child.measuredHeight
                child.layout(lc, tc, rc, bc)
                left += child.measuredWidth + lp.leftMargin + lp.rightMargin
            }
            left = 0
            top += lineHeight
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val sizeWidth = View.MeasureSpec.getSize(widthMeasureSpec)
        val sizeHeight = View.MeasureSpec.getSize(heightMeasureSpec)
        val modeWidth = View.MeasureSpec.getMode(widthMeasureSpec)
        val modeHeight = View.MeasureSpec.getMode(heightMeasureSpec)

        var width = 0//width=所有行里面最宽的一行
        var height = 0//height=所有行的高度相加
        //一行的宽度=一行当中的所有view的宽度的和
        var lineWidth = 0
        var lineHeight = 0

        //1.测量所有子控件的大小
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            val lp = child.layoutParams as ViewGroup.MarginLayoutParams
            //子控件真实占用的宽和高度
            val childWidth = child.measuredWidth + lp.leftMargin + lp.rightMargin
            val childHeight = child.measuredHeight + lp.topMargin + lp.bottomMargin
            //当一行放不下的时候需要换行
            if (lineWidth + childWidth > sizeWidth) {
                //换行
                width = Math.max(lineWidth, width)
                lineWidth = childWidth

                height += lineHeight
                lineHeight = childHeight
            } else {//累加
                lineWidth += childWidth
                lineHeight = Math.max(lineHeight, childHeight)
            }
            //最后一步
            if (i == childCount - 1) {
                width = Math.max(width, lineWidth)
                height += lineHeight
            }
        }

        //2.测量并定义自身的大小
        val measuredWidth = if (modeWidth == View.MeasureSpec.EXACTLY) sizeWidth else width//wrap_content/match_parent/EXACTLY
        val measuredHeight = if (modeHeight == View.MeasureSpec.EXACTLY) sizeHeight else height//wrap_content/match_parent/EXACTLY
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): ViewGroup.LayoutParams {
        return ViewGroup.MarginLayoutParams(p)
    }

    override fun generateLayoutParams(attrs: AttributeSet): ViewGroup.LayoutParams {
        return ViewGroup.MarginLayoutParams(context, attrs)
    }

}