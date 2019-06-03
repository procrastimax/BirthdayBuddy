package com.procrastimax.birthdaybuddy.views

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.procrastimax.birthdaybuddy.BuildConfig
import com.procrastimax.birthdaybuddy.R
import kotlinx.android.synthetic.main.card_view_about.view.*

class AboutAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class AboutInstance {
        App,
        Version,
        BuildNumber,
        License,
        OpenSource,
        Contact,
        Thanks
    }

    class AboutCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val itemList = listOf(
        AboutInstance.OpenSource,
        AboutInstance.License,
        AboutInstance.Contact,
        AboutInstance.Thanks,
        AboutInstance.App,
        AboutInstance.Version,
        AboutInstance.BuildNumber
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewtype: Int): RecyclerView.ViewHolder {
        val cardView = LayoutInflater.from(parent.context).inflate(R.layout.card_view_about, parent, false)
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
            AboutInstance.App -> {
                holder.itemView.tv_about_title.text = context.getText(R.string.about_title_appName)
                holder.itemView.tv_about_content.text = context.getText(R.string.app_name)
                holder.itemView.tv_about_content.linksClickable = true
            }
            AboutInstance.Version -> {
                holder.itemView.tv_about_title.text = context.getText(R.string.about_title_version)
                holder.itemView.tv_about_content.text = BuildConfig.VERSION_NAME
                holder.itemView.tv_about_content.linksClickable = false
            }
            AboutInstance.BuildNumber -> {
                holder.itemView.tv_about_title.text = context.getText(R.string.about_title_buildNumber)
                holder.itemView.tv_about_content.text = BuildConfig.VERSION_CODE.toString()
                holder.itemView.tv_about_content.linksClickable = false
            }
            AboutInstance.License -> {
                holder.itemView.tv_about_title.text = context.getText(R.string.about_title_license)
                holder.itemView.tv_about_content.text = context.getText(R.string.about_content_license)
                holder.itemView.tv_about_content.linksClickable = true
            }
            AboutInstance.OpenSource -> {
                holder.itemView.tv_about_title.text = context.getText(R.string.about_title_openSource)
                holder.itemView.tv_about_content.text = context.getText(R.string.about_content_openSource)
                holder.itemView.tv_about_content.linksClickable = true
            }
            AboutInstance.Contact -> {
                holder.itemView.tv_about_title.text = context.getText(R.string.about_title_contactInformation)
                holder.itemView.tv_about_content.text = context.getText(R.string.about_content_contact)
                holder.itemView.tv_about_content.linksClickable = true
            }
            AboutInstance.Thanks -> {
                holder.itemView.tv_about_title.text = context.getText(R.string.about_title_thanks)
                holder.itemView.tv_about_content.text = context.getText(R.string.about_content_thanks)
                holder.itemView.tv_about_content.linksClickable = true
            }
        }

    }
}