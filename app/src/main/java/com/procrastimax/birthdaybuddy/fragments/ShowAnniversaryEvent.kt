package com.procrastimax.birthdaybuddy.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.procrastimax.birthdaybuddy.MainActivity
import com.procrastimax.birthdaybuddy.R
import com.procrastimax.birthdaybuddy.handler.EventHandler
import com.procrastimax.birthdaybuddy.models.EventAnniversary
import com.procrastimax.birthdaybuddy.models.EventDate
import kotlinx.android.synthetic.main.fragment_show_anniversary_event.*
import java.text.DateFormat

/**
 * ShowAnniversaryEvent is a fragment to show all known data from an instance of EventBirthday
 *
 * TODO:
 * - add tiny animation for opening this fragment
 */
class ShowAnniversaryEvent : ShowEventFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_anniversary_event, container, false)
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
                val newAnniversaryFragment = AnniversaryInstanceFragment.newInstance()
                newAnniversaryFragment.arguments = bundle
                ft.replace(
                    R.id.fragment_placeholder,
                    newAnniversaryFragment,
                    AnniversaryInstanceFragment.ANNIVERSARY_INSTANCE_FRAGMENT_TAG
                )
                ft.addToBackStack(null)
                ft.commit()
            }
            updateUI()
        }
    }

    /**
     * updateUI updates all TextViews and other views to the current instance(Anniversary, Birthday) data
     */
    override fun updateUI() {
        //dont update ui when wrong item id / or deleted item
        if (EventHandler.event_list[item_id].second !is EventAnniversary) {
            (context as MainActivity).supportFragmentManager.popBackStack()
        } else {
            val anniversaryEvent = EventHandler.event_list[item_id].second as EventAnniversary
            //set name of anniversary
            this.tv_show_anniversary_name.text = anniversaryEvent.name

            val date: String
            date = EventDate.parseDateToString(anniversaryEvent.dateToCurrentTimeContext(), DateFormat.FULL)
            if (anniversaryEvent.hasStartYear) {
                //show adapted string for first birthday of a person, 1 year, not 1 years
                tv_show_anniversary_years.text = resources.getQuantityString(
                    R.plurals.anniversary_years,
                    anniversaryEvent.getYearsSince(),
                    anniversaryEvent.getYearsSince()
                )

            } else {
                tv_show_anniversary_years.textSize = 0.0f
            }

            tv_show_anniversary_date.text = resources.getQuantityString(
                R.plurals.anniversary_show_date,
                anniversaryEvent.getDaysUntil(),
                anniversaryEvent.getDaysUntil(),
                date
            )

            if (!anniversaryEvent.note.isNullOrBlank()) {
                this.tv_show_anniversary_note.text =
                    context!!.resources.getString(R.string.anniversary_note, anniversaryEvent.note)
                this.tv_show_anniversary_note.setTextColor(ContextCompat.getColor(context!!, R.color.darkGrey))
            } else {
                this.tv_show_anniversary_note.text = context!!.resources.getString(R.string.anniversary_no_note)
                this.tv_show_anniversary_note.textSize = 0.0f
                this.tv_show_anniversary_note.setTextColor(ContextCompat.getColor(context!!, R.color.brightGrey))
            }
        }
    }

    /**
     * shareEvent a function which is called after the share button has been pressed
     * It provides a simple intent to share data as plain text in other apps
     */
    override fun shareEvent() {
        if (EventHandler.event_list[item_id].second is EventAnniversary) {
            val anniversary = EventHandler.event_list[item_id].second as EventAnniversary

            val intent = Intent(Intent.ACTION_SEND)
            intent.setType("text/plain")
            var shareAnniversaryMsg: String
            //anniversary name
            shareAnniversaryMsg = context!!.resources.getString(R.string.share_anniversary_name, anniversary.name)

            //anniversary next date
            shareAnniversaryMsg += "\n" + context!!.resources.getString(
                R.string.share_anniversary_date_next,
                EventDate.parseDateToString(anniversary.dateToCurrentTimeContext(), DateFormat.FULL)
            )

            //anniversary days until
            shareAnniversaryMsg += "\n" + context!!.resources.getQuantityString(
                R.plurals.share_anniversary_days,
                anniversary.getDaysUntil(),
                anniversary.getDaysUntil()
            )

            if (anniversary.hasStartYear) {
                //anniversary date start
                shareAnniversaryMsg += "\n" + context!!.resources.getString(
                    R.string.share_anniversary_date_start,
                    EventDate.parseDateToString(anniversary.eventDate, DateFormat.FULL)
                )

                //anniversary years since
                shareAnniversaryMsg += "\n" + context!!.resources.getQuantityString(
                    R.plurals.share_anniversary_year,
                    anniversary.getYearsSince(),
                    anniversary.getYearsSince()
                )
            }
            intent.putExtra(Intent.EXTRA_TEXT, shareAnniversaryMsg)
            startActivity(Intent.createChooser(intent, resources.getString(R.string.intent_share_chooser_title)))
        }
    }

    companion object {
        /**
         * SHOW_ANNIVERSARY_FRAGMENT_TAG is the fragments tag as a string
         */
        val SHOW_ANNIVERSARY_FRAGMENT_TAG = "SHOW_ANNIVERSARY"

        /**
         * newInstance returns a new instance of this fragment
         */
        @JvmStatic
        fun newInstance(): ShowAnniversaryEvent {
            return ShowAnniversaryEvent()
        }
    }
}
