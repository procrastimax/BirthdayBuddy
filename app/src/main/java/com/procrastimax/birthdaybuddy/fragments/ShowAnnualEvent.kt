package com.procrastimax.birthdaybuddy.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.procrastimax.birthdaybuddy.MainActivity
import com.procrastimax.birthdaybuddy.R
import com.procrastimax.birthdaybuddy.handler.EventHandler
import com.procrastimax.birthdaybuddy.models.AnnualEvent
import com.procrastimax.birthdaybuddy.models.EventDate
import kotlinx.android.synthetic.main.fragment_show_annual_event.*
import java.text.DateFormat

/**
 * ShowAnnualEvent is a fragment to show all known data from an instance of EventBirthday
 *
 * TODO:
 * - add tiny animation for opening this fragment
 */
class ShowAnnualEvent : ShowEventFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_annual_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            item_id = arguments!!.getInt(ITEM_ID_PARAM)

            val editBtn: ImageView = toolbar.findViewById<ImageView>(R.id.iv_toolbar_show_event_edit)

            editBtn.setOnClickListener {
                val bundle = Bundle()
                bundle.putInt(
                    ITEM_ID_PARAM,
                    item_id
                )
                val ft = (context as MainActivity).supportFragmentManager.beginTransaction()

                // add arguments to fragment
                val newAnnualEventInstanceFragment = AnnualEventInstanceFragment.newInstance()
                newAnnualEventInstanceFragment.arguments = bundle
                ft.replace(
                    R.id.fragment_placeholder,
                    newAnnualEventInstanceFragment,
                    AnnualEventInstanceFragment.ANNUAL_EVENT_INSTANCE_FRAGMENT_TAG
                )
                ft.addToBackStack(null)
                ft.commit()
                arguments = null
            }
            updateUI()
        } else {
            (context as MainActivity).supportFragmentManager.popBackStack()
        }
    }

    /**
     * updateUI updates all TextViews and other views to the current instance(AnnualEvent, Birthday) data
     */
    override fun updateUI() {
        //dont update ui when wrong item id / or deleted item
        if (EventHandler.getList()[item_id] !is AnnualEvent) {
            (context as MainActivity).supportFragmentManager.popBackStack()
        } else {
            val annual_event = EventHandler.getList()[item_id] as AnnualEvent
            //set name of annual_event
            this.tv_show_annual_event_name.text = annual_event.name

            val date: String
            date =
                EventDate.parseDateToString(EventDate.dateToCurrentTimeContext(annual_event.eventDate), DateFormat.FULL)
            if (annual_event.hasStartYear) {
                //show adapted string for first birthday of a person, 1 year, not 1 years
                tv_show_annual_event_years.text = resources.getQuantityString(
                    R.plurals.annual_event_years,
                    annual_event.getYearsSince(),
                    annual_event.getYearsSince()
                )

            } else {
                this.tv_show_annual_event_years.visibility = TextView.GONE
            }

            tv_show_annual_event_date.text = resources.getQuantityString(
                R.plurals.annual_event_show_date,
                annual_event.getDaysUntil(),
                annual_event.getDaysUntil(),
                date
            )

            if (!annual_event.note.isNullOrBlank()) {
                this.tv_show_annual_event_note.text =
                    context!!.resources.getString(R.string.annual_event_note, annual_event.note)
                this.tv_show_annual_event_note.setTextColor(ContextCompat.getColor(context!!, R.color.darkGrey))
            } else {
                this.tv_show_annual_event_note.visibility = TextView.GONE
            }
        }
    }

    /**
     * shareEvent a function which is called after the share button has been pressed
     * It provides a simple intent to share data as plain text in other apps
     */
    override fun shareEvent() {
        if (EventHandler.getList()[item_id] is AnnualEvent) {
            val annual_event = EventHandler.getList()[item_id] as AnnualEvent

            val intent = Intent(Intent.ACTION_SEND)
            intent.setType("text/plain")
            var share_Annual_Event_Msg: String
            //annual_event name
            share_Annual_Event_Msg = context!!.resources.getString(R.string.share_annual_event_name, annual_event.name)

            //annual_event next date
            share_Annual_Event_Msg += "\n" + context!!.resources.getString(
                R.string.share_annual_event_date_next,
                EventDate.parseDateToString(EventDate.dateToCurrentTimeContext(annual_event.eventDate), DateFormat.FULL)
            )

            //annual_event days until
            share_Annual_Event_Msg += "\n" + context!!.resources.getQuantityString(
                R.plurals.share_annual_event_days,
                annual_event.getDaysUntil(),
                annual_event.getDaysUntil()
            )

            if (annual_event.hasStartYear) {
                //annual_event date start
                share_Annual_Event_Msg += "\n" + context!!.resources.getString(
                    R.string.share_annual_event_date_start,
                    EventDate.parseDateToString(annual_event.eventDate, DateFormat.FULL)
                )

                //annual_event years since
                share_Annual_Event_Msg += "\n" + context!!.resources.getQuantityString(
                    R.plurals.share_annual_event_year,
                    annual_event.getYearsSince(),
                    annual_event.getYearsSince()
                )
            }
            intent.putExtra(Intent.EXTRA_TEXT, share_Annual_Event_Msg)
            startActivity(Intent.createChooser(intent, resources.getString(R.string.intent_share_chooser_title)))
        }
    }

    companion object {
        /**
         * SHOW_ANNUAL_EVENT_INSTANCE_FRAGMENT_TAG is the fragments tag as a string
         */
        val SHOW_ANNUAL_EVENT_INSTANCE_FRAGMENT_TAG = "SHOW_ANNUAL_EVENT"

        /**
         * newInstance returns a new instance of this fragment
         */
        @JvmStatic
        fun newInstance(): ShowAnnualEvent {
            return ShowAnnualEvent()
        }
    }
}
