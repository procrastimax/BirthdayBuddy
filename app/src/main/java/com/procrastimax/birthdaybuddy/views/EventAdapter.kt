package com.procrastimax.birthdaybuddy.views

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.procrastimax.birthdaybuddy.R
import com.procrastimax.birthdaybuddy.handler.EventHandler
import com.procrastimax.birthdaybuddy.models.EventBirthday
import kotlinx.android.synthetic.main.birthday_event_item_view.view.*
import java.text.DateFormat

class EventAdapter(val context: Context) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            val tv_forename: TextView = itemView.findViewById(R.id.tv_forename)
            val tv_surname: TextView = itemView.findViewById(R.id.tv_surname)
            val tv_date_value: TextView = itemView.findViewById(R.id.tv_birthday_date_value)
            val tv_days_until_value: TextView = itemView.findViewById(R.id.tv_days_until_value)
            val tv_years_since_value: TextView = itemView.findViewById(R.id.tv_years_since_value)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventAdapter.EventViewHolder {
        // create a new view
        val item_view = LayoutInflater.from(parent.context).inflate(R.layout.birthday_event_item_view, parent, false)
        return EventViewHolder(item_view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        //check if not null
        if (EventHandler.getValueToKey(position) != null) {
            //check if is birthday event and if the year is given
            if (EventHandler.getValueToKey(position) is EventBirthday) {
                //set date
                holder.itemView.tv_birthday_date_value.text =
                    (EventHandler.getValueToKey(position) as EventBirthday).getPrettyShortStringWithoutYear()

                //set days until
                holder.itemView.tv_days_until_value.text =
                    EventHandler.getValueToKey(position)!!.getDaysUntil().toString()

                //set years since, if specified
                if ((EventHandler.getValueToKey(position) as EventBirthday).isYearGiven) {
                    holder.itemView.tv_years_since_value.text =
                        EventHandler.getValueToKey(position)!!.getYearsSince().toString()
                } else {
                    holder.itemView.tv_years_since_value.text =  context.getString(R.string.empty_value_field)
                }

                //set forename
                holder.itemView.tv_forename.text = (EventHandler.getValueToKey(position) as EventBirthday).forename

                //set surname
                holder.itemView.tv_surname.text = (EventHandler.getValueToKey(position) as EventBirthday).surname

            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return if (EventHandler.getEvents().isEmpty()) {
            0
        } else {
            EventHandler.getLastIndex()
        }
    }
}