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
import com.procrastimax.birthdaybuddy.handler.DrawableHandler
import com.procrastimax.birthdaybuddy.handler.EventHandler
import com.procrastimax.birthdaybuddy.models.EventBirthday
import com.procrastimax.birthdaybuddy.models.EventDate
import kotlinx.android.synthetic.main.fragment_show_birthday_event.*
import java.text.DateFormat

const val ITEM_ID_PARAM = "ITEMID"

/**
 * TODO:
 * - add export possibility for contact info as plaintext
 * - add tiny animation for opening this fragment
 */
class ShowBirthdayEvent : Fragment() {

    var item_id: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_birthday_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (context as MainActivity).changeToolbarState(MainActivity.Companion.ToolbarState.ShowEvent)
        val toolbar = activity!!.findViewById<android.support.v7.widget.Toolbar>(R.id.toolbar)
        val closeBtn = toolbar.findViewById<ImageView>(R.id.iv_toolbar_show_event_back)
        val editBtn = toolbar.findViewById<ImageView>(R.id.iv_toolbar_show_event_edit)
        val shareBtn = toolbar.findViewById<ImageView>(R.id.iv_toolbar_show_event_share)

        closeBtn.setOnClickListener {
            closeButtonPressed()
        }

        shareBtn.setOnClickListener {
            shareAnniversary()
        }

        if (arguments != null) {
            item_id = arguments!!.getInt(ITEM_ID_PARAM)

            editBtn.setOnClickListener {
                val bundle = Bundle()
                //do this in more adaptable way
                bundle.putInt(
                    ITEM_ID_PARAM,
                    item_id
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
            }
            updateUI(item_id)
        }
    }

    private fun updateUI(id: Int) {
        //dont update ui when wrong item id / or deleted item
        if (EventHandler.event_list[id].second !is EventBirthday) {
            (context as MainActivity).supportFragmentManager.popBackStack()
        } else {
            val birthdayEvent = EventHandler.event_list[id].second as EventBirthday
            this.tv_show_birthday_forename.text = birthdayEvent.forename
            this.tv_show_birthday_surname.text = birthdayEvent.surname

            val date: String
            if (birthdayEvent.isYearGiven) {
                date = birthdayEvent.dateToPrettyString(DateFormat.FULL)

                //show adapted string for first birthday of a person, 1 year, not 1 years
                tv_show_birthday_years_old.text = resources.getQuantityString(
                    R.plurals.person_years_old,
                    birthdayEvent.getYearsSince() + 1,
                    birthdayEvent.forename,
                    birthdayEvent.getYearsSince() + 1
                )

            } else {
                date = birthdayEvent.dateToPrettyString(DateFormat.DATE_FIELD).substring(0..5)
                tv_show_birthday_years_old.textSize = 0.0f
            }

            tv_show_birthday_date.text = context!!.resources.getString(R.string.person_show_date, date)

            //show adapted string for 1 day, not 1 days
            if (birthdayEvent.getDaysUntil() == 1) {
                tv_show_birthday_days.text =
                    resources.getQuantityString(
                        R.plurals.person_days_until,
                        birthdayEvent.getDaysUntil(),
                        birthdayEvent.forename,
                        birthdayEvent.getDaysUntil(),
                        EventDate.parseDateToString(birthdayEvent.dateToCurrentTimeContext(), DateFormat.FULL)
                    )
            } else {
                tv_show_birthday_days.text =
                    resources.getQuantityString(
                        R.plurals.person_days_until,
                        birthdayEvent.getDaysUntil(),
                        birthdayEvent.forename,
                        birthdayEvent.getDaysUntil(),
                        EventDate.parseDateToString(birthdayEvent.dateToCurrentTimeContext(), DateFormat.FULL)
                    )
            }

            if (!birthdayEvent.note.isNullOrBlank()) {
                this.tv_show_birthday_note.text =
                    context!!.resources.getString(R.string.person_note, birthdayEvent.note)
                this.tv_show_birthday_note.setTextColor(ContextCompat.getColor(context!!, R.color.darkGrey))
            } else {
                this.tv_show_birthday_note.text = context!!.resources.getString(R.string.person_no_note)
                this.tv_show_birthday_note.textSize = 0.0f
                this.tv_show_birthday_note.setTextColor(ContextCompat.getColor(context!!, R.color.brightGrey))
            }

            val avatarUri = (EventHandler.event_list[item_id].second as EventBirthday).avatarImageUri

            if (avatarUri != null && avatarUri != "-") {
                iv_avatar.setImageDrawable(
                    DrawableHandler.getDrawableAt(EventHandler.event_list[item_id].first)
                )
            }
        }
    }

    private fun shareAnniversary() {
        if (EventHandler.event_list[item_id].second is EventBirthday) {
            val birthday = EventHandler.event_list[item_id].second as EventBirthday

            val intent = Intent(Intent.ACTION_SEND)
            intent.setType("text/plain")
            var shareBirthdayMsg: String
            // forename/ surname
            shareBirthdayMsg =
                context!!.resources.getString(
                    R.string.share_birthday_name, birthday.forename, birthday.surname
                )

            //next birthday
            shareBirthdayMsg += "\n" + context!!.resources.getString(
                R.string.share_birthday_date_next,
                EventDate.parseDateToString(birthday.dateToCurrentTimeContext(), DateFormat.FULL)
            )

            // in X days
            shareBirthdayMsg += "\n" + context!!.resources.getQuantityString(
                R.plurals.share_birthday_days,
                birthday.getDaysUntil(),
                birthday.getDaysUntil()
            )

            if (birthday.isYearGiven) {
                //date person was born
                shareBirthdayMsg += "\n" + context!!.resources.getString(
                    R.string.share_birthday_date_start,
                    EventDate.parseDateToString(birthday.eventDate, DateFormat.FULL)
                )
                //person currently X years old
                shareBirthdayMsg += "\n" + context!!.resources.getQuantityString(
                    R.plurals.share_birthday_year,
                    birthday.getYearsSince(),
                    birthday.getYearsSince()
                )
            }
            intent.putExtra(Intent.EXTRA_TEXT, shareBirthdayMsg)
            startActivity(Intent.createChooser(intent, resources.getString(R.string.intent_share_chooser_title)))
        }
    }

    override fun onDetach() {
        super.onDetach()
        (context as MainActivity).changeToolbarState(MainActivity.Companion.ToolbarState.Default)
    }

    fun closeButtonPressed() {
        (context as MainActivity).onBackPressed()
    }

    companion object {
        @JvmStatic
        fun newInstance(): ShowBirthdayEvent {
            return ShowBirthdayEvent()
        }
    }
}
