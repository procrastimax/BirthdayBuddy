package com.procrastimax.birthdaybuddy.views

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.procrastimax.birthdaybuddy.MainActivity
import com.procrastimax.birthdaybuddy.R
import com.procrastimax.birthdaybuddy.fragments.*
import com.procrastimax.birthdaybuddy.handler.BitmapHandler
import com.procrastimax.birthdaybuddy.handler.EventHandler
import com.procrastimax.birthdaybuddy.models.AnnualEvent
import com.procrastimax.birthdaybuddy.models.EventBirthday
import com.procrastimax.birthdaybuddy.models.MonthDivider
import com.procrastimax.birthdaybuddy.models.OneTimeEvent
import kotlinx.android.synthetic.main.annual_event_item_view.view.*
import kotlinx.android.synthetic.main.birthday_event_item_view.view.*
import kotlinx.android.synthetic.main.event_month_view_divider.view.*
import kotlinx.android.synthetic.main.one_time_event_item_view.view.*


class EventAdapter(private val context: Context, private val fragmentManager: FragmentManager) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var isClickable: Boolean = true

    class BirthdayEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class EventMonthDividerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class AnnualEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class OneTimeEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    /**
     * getItemViewType overrides the standard function
     * it defines the different viewholder types used for the recycler view
     * 0 - month description divider
     * 1 - birthday event viewholder
     * 2 - annual event viewholder
     * 3 - one time event viewholder
     *
     * @param position: Int
     * @return Int
     */
    override fun getItemViewType(position: Int): Int {
        when (EventHandler.getList()[position]) {
            is MonthDivider -> {
                if (position < EventHandler.getList().size - 1) {
                    if (EventHandler.getList()[position + 1] !is MonthDivider) {
                        return 0
                    }
                }
                return -1
            }
            is EventBirthday -> {
                return 1
            }
            is AnnualEvent -> {
                return 2
            }
            is OneTimeEvent -> {
                return 3
            }
        }
        return -1
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
            2 -> {
                val item_view =
                    LayoutInflater.from(parent.context).inflate(R.layout.annual_event_item_view, parent, false)
                return AnnualEventViewHolder(item_view)
            }
            3 -> {
                val item_view =
                    LayoutInflater.from(parent.context).inflate(R.layout.one_time_event_item_view, parent, false)
                return OneTimeEventViewHolder(item_view)
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
                EventHandler.getList()[position].let { monthDivider ->
                    if (monthDivider is MonthDivider) {
                        holder.itemView.tv_divider_description_month.text =
                            monthDivider.month_name
                    }
                }
            }

            //BirthdayEventViewHolder
            1 -> {
                //check if is birthday event and if the year is given
                EventHandler.getList()[position].let { birthday ->
                    if (birthday is EventBirthday) {
                        //set on click listener for item
                        holder.itemView.setOnClickListener {
                            if (isClickable) {
                                val bundle = Bundle()
                                //do this in more adaptable way
                                bundle.putInt(
                                    MainActivity.FRAGMENT_EXTRA_TITLE_EVENTID,
                                    birthday.eventID
                                )
                                val ft = fragmentManager.beginTransaction()
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
                        }

                        holder.itemView.setOnLongClickListener {
                            if (isClickable) {
                                val bundle = Bundle()
                                //do this in more adaptable way
                                bundle.putInt(
                                    MainActivity.FRAGMENT_EXTRA_TITLE_EVENTID,
                                    birthday.eventID
                                )
                                val ft = fragmentManager.beginTransaction()
                                // add arguments to fragment
                                val newBirthdayFragment = BirthdayInstanceFragment.newInstance()
                                newBirthdayFragment.arguments = bundle
                                ft.replace(
                                    R.id.fragment_placeholder,
                                    newBirthdayFragment
                                )
                                ft.addToBackStack(null)
                                ft.commit()
                            }
                            true
                        }

                        val textColor: Int

                        //set days until
                        val daysUntil = birthday.getDaysUntil()
                        if (daysUntil == 0) {
                            textColor = ContextCompat.getColor(context, R.color.colorAccent)
                            holder.itemView.tv_birthday_event_item_days_until_value.text =
                                context.getText(R.string.today)
                            holder.itemView.tv_birthday_event_item_days_until_value.setTextColor(textColor)
                        } else {
                            textColor = ContextCompat.getColor(context, R.color.textDark)
                            holder.itemView.tv_birthday_event_item_days_until_value.text =
                                daysUntil.toString()
                            holder.itemView.tv_birthday_event_item_days_until_value.setTextColor(textColor)
                        }

                        //set date
                        holder.itemView.tv_birthday_event_item_date_value.text =
                            birthday.getPrettyShortStringWithoutYear()
                        holder.itemView.tv_birthday_event_item_date_value.setTextColor(textColor)

                        //set years since, if specified
                        if (birthday.isYearGiven) {
                            holder.itemView.tv_birthday_event_item_years_since_value.text =
                                (birthday.getYearsSince() + 1).toString()
                        } else {
                            holder.itemView.tv_birthday_event_item_years_since_value.text = "-"
                        }
                        holder.itemView.tv_birthday_event_item_years_since_value.setTextColor(textColor)

                        if (birthday.eventAlreadyOccurred()) {
                            holder.itemView.constraint_layout_birthday_item_view.background =
                                ContextCompat.getDrawable(context, R.drawable.ripple_recycler_view_item_dark)
                        } else {
                            holder.itemView.constraint_layout_birthday_item_view.background =
                                ContextCompat.getDrawable(context, R.drawable.ripple_recycler_view_item)
                        }

                        //if a birthday has a nickname, only show nickname
                        if (birthday.nickname != null) {

                            //set forename and surname invisible
                            holder.itemView.tv_birthday_event_item_forename.visibility = TextView.GONE
                            holder.itemView.tv_birthday_event_item_surname.visibility = TextView.GONE

                            //set nickname textview visible
                            holder.itemView.tv_birthday_event_item_nickname.visibility = TextView.VISIBLE
                            holder.itemView.tv_birthday_event_item_nickname.setTextColor(textColor)

                            //set nickname textview text
                            holder.itemView.tv_birthday_event_item_nickname.text = birthday.nickname

                        } else {
                            //when surname is given, set surname and forename
                            if (birthday.surname != null) {
                                //set forename and surname invisible
                                holder.itemView.tv_birthday_event_item_forename.visibility = TextView.VISIBLE
                                holder.itemView.tv_birthday_event_item_surname.visibility = TextView.VISIBLE

                                //set nickname textview visible
                                holder.itemView.tv_birthday_event_item_nickname.visibility = TextView.GONE

                                holder.itemView.tv_birthday_event_item_forename.text = birthday.forename
                                holder.itemView.tv_birthday_event_item_forename.setTextColor(textColor)

                                //set surname
                                holder.itemView.tv_birthday_event_item_surname.text = birthday.surname
                                holder.itemView.tv_birthday_event_item_surname.setTextColor(textColor)

                                //when surname is not given, set forename as nickname textview
                            } else {
                                //set forename and surname invisible
                                holder.itemView.tv_birthday_event_item_forename.visibility = TextView.GONE
                                holder.itemView.tv_birthday_event_item_surname.visibility = TextView.GONE

                                //set nickname textview visible
                                holder.itemView.tv_birthday_event_item_nickname.visibility = TextView.VISIBLE
                                holder.itemView.tv_birthday_event_item_nickname.setTextColor(textColor)

                                //set nickname textview text
                                holder.itemView.tv_birthday_event_item_nickname.text = birthday.forename
                            }
                        }

                        val avatarUri = birthday.avatarImageUri

                        //when context is mainactivity
                        if (context is MainActivity) {
                            if (avatarUri != null && !(context).isLoading) {
                                holder.itemView.iv_birthday_event_item_image.setImageBitmap(
                                    BitmapHandler.getBitmapAt(
                                        birthday.eventID
                                    )
                                )
                            } else {
                                holder.itemView.iv_birthday_event_item_image.setImageResource(R.drawable.ic_birthday_person)
                            }
                        }
                    }
                }
            }

            //annual event item view holder
            2 -> {
                //check if is birthday event and if the year is given
                EventHandler.getList()[position].let { annualEvent ->
                    if (annualEvent is AnnualEvent) {
                        //set on click listener for item
                        holder.itemView.setOnClickListener {
                            if (isClickable) {
                                val bundle = Bundle()
                                //do this in more adaptable way
                                bundle.putInt(
                                    MainActivity.FRAGMENT_EXTRA_TITLE_EVENTID,
                                    annualEvent.eventID
                                )
                                val ft = fragmentManager.beginTransaction()
                                // add arguments to fragment
                                val newAnnualEvent = ShowAnnualEvent.newInstance()
                                newAnnualEvent.arguments = bundle
                                ft.replace(
                                    R.id.fragment_placeholder,
                                    newAnnualEvent
                                )
                                ft.addToBackStack(null)
                                ft.commit()
                            }
                        }

                        holder.itemView.setOnLongClickListener {
                            if (isClickable) {
                                val bundle = Bundle()
                                //do this in more adaptable way
                                bundle.putInt(
                                    MainActivity.FRAGMENT_EXTRA_TITLE_EVENTID,
                                    annualEvent.eventID
                                )
                                val ft = fragmentManager.beginTransaction()
                                // add arguments to fragment
                                val newAnnualEvent = AnnualEventInstanceFragment.newInstance()
                                newAnnualEvent.arguments = bundle
                                ft.replace(
                                    R.id.fragment_placeholder,
                                    newAnnualEvent
                                )
                                ft.addToBackStack(null)
                                ft.commit()
                            }
                            true
                        }

                        val textColor: Int

                        //set days until
                        val daysUntil = EventHandler.getList()[position].getDaysUntil()
                        if (daysUntil == 0) {
                            textColor = ContextCompat.getColor(context, R.color.colorAccent)
                            holder.itemView.tv_days_until_annual_value.text = context.resources.getText(R.string.today)
                            holder.itemView.tv_days_until_annual_value.setTextColor(textColor)
                        } else {
                            textColor = ContextCompat.getColor(context, R.color.textDark)
                            holder.itemView.tv_days_until_annual_value.text = daysUntil.toString()
                            holder.itemView.tv_days_until_annual_value.setTextColor(textColor)
                        }

                        //set date
                        holder.itemView.tv_annual_item_date_value.text =
                            annualEvent.getPrettyShortStringWithoutYear()
                        holder.itemView.tv_annual_item_date_value.setTextColor(textColor)

                        //set years since, if specified
                        if (annualEvent.hasStartYear) {
                            holder.itemView.tv_years_since_annual_value.text = annualEvent.getYearsSince().toString()
                        } else {
                            holder.itemView.tv_years_since_annual_value.text = "-"
                        }
                        holder.itemView.tv_years_since_annual_value.setTextColor(textColor)

                        if (annualEvent.eventAlreadyOccurred()) {
                            holder.itemView.constraint_layout_annual_item_view.background =
                                ContextCompat.getDrawable(context, R.drawable.ripple_recycler_view_item_dark)
                        } else {
                            holder.itemView.constraint_layout_annual_item_view.background =
                                ContextCompat.getDrawable(context, R.drawable.ripple_recycler_view_item)
                        }

                        //set name
                        holder.itemView.tv_annual_item_name.text = annualEvent.name
                    }
                }
            }

            //one time event item view holder
            3 -> {
                //check if is birthday event and if the year is given
                EventHandler.getList()[position].let { oneTimeEvent ->
                    if (oneTimeEvent is OneTimeEvent) {

                        //set on click listener for item
                        holder.itemView.setOnClickListener {
                            if (isClickable) {
                                val bundle = Bundle()
                                bundle.putInt(
                                    MainActivity.FRAGMENT_EXTRA_TITLE_EVENTID,
                                    oneTimeEvent.eventID
                                )
                                val ft = fragmentManager.beginTransaction()
                                // add arguments to fragment
                                val newOneTimeEvent = ShowOneTimeEvent.newInstance()
                                newOneTimeEvent.arguments = bundle
                                ft.replace(
                                    R.id.fragment_placeholder,
                                    newOneTimeEvent
                                )
                                ft.addToBackStack(null)
                                ft.commit()
                            }
                        }

                        holder.itemView.setOnLongClickListener {
                            if (isClickable) {
                                val bundle = Bundle()
                                //do this in more adaptable way
                                bundle.putInt(
                                    MainActivity.FRAGMENT_EXTRA_TITLE_EVENTID,
                                    oneTimeEvent.eventID
                                )
                                val ft = fragmentManager.beginTransaction()
                                // add arguments to fragment
                                val newOneTimeEvent = OneTimeEventInstanceFragment.newInstance()
                                newOneTimeEvent.arguments = bundle
                                ft.replace(
                                    R.id.fragment_placeholder,
                                    newOneTimeEvent
                                )
                                ft.addToBackStack(null)
                                ft.commit()
                            }
                            true
                        }

                        val textColor: Int

                        //set days until
                        val daysUntil = oneTimeEvent.getDaysUntil()
                        if (daysUntil == 0) {
                            textColor = ContextCompat.getColor(context, R.color.colorAccent)
                            holder.itemView.tv_days_until_one_time_value.text =
                                context.resources.getText(R.string.today)
                            holder.itemView.tv_days_until_one_time_value.setTextColor(textColor)
                        } else {
                            textColor = ContextCompat.getColor(context, R.color.textDark)
                            holder.itemView.tv_days_until_one_time_value.text = oneTimeEvent.getDaysUntil().toString()
                            holder.itemView.tv_days_until_one_time_value.setTextColor(textColor)
                        }

                        //set date
                        holder.itemView.tv_one_time_item_date_value.text =
                            oneTimeEvent.getPrettyShortStringWithoutYear()
                        holder.itemView.tv_one_time_item_date_value.setTextColor(textColor)

                        //set years until
                        holder.itemView.tv_years_one_time_value.text = oneTimeEvent.getYearsUntil().toString()
                        holder.itemView.tv_years_one_time_value.setTextColor(textColor)

                        if (oneTimeEvent.eventAlreadyOccurred()) {
                            holder.itemView.constraint_layout_onetime_item_view.background =
                                ContextCompat.getDrawable(context, R.drawable.ripple_recycler_view_item_dark)
                        } else {
                            holder.itemView.constraint_layout_onetime_item_view.background =
                                ContextCompat.getDrawable(context, R.drawable.ripple_recycler_view_item)
                        }

                        //set name
                        holder.itemView.tv_one_time_item_name.text = oneTimeEvent.name
                    }
                }
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return if (EventHandler.getList().isEmpty()) {
            0
        } else {
            EventHandler.getList().size
        }
    }
}