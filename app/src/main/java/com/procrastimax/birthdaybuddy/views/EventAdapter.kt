package com.procrastimax.birthdaybuddy.views

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.procrastimax.birthdaybuddy.MainActivity
import com.procrastimax.birthdaybuddy.R
import com.procrastimax.birthdaybuddy.fragments.BirthdayInstanceFragment
import com.procrastimax.birthdaybuddy.fragments.ITEM_ID_PARAM
import com.procrastimax.birthdaybuddy.fragments.ShowBirthdayEvent
import com.procrastimax.birthdaybuddy.handler.EventHandler
import com.procrastimax.birthdaybuddy.models.EventBirthday
import com.procrastimax.birthdaybuddy.models.MonthDivider
import kotlinx.android.synthetic.main.birthday_event_item_view.view.*
import kotlinx.android.synthetic.main.event_month_view_divider.view.*

class EventAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class BirthdayEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_forename: TextView = itemView.findViewById(R.id.tv_forename)
        val tv_surname: TextView = itemView.findViewById(R.id.tv_surname)
        val tv_date_value: TextView = itemView.findViewById(R.id.tv_birthday_date_value)
        val tv_days_until_value: TextView = itemView.findViewById(R.id.tv_days_until_value)
        val tv_years_since_value: TextView = itemView.findViewById(R.id.tv_years_since_value)
    }

    class EventMonthDividerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //val tv_month_name: TextView = itemView.findViewById(R.id.tv_divider_description_month)
    }

    /**
     * getItemViewType overrides the standard function
     * it defines the different viewholder types used for the recycler view
     * 0 - month description divider
     * 1 - birthday event viewholder
     *
     * @param position: Int
     * @return Int
     */
    override fun getItemViewType(position: Int): Int {
        if (EventHandler.event_list[position].second is MonthDivider) {
            return 0
        } else return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // create a new view
        when (viewType) {
            0 -> {
                val item_view =
                    LayoutInflater.from(parent.context).inflate(R.layout.event_month_view_divider, parent, false)
                return EventMonthDividerViewHolder(item_view)
            }
            1 -> {
                val item_view =
                    LayoutInflater.from(parent.context).inflate(R.layout.birthday_event_item_view, parent, false)
                return BirthdayEventViewHolder(item_view)
            }
            else -> {
                //Default is birthday event
                val item_view = View(context)
                return EventMonthDividerViewHolder(item_view)
            }
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // - get element from dataset at this position
        // - replace the contents of the view with that element

        when (holder.itemViewType) {
            //EventMonthDividerViewHolder
            0 -> {
                holder.itemView.tv_divider_description_month.text =
                    (EventHandler.event_list[position].second as MonthDivider).month_name
            }
            //BirthdayEventViewHolder
            1 -> {
                //check if is birthday event and if the year is given
                if (EventHandler.event_list[position].second is EventBirthday) {

                    //TODO: make key names static
                    //set on click listener for item
                    holder.itemView.setOnClickListener {
                        val bundle = Bundle()
                        //do this in more adaptable way
                        bundle.putInt(
                            ITEM_ID_PARAM,
                            position
                        )

                        val ft = (context as MainActivity).supportFragmentManager.beginTransaction()
                        // add arguments to fragment
                        val newBirthdayFragment = ShowBirthdayEvent.newInstance()
                        newBirthdayFragment.arguments = bundle
                        ft.replace(
                            R.id.fragment_placeholder,
                            newBirthdayFragment
                        )
                        ft.addToBackStack(null)
                        ft.commit()

                    }

                    holder.itemView.setOnLongClickListener {
                        val bundle = Bundle()
                        //do this in more adaptable way
                        bundle.putInt(
                            ITEM_ID_PARAM,
                            position
                        )

                        val ft = (context as MainActivity).supportFragmentManager.beginTransaction()

                        // add arguments to fragment
                        val newBirthdayFragment = BirthdayInstanceFragment.newInstance()
                        newBirthdayFragment.arguments = bundle
                        ft.replace(
                            R.id.fragment_placeholder,
                            newBirthdayFragment
                        )
                        ft.addToBackStack(null)
                        ft.commit()
                        true
                    }

                    //set date
                    holder.itemView.tv_birthday_date_value.text =
                        (EventHandler.event_list[position].second as EventBirthday).getPrettyShortStringWithoutYear()

                    //set days until
                    holder.itemView.tv_days_until_value.text =
                        EventHandler.event_list[position].second.getDaysUntil().toString()

                    //set years since, if specified
                    if ((EventHandler.event_list[position].second as EventBirthday).isYearGiven) {
                        holder.itemView.tv_years_since_value.text =
                            EventHandler.event_list[position].second.getYearsSince().toString()
                    } else {
                        holder.itemView.tv_years_since_value.text = context.getString(R.string.empty_value_field)
                    }

                    //set forename
                    holder.itemView.tv_forename.text =
                        (EventHandler.event_list[position].second as EventBirthday).forename

                    //set surname
                    holder.itemView.tv_surname.text =
                        (EventHandler.event_list[position].second as EventBirthday).surname
                }
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