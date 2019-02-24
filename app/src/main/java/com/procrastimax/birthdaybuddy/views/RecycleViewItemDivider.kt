package com.procrastimax.birthdaybuddy.views

import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.graphics.drawable.Drawable

import android.content.Context
import android.support.v4.content.ContextCompat
import com.procrastimax.birthdaybuddy.MainActivity
import com.procrastimax.birthdaybuddy.R


class RecycleViewItemDivider(private val context: Context) : RecyclerView.ItemDecoration() {
    private var mDivider: Drawable = ContextCompat.getDrawable(context, R.drawable.horizontal_divider)!!

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = MainActivity.convertPxToDp(context, 64f).toInt()
        val right = parent.width - parent.paddingRight - MainActivity.convertPxToDp(context, 16f).toInt()

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)

            val params = child.layoutParams as RecyclerView.LayoutParams

            val top = child.bottom + params.bottomMargin
            val bottom = top + mDivider.intrinsicHeight

            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }
}