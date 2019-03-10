package com.procrastimax.birthdaybuddy.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.procrastimax.birthdaybuddy.MainActivity
import com.procrastimax.birthdaybuddy.R
import com.procrastimax.birthdaybuddy.handler.EventHandler
import com.procrastimax.birthdaybuddy.models.EventBirthday
import com.procrastimax.birthdaybuddy.models.EventDay
import kotlinx.android.synthetic.main.fragment_event_list.*
import java.text.DateFormat
import java.util.*

/**
 * TODO:
 *  - move accept/close button in statusbar
 *  - add animations for accept/close  button
 *  - control beahaviour when hold in potrait mode
 *  - add delete birthday button
 */
class BirthdayInstanceFragment : Fragment() {

    var isEditedBirthday: Boolean = false
    var instanceID = -1

    val edit_forename: EditText by lazy {
        view!!.findViewById<EditText>(R.id.edit_add_fragment_forename)
    }

    val edit_surname: EditText by lazy {
        view!!.findViewById<EditText>(R.id.edit_add_fragment_surname)
    }

    val edit_date: TextView by lazy {
        view!!.findViewById<TextView>(R.id.edit_add_fragment_date)
    }

    val edit_note: EditText by lazy {
        view!!.findViewById<EditText>(R.id.edit_add_fragment_note)
    }

    val switch_isYearGiven: Switch by lazy {
        view!!.findViewById<Switch>(R.id.sw_is_year_given)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_new_birthday, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            isEditedBirthday = true
            //when no arguments are delivered
            if (arguments!!.size() == 0) {

            } else {
                instanceID = (arguments!!.getInt("ID"))
                val forname = (arguments!!.getString("FORENAME"))
                val surname = (arguments!!.getString("SURNAME"))
                val isYearGiven = (arguments!!.getString("ISYEARGIVEN")!!).toBoolean()
                val date = EventDay.parseStringToDate(arguments!!.getString("DATE")!!)
                val note = (arguments!!.getString("NOTE"))

                if (isYearGiven) {
                    edit_date.text = EventDay.parseDateToString(date, DateFormat.FULL)
                } else {
                    edit_date.text = EventDay.parseDateToString(date, DateFormat.DATE_FIELD).substring(0..5)
                }

                edit_surname.setText(surname)
                edit_forename.setText(forname)
                if (!note.isNullOrBlank()) {
                    edit_note.setText(note)
                }
                switch_isYearGiven.isChecked = isYearGiven
            }
        }

        val toolbar = activity!!.findViewById<android.support.v7.widget.Toolbar>(R.id.toolbar)

        val closeBtn = toolbar.findViewById<ImageView>(R.id.btn_add_fragment_close)

        toolbar.setBackgroundColor(ContextCompat.getColor(context!!, R.color.background_material_light))

        closeBtn.setOnClickListener {
            closeButtonPressed()
        }

        val acceptBtn = toolbar.findViewById<ImageView>(R.id.btn_add_fragment_accept)
        acceptBtn.setOnClickListener { acceptButtonPressed() }

        edit_date.setOnClickListener {
            showDatePickerDialog()
        }

        switch_isYearGiven.setOnCheckedChangeListener { _, isChecked ->
            if (edit_date.text.isNotBlank()) {
                //year is given
                if (isChecked) {

                    var date = EventDay.parseStringToDate(
                        edit_date.text.toString() + Calendar.getInstance().get(Calendar.YEAR),
                        DateFormat.DATE_FIELD
                    )
                    //if days of year from date without year is a bigger value than the current day of year, the date is in the future, so it has to be set to current day
                    if (date.after(Calendar.getInstance().time)) {
                        date = Calendar.getInstance().time
                    }
                    edit_date.text = EventDay.parseDateToString(date, DateFormat.FULL)

                    //year is not given
                } else {
                    val date = EventDay.parseStringToDate(edit_date.text.toString(), DateFormat.FULL)
                    edit_date.text = EventDay.parseDateToString(date, DateFormat.DATE_FIELD).substring(0..5)
                }
            } else {
                if (isChecked) {
                    edit_date.hint = context!!.resources.getString(R.string.edit_date_hint_with_year)
                } else {
                    edit_date.hint = context!!.resources.getString(R.string.edit_date_hint_without_year)
                }
            }
        }
    }

    private fun showDatePickerDialog() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd =
            DatePickerDialog(context!!, DatePickerDialog.OnDateSetListener { view, year_, monthOfYear, dayOfMonth ->
                // Display Selected date in Toast
                c.set(Calendar.YEAR, year_)
                c.set(Calendar.MONTH, monthOfYear)
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                if (c.time.after(Calendar.getInstance().time) && switch_isYearGiven.isChecked) {
                    Toast.makeText(
                        view.context,
                        context!!.resources.getText(R.string.future_birthday_error),
                        Toast.LENGTH_LONG
                    ).show()
                } else {

                    if (switch_isYearGiven.isChecked) {
                        edit_date.text = EventDay.parseDateToString(c.time, DateFormat.FULL)
                    } else {
                        edit_date.text = EventDay.parseDateToString(c.time, DateFormat.DATE_FIELD).substring(0..5)
                    }
                }
            }, year, month, day)
        dpd.show()
    }

    override fun onDetach() {
        super.onDetach()
        (context as MainActivity).changeToolbarState(MainActivity.Companion.ToolbarState.Default)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        (context as MainActivity).changeToolbarState(MainActivity.Companion.ToolbarState.AddBirthday)
    }

    fun closeButtonPressed() {
        (context as MainActivity).onBackPressed()
    }

    fun acceptButtonPressed() {

        val forename = edit_forename.text.toString()
        val surname = edit_surname.text.toString()
        val date = edit_date.text.toString()
        val note = edit_note.text.toString()
        val isYearGiven = switch_isYearGiven.isChecked

        if (forename.isBlank() || surname.isBlank() || date.isBlank()) {
            Toast.makeText(context, context!!.resources.getText(R.string.empty_fields_error), Toast.LENGTH_LONG).show()
        } else {
            val birthday: EventBirthday
            if (switch_isYearGiven.isChecked) {
                birthday =
                    EventBirthday(EventDay.parseStringToDate(date, DateFormat.FULL), forename, surname, isYearGiven)
            } else {
                birthday = EventBirthday(
                    EventDay.parseStringToDate(
                        date + (Calendar.getInstance().get(Calendar.YEAR) - 1),
                        DateFormat.DATE_FIELD
                    ), forename, surname, isYearGiven
                )
            }

            if (note.isNotBlank()) {
                birthday.note = note
            }

            //new bithday entry, just add a new entry in map
            if (!isEditedBirthday) {
                EventHandler.addEvent(birthday, true)
                //TODO: add undo action
                Snackbar.make(
                    view!!,
                    context!!.resources.getString(R.string.person_added_notification, forename),
                    Snackbar.LENGTH_LONG
                ).show()
                closeButtonPressed()

                //already existant birthday entry, overwrite old entry in map
            } else {
                EventHandler.changeEventAt(instanceID, birthday, true)

                //TODO: add undo action
                Snackbar.make(
                    view!!,
                    context!!.resources.getString(R.string.person_changed_notification, forename),
                    Snackbar.LENGTH_LONG
                ).show()
                closeButtonPressed()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): BirthdayInstanceFragment {
            return BirthdayInstanceFragment()
        }
    }
}
