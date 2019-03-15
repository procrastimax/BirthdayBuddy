package com.procrastimax.birthdaybuddy.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
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
import com.procrastimax.birthdaybuddy.models.EventDate
import java.text.DateFormat
import java.util.*

/**
 * TODO:
 *  - move accept/close button in statusbar
 *  - add animations for accept/close  button
 *  - control beahaviour when hold in potrait mode
 */
class BirthdayInstanceFragment : Fragment() {

    var isEditedBirthday: Boolean = false
    var itemID = -1

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

    /**
     * wasChangeMade checks wether a change to the edit fields was made or not
     * @param event: EventBirthday
     * @return Boolean
     */
    private fun wasChangeMade(event: EventBirthday): Boolean {
        if (switch_isYearGiven.isChecked) {
            if (edit_date.text != event.dateToPrettyString(DateFormat.FULL)) return true
        } else {
            if (edit_date.text != event.dateToPrettyString(DateFormat.DATE_FIELD).subSequence(0..5).toString()) return true
        }

        if (edit_note.text.isNotBlank() && event.note == null) {
            return true
        } else {
            if (event.note != null) {
                if (edit_note.text.toString() != event.note!!) return true
            }
        }

        if (edit_forename.text.toString() != event.forename) return true
        if (edit_surname.text.toString() != event.surname) return true
        if (switch_isYearGiven.isChecked != event.isYearGiven) return true

        return false
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

        //retrieve fragment parameter when edited instance
        if (arguments != null) {
            isEditedBirthday = true
            //when no arguments are delivered
            if (arguments!!.size() == 0) {

            } else {
                itemID = (arguments!!.getInt(ITEM_ID_PARAM))
                val birthday = EventHandler.event_list[itemID].second as EventBirthday

                if (birthday.isYearGiven) {
                    edit_date.text = EventDate.parseDateToString(birthday.eventDate, DateFormat.FULL)
                } else {
                    edit_date.text =
                        EventDate.parseDateToString(birthday.eventDate, DateFormat.DATE_FIELD).substring(0..5)
                }

                edit_surname.setText(birthday.surname)
                edit_forename.setText(birthday.forename)
                if (!birthday.note.isNullOrBlank()) {
                    edit_note.setText(birthday.note)
                }
                switch_isYearGiven.isChecked = birthday.isYearGiven
            }
        }
        //switch toolbar layout
        if (isEditedBirthday) {
            (context as MainActivity).changeToolbarState(MainActivity.Companion.ToolbarState.EditBirtday)

        } else {
            (context as MainActivity).changeToolbarState(MainActivity.Companion.ToolbarState.AddBirthday)
        }

        val toolbar = activity!!.findViewById<android.support.v7.widget.Toolbar>(R.id.toolbar)

        val closeBtn = toolbar.findViewById<ImageView>(R.id.btn_add_fragment_close)

        toolbar.setBackgroundColor(ContextCompat.getColor(context!!, R.color.material_light_white_background))
        toolbar.setContentInsetsAbsolute(0, 0)

        //when edit instance birthday, than initialize delete btn
        if (isEditedBirthday) {
            val deleteBtn = toolbar.findViewById<ImageView>(R.id.btn_delete_birthday)
            deleteBtn.setOnClickListener {


                val alert_builder = AlertDialog.Builder(context)
                alert_builder.setTitle(resources.getString(R.string.alert_dialog_title_delete_birthday))
                alert_builder.setMessage(resources.getString(R.string.alert_dialog_body_message))

                // Set a positive button and its click listener on alert dialog
                alert_builder.setPositiveButton(resources.getString(R.string.alert_dialog_accept_delete_birthday)) { dialog, which ->
                    // delete birthday on positive button
                    Snackbar.make(
                        view,
                        resources.getString(R.string.person_deleted_notification, edit_forename.text),
                        Snackbar.LENGTH_LONG
                    ).show()
                    EventHandler.removeEventByKey(EventHandler.event_list[itemID].first, true)
                    closeButtonPressed()
                }

                // dont do anything on negative button
                alert_builder.setNegativeButton(resources.getString(R.string.alert_dialog_dismiss_delete_birthday)) { dialog, which ->
                }

                // Finally, make the alert dialog using builder
                val dialog: AlertDialog = alert_builder.create()

                // Display the alert dialog on app interface
                dialog.show()
            }
        }

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
                    var date = EventDate.parseStringToDate(
                        edit_date.text.toString() + (Calendar.getInstance().get(Calendar.YEAR) - 1),
                        DateFormat.DATE_FIELD
                    )

                    edit_date.text = EventDate.parseDateToString(date, DateFormat.FULL)

                    //year is not given
                } else {
                    val date = EventDate.parseStringToDate(edit_date.text.toString(), DateFormat.FULL)
                    edit_date.text = EventDate.parseDateToString(date, DateFormat.DATE_FIELD).substring(0..5)
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

        //set calendar to the date which is stored in the edit field, when the edit is not empty
        if (!edit_date.text.isNullOrBlank()) {
            if (switch_isYearGiven.isChecked) {
                c.time = EventDate.parseStringToDate(edit_date.text.toString(), DateFormat.FULL)
            } else {
                c.time = EventDate.parseStringToDate(
                    edit_date.text.toString() + (Calendar.getInstance().get(Calendar.YEAR) - 1),
                    DateFormat.DATE_FIELD
                )
            }
        }

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
                        edit_date.text = EventDate.parseDateToString(c.time, DateFormat.FULL)
                    } else {
                        edit_date.text = EventDate.parseDateToString(c.time, DateFormat.DATE_FIELD).substring(0..5)
                    }
                }
            }, year, month, day)
        dpd.show()
    }

    override fun onDetach() {
        super.onDetach()
        (context as MainActivity).changeToolbarState(MainActivity.Companion.ToolbarState.Default)
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
                    EventBirthday(EventDate.parseStringToDate(date, DateFormat.FULL), forename, surname, isYearGiven)
            } else {
                birthday = EventBirthday(
                    EventDate.parseStringToDate(
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
                if (wasChangeMade(EventHandler.event_list[itemID].second as EventBirthday)) {
                    EventHandler.changeEventAt(EventHandler.event_list[itemID].first, birthday, context!!, true)

                    //TODO: add undo action
                    Snackbar.make(
                        view!!,
                        context!!.resources.getString(R.string.person_changed_notification, forename),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
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
