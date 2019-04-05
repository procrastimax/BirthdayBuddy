package com.procrastimax.birthdaybuddy.views

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.procrastimax.birthdaybuddy.R

class HelpAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class HelpInstance(val value: Int) {
        EventBirthday(0),
        AnnualEvent(1),
        OneTimeEvent(2)
    }

    class AboutCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    val itemList = listOf<HelpInstance>(
        HelpInstance.EventBirthday,
        HelpInstance.AnnualEvent,
        HelpInstance.OneTimeEvent
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewtype: Int): RecyclerView.ViewHolder {
        val cardView = LayoutInflater.from(parent.context).inflate(R.layout.card_view_help, parent, false)
        return AboutCardViewHolder(cardView)
    }

    override fun getItemCount(): Int {
        return this.itemList.size
    }

    override fun getItemViewType(position: Int): Int {
        return this.itemList[position].value
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (position) {
            0 -> {

            }
            1 -> {

            }
            2 -> {

            }
        }
    }
}