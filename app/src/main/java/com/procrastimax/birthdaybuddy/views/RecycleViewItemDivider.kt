package com.procrastimax.birthdaybuddy.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
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

            //dont render a decoration when the child after this is a month divider/ not-decoration-view
            if (i < childCount - 1) {
                val child_following = parent.getChildAt(i + 1)
                if (!isDecorated(child_following, parent)) {
                    continue
                }
            }

            if (isDecorated(child, parent)) {
                val params = child.layoutParams as RecyclerView.LayoutParams

                val top = child.bottom + params.bottomMargin
                val bottom = top + mDivider.intrinsicHeight

                mDivider.setBounds(left, top, right, bottom)
                mDivider.draw(c)
            }
        }
    }

    /**
     * isDecorated check if the holder is a special type of holder and therefore should be decorated or not
     * @param view: View
     * @param parent: RecyclerView
     * @return Boolean
     */
    private fun isDecorated(view: View, parent: RecyclerView): Boolean {
        val holder: RecyclerView.ViewHolder = parent.getChildViewHolder(view)
        return ((holder is EventAdapter.BirthdayEventViewHolder) or (holder is EventAdapter.AnnualEventViewHolder) or (holder is EventAdapter.OneTimeEventViewHolder))
    }
}