package com.procrastimax.birthdaybuddy.views

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.procrastimax.birthdaybuddy.R
import kotlinx.android.synthetic.main.card_view_help.view.*

class HelpAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class HelpInstance {
        EventBirthday,
        AnnualEvent,
        OneTimeEvent,
        Reason,
        Support,
        Notifications
    }

    class AboutCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    val itemList = listOf(
        HelpInstance.Reason,
        HelpInstance.EventBirthday,
        HelpInstance.AnnualEvent,
        HelpInstance.OneTimeEvent,
        HelpInstance.Notifications,
        HelpInstance.Support
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewtype: Int): RecyclerView.ViewHolder {
        val cardView = LayoutInflater.from(parent.context).inflate(R.layout.card_view_help, parent, false)
        return AboutCardViewHolder(cardView)
    }

    override fun getItemCount(): Int {
        return this.itemList.size
    }

    override fun getItemViewType(position: Int): Int {
        return this.itemList[position].ordinal
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (itemList[position]) {
            HelpInstance.Reason -> {
                holder.itemView.tv_card_view_help_title.text = context.resources.getText(R.string.help_title_reason)
                holder.itemView.tv_card_view_help_content.text = context.resources.getText(R.string.help_content_reason)
            }
            HelpInstance.EventBirthday -> {
                holder.itemView.tv_card_view_help_title.text = context.resources.getText(R.string.help_title_Birthday)
                holder.itemView.tv_card_view_help_content.text =
                    context.resources.getText(R.string.help_content_Birthday)
            }
            HelpInstance.AnnualEvent -> {
                holder.itemView.tv_card_view_help_title.text = context.resources.getText(R.string.help_title_Annual)
                holder.itemView.tv_card_view_help_content.text = context.resources.getText(R.string.help_content_Annual)

            }
            HelpInstance.OneTimeEvent -> {
                holder.itemView.tv_card_view_help_title.text = context.resources.getText(R.string.help_title_OneTime)
                holder.itemView.tv_card_view_help_content.text =
                    context.resources.getText(R.string.help_content_OneTime)
            }
            HelpInstance.Support -> {
                holder.itemView.tv_card_view_help_title.text = context.resources.getText(R.string.help_title_support)
                holder.itemView.tv_card_view_help_content.text =
                    context.resources.getText(R.string.help_content_support)
            }
            HelpInstance.Notifications -> {
                holder.itemView.tv_card_view_help_title.text =
                    context.resources.getText(R.string.help_title_notifications)
                holder.itemView.tv_card_view_help_content.text =
                    context.resources.getText(R.string.help_content_notifications)
            }
        }
    }
}