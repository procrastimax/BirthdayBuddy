package com.procrastimax.birthdaybuddy.views

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.procrastimax.birthdaybuddy.MainActivity
import com.procrastimax.birthdaybuddy.R
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

class EventAdapterSearching(private val context: Context, private val eventIDs: List<Int>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class BirthdayEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class EventMonthDividerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class AnnualEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class OneTimeEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    /**
     * getItemViewType overrides the standard function
     * it defines the different viewholder types used for the recycler view
     * 1 - birthday event viewholder
     * 2 - annual event viewholder
     * 3 - one time event viewholder
     *
     * @param position: Int
     * @return Int
     */
    override fun getItemViewType(position: Int): Int {
        when (EventHandler.getList()[position]) {
            is EventBirthday -> {
                if (eventIDs.contains(EventHandler.getList()[position].eventID))
                    return 1
            }
            is AnnualEvent -> {
                if (eventIDs.contains(EventHandler.getList()[position].eventID))
                    return 2
            }
            is OneTimeEvent -> {
                if (eventIDs.contains(EventHandler.getList()[position].eventID))
                    return 3
            }
        }
        return -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // create a new view
        when (viewType) {
            0 -> {
                val itemView =
                    LayoutInflater.from(parent.context).inflate(R.layout.event_month_view_divider, parent, false)
                return EventMonthDividerViewHolder(itemView)
            }
            1 -> {
                val itemView =
                    LayoutInflater.from(parent.context).inflate(R.layout.birthday_event_item_view, parent, false)
                return BirthdayEventViewHolder(itemView)
            }
            2 -> {
                val itemView =
                    LayoutInflater.from(parent.context).inflate(R.layout.annual_event_item_view, parent, false)
                return AnnualEventViewHolder(itemView)
            }
            3 -> {
                val itemView =
                    LayoutInflater.from(parent.context).inflate(R.layout.one_time_event_item_view, parent, false)
                return OneTimeEventViewHolder(itemView)
            }
            else -> {
                //Default is birthday event
                val itemView = View(context)
                return BirthdayEventViewHolder(itemView)
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
                    (EventHandler.getList()[position] as MonthDivider).month_name
            }

            //BirthdayEventViewHolder
            1 -> {
                //check if is birthday event and if the year is given
                EventHandler.getList()[position].let { birthdayEvent ->
                    if (birthdayEvent is EventBirthday) {

                        //set on click listener for item
                        holder.itemView.setOnClickListener {
                            //replace this activity with the MainActivity to show searched
                            val intent = Intent(context, MainActivity::class.java)
                            intent.putExtra(
                                MainActivity.FRAGMENT_EXTRA_TITLE_EVENTID,
                                birthdayEvent.eventID
                            )
                            intent.putExtra(MainActivity.FRAGMENT_EXTRA_TITLE_POSITION, position)
                            intent.putExtra(MainActivity.FRAGMENT_EXTRA_TITLE_TYPE, MainActivity.FRAGMENT_TYPE_SHOW)
                            startActivity(context, intent, null)
                        }

                        holder.itemView.setOnLongClickListener {
                            //replace this activity with the MainActivity to show searched
                            val intent = Intent(context, MainActivity::class.java)
                            intent.putExtra(
                                MainActivity.FRAGMENT_EXTRA_TITLE_EVENTID,
                                birthdayEvent.eventID
                            )
                            intent.putExtra(MainActivity.FRAGMENT_EXTRA_TITLE_POSITION, position)
                            intent.putExtra(MainActivity.FRAGMENT_EXTRA_TITLE_TYPE, MainActivity.FRAGMENT_TYPE_EDIT)
                            startActivity(context, intent, null)
                            true
                        }

                        //set date
                        holder.itemView.tv_birthday_event_item_date_value.text =
                            birthdayEvent.getPrettyShortStringWithoutYear()

                        //set days until
                        val daysUntil = birthdayEvent.getDaysUntil()
                        if (daysUntil == 0) {
                            holder.itemView.tv_birthday_event_item_days_until_value.text =
                                context.resources.getText(R.string.today)
                        } else {
                            holder.itemView.tv_birthday_event_item_days_until_value.text =
                                birthdayEvent.getDaysUntil().toString()
                        }

                        //set years since, if specified
                        if (birthdayEvent.isYearGiven) {
                            holder.itemView.tv_birthday_event_item_years_since_value.text =
                                (birthdayEvent.getYearsSince() + 1).toString()
                        } else {
                            holder.itemView.tv_birthday_event_item_years_since_value.text =
                                "-"
                        }

                        //if a birthday has a nickname, only show nickname
                        if (birthdayEvent.nickname != null) {

                            //set forename and surname invisible
                            holder.itemView.tv_birthday_event_item_forename.visibility = TextView.GONE
                            holder.itemView.tv_birthday_event_item_surname.visibility = TextView.GONE

                            //set nickname textview visible
                            holder.itemView.tv_birthday_event_item_nickname.visibility = TextView.VISIBLE

                            //set nickname textview text
                            holder.itemView.tv_birthday_event_item_nickname.text =
                                birthdayEvent.nickname

                        } else {

                            //when surname is given, set surname and forename
                            if (birthdayEvent.surname != null) {
                                //set forename and surname invisible
                                holder.itemView.tv_birthday_event_item_forename.visibility = TextView.VISIBLE
                                holder.itemView.tv_birthday_event_item_surname.visibility = TextView.VISIBLE

                                //set nickname textview visible
                                holder.itemView.tv_birthday_event_item_nickname.visibility = TextView.GONE

                                holder.itemView.tv_birthday_event_item_forename.text = birthdayEvent.forename

                                //set surname
                                holder.itemView.tv_birthday_event_item_surname.text = birthdayEvent.surname

                                //when surname is not given, set forename as nickname textview
                            } else {
                                //set forename and surname invisible
                                holder.itemView.tv_birthday_event_item_forename.visibility = TextView.GONE
                                holder.itemView.tv_birthday_event_item_surname.visibility = TextView.GONE

                                //set nickname textview visible
                                holder.itemView.tv_birthday_event_item_nickname.visibility = TextView.VISIBLE

                                //set nickname textview text
                                holder.itemView.tv_birthday_event_item_nickname.text = birthdayEvent.forename
                            }
                        }

                        val avatarUri = birthdayEvent.avatarImageUri

                        //when called from MainActivity
                        if (context is MainActivity) {
                            if (avatarUri != null) {
                                holder.itemView.iv_birthday_event_item_image.setImageBitmap(
                                    BitmapHandler.getBitmapAt(
                                        birthdayEvent.eventID
                                    )
                                )
                            } else {
                                holder.itemView.iv_birthday_event_item_image.setImageResource(R.drawable.ic_birthday_person)
                            }
                        } else {
                            //called from search activity
                            if (avatarUri != null) {
                                holder.itemView.iv_birthday_event_item_image.setImageBitmap(
                                    BitmapHandler.getBitmapAt(
                                        birthdayEvent.eventID
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
                            //replace this activity with the main acitivty to show searched
                            val intent = Intent(context, MainActivity::class.java)
                            intent.putExtra(
                                MainActivity.FRAGMENT_EXTRA_TITLE_EVENTID,
                                annualEvent.eventID
                            )
                            intent.putExtra(MainActivity.FRAGMENT_EXTRA_TITLE_POSITION, position)
                            intent.putExtra(MainActivity.FRAGMENT_EXTRA_TITLE_TYPE, MainActivity.FRAGMENT_TYPE_SHOW)
                            startActivity(context, intent, null)
                        }

                        holder.itemView.setOnLongClickListener {
                            //replace this activity with the main acitivty to show searched
                            val intent = Intent(context, MainActivity::class.java)
                            intent.putExtra(
                                MainActivity.FRAGMENT_EXTRA_TITLE_EVENTID,
                                annualEvent.eventID
                            )
                            intent.putExtra(MainActivity.FRAGMENT_EXTRA_TITLE_POSITION, position)
                            intent.putExtra(MainActivity.FRAGMENT_EXTRA_TITLE_TYPE, MainActivity.FRAGMENT_TYPE_EDIT)
                            startActivity(context, intent, null)
                            true
                        }

                        //set name
                        val name = annualEvent.name
                        holder.itemView.tv_annual_item_name.text = name

                        //set date
                        val date = annualEvent.getPrettyShortStringWithoutYear()
                        holder.itemView.tv_annual_item_date_value.text = date

                        //set days until
                        val daysUntil = annualEvent.getDaysUntil()
                        if (daysUntil == 0) {
                            holder.itemView.tv_days_until_annual_value.text =
                                context.resources.getText(R.string.today)
                        } else {
                            holder.itemView.tv_days_until_annual_value.text = annualEvent.getDaysUntil().toString()
                        }

                        //set years since, if specified
                        if (annualEvent.hasStartYear) {
                            holder.itemView.tv_years_since_annual_value.text =
                                annualEvent.getYearsSince().toString()
                        } else {
                            holder.itemView.tv_years_since_annual_value.text =
                                "-"
                        }
                    }
                }
            }

            //one time event item view holder
            3 -> {
                //check if is birthday event and if the year is given
                //check if is birthday event and if the year is given
                EventHandler.getList()[position].let { oneTimeEvent ->
                    if (oneTimeEvent is OneTimeEvent) {

                        //set on click listener for item
                        holder.itemView.setOnClickListener {
                            //replace this activity with the main acitivty to show searched
                            val intent = Intent(context, MainActivity::class.java)
                            intent.putExtra(
                                MainActivity.FRAGMENT_EXTRA_TITLE_EVENTID,
                                EventHandler.getList()[position].eventID
                            )
                            intent.putExtra(MainActivity.FRAGMENT_EXTRA_TITLE_POSITION, position)
                            intent.putExtra(MainActivity.FRAGMENT_EXTRA_TITLE_TYPE, MainActivity.FRAGMENT_TYPE_SHOW)
                            startActivity(context, intent, null)
                        }

                        holder.itemView.setOnLongClickListener {
                            //replace this activity with the main acitivty to show searched
                            val intent = Intent(context, MainActivity::class.java)
                            intent.putExtra(
                                MainActivity.FRAGMENT_EXTRA_TITLE_EVENTID,
                                EventHandler.getList()[position].eventID
                            )
                            intent.putExtra(MainActivity.FRAGMENT_EXTRA_TITLE_POSITION, position)
                            intent.putExtra(MainActivity.FRAGMENT_EXTRA_TITLE_TYPE, MainActivity.FRAGMENT_TYPE_EDIT)
                            startActivity(context, intent, null)
                            true
                        }

                        //set date
                        holder.itemView.tv_one_time_item_date_value.text =
                            oneTimeEvent.getPrettyShortStringWithoutYear()

                        //set days until
                        val daysUntil = oneTimeEvent.getDaysUntil()
                        if (daysUntil == 0) {
                            holder.itemView.tv_days_until_one_time_value.text =
                                context.resources.getText(R.string.today)
                        } else {
                            holder.itemView.tv_days_until_one_time_value.text = oneTimeEvent.getDaysUntil().toString()
                        }

                        //set years until
                        holder.itemView.tv_years_one_time_value.text = oneTimeEvent.getYearsUntil().toString()

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