package com.procrastimax.birthdaybuddy.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.procrastimax.birthdaybuddy.R
import com.procrastimax.birthdaybuddy.handler.EventHandler
import com.procrastimax.birthdaybuddy.models.EventBirthday
import java.text.DateFormat

const val ITEM_ID_PARAM = "ITEMID"

/**
 * TODO:
 * - add export possibility for contact info as plaintext
 * - add tiny animation for opening this fragment
 */
class ShowBirthdayEvent : Fragment() {

    val tv_forename: TextView by lazy {
        view!!.findViewById<TextView>(R.id.tv_forename)
    }

    val tv_surname: TextView by lazy {
        view!!.findViewById<TextView>(R.id.tv_surname)
    }

    val tv_date: TextView by lazy {
        view!!.findViewById<TextView>(R.id.tv_date)
    }

    val tv_note: TextView by lazy {
        view!!.findViewById<TextView>(R.id.tv_note)
    }

    val tv_years_old: TextView by lazy {
        view!!.findViewById<TextView>(R.id.tv_years_old)
    }

    val tv_days_until: TextView by lazy {
        view!!.findViewById<TextView>(R.id.tv_days_until)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_birthday_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            val item_id = arguments!!.getInt(ITEM_ID_PARAM)
            val birthdayEvent = EventHandler.event_list[item_id].second as EventBirthday
            this.tv_forename.text = birthdayEvent.forename
            this.tv_surname.text = birthdayEvent.surname

            var date = ""
            if (birthdayEvent.isYearGiven) {
                date = birthdayEvent.dateToPrettyString(DateFormat.FULL)

                //show adapted string for first birthday of a person, 1 year, not 1 years
                tv_years_old.text = resources.getQuantityString(
                    R.plurals.person_years_old,
                    birthdayEvent.getYearsSince() + 1,
                    birthdayEvent.forename,
                    birthdayEvent.getYearsSince() + 1
                )

            } else {
                date = birthdayEvent.dateToPrettyString(DateFormat.DATE_FIELD).subSequence(0..5).toString()
                tv_years_old.textSize = 0.0f
            }

            tv_date.text = context!!.resources.getString(R.string.person_show_date, date)

            //show adapted string for 1 day, not 1 days
            if (birthdayEvent.getDaysUntil() == 1) {
                tv_days_until.text = resources.getString(R.string.person_day_until, birthdayEvent.getDaysUntil())
            } else {
                tv_days_until.text = resources.getString(R.string.person_days_until, birthdayEvent.getDaysUntil())
            }

            if (!birthdayEvent.note.isNullOrBlank()) {
                this.tv_note.text = context!!.resources.getString(R.string.person_note, birthdayEvent.note)
                this.tv_note.setTextColor(ContextCompat.getColor(context!!, R.color.darkGrey))
            } else {
                this.tv_note.text = context!!.resources.getString(R.string.person_no_note)
                this.tv_note.textSize = 0.0f
                this.tv_note.setTextColor(ContextCompat.getColor(context!!, R.color.brightGrey))
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        //(context as MainActivity).changeToolbarState(MainActivity.Companion.ToolbarState.Default)
    }

    companion object {
        @JvmStatic
        fun newInstance(): ShowBirthdayEvent {
            return ShowBirthdayEvent()
        }
    }
}
