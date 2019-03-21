package com.procrastimax.birthdaybuddy.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.procrastimax.birthdaybuddy.MainActivity
import com.procrastimax.birthdaybuddy.R
import com.procrastimax.birthdaybuddy.handler.EventHandler
import com.procrastimax.birthdaybuddy.models.EventAnniversary
import com.procrastimax.birthdaybuddy.models.EventDate
import kotlinx.android.synthetic.main.fragment_anniversary_instance.*
import kotlinx.android.synthetic.main.fragment_event_list.*
import java.text.DateFormat
import java.util.*

/**
 * AnniversaryInstanceFragment is a fragment class for adding/editing an instance of EventAnniversary
 * This fragment shows up, when the users wants to add a new EventAnniversary or edit an existing one
 * The fragment consists of several TextEdits to manage user data input
 *
 * This class inherits from android.support.v4.app.Fragment
 */
class AnniversaryInstanceFragment : EventInstanceFragment() {

    /**
     * isEditAnniversary is a boolean flag to indicate wether this fragment is intended to edit or add an instance of EventAnniversary
     * this is later used to fill TextEdits with existing data of an EventAnniversary instance
     */
    var isEditAnniversary = false

    /**
     * itemID is the id the EventAnniversary has in the EventHandler - eventlist
     * In other words this id is the index of the clicked item from the EventListFragment recyclerview
     */
    var itemID = -1

    /**
     * edit_name is the TextEdit used for editing/ showing the name of the anniversary
     * It is lazy initialized
     */
    val edit_name: EditText by lazy {
        view!!.findViewById<EditText>(R.id.edit_add_fragment_name_anniversary)
    }

    /**
     * edit_date is the TextEdit used for editing/ showing the date of the anniversary
     * It is lazy initialized
     */
    val edit_date: TextView by lazy {
        view!!.findViewById<TextView>(R.id.edit_add_fragment_date_anniversary)
    }

    /**
     * edit_note is the TextEdit used for editing/ showing the note of the anniversary
     * It is lazy initialized
     */
    val edit_note: EditText by lazy {
        view!!.findViewById<EditText>(R.id.edit_add_fragment_note_anniversary)
    }

    /**
     * switch_isYearGiven is the Switch to indicate wether the user wants to provide a date with a year or without a year
     * It is lazy initialized
     */
    val switch_isYearGiven: Switch by lazy {
        view!!.findViewById<Switch>(R.id.sw_is_year_given_anniversary)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_anniversary_instance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //retrieve fragment parameter when edited instance
        if (arguments != null) {
            isEditAnniversary = true
            if (arguments!!.size() > 0) {
                itemID = (arguments!!.getInt(ITEM_ID_PARAM))
                val anniversary = EventHandler.event_list[itemID].second as EventAnniversary

                if (anniversary.hasStartYear) {
                    edit_date.text = EventDate.parseDateToString(anniversary.eventDate, DateFormat.FULL)
                } else {
                    edit_date.text =
                        EventDate.parseDateToString(anniversary.eventDate, DateFormat.DATE_FIELD).substring(0..5)
                }

                edit_name.setText(anniversary.name)
                if (!anniversary.note.isNullOrBlank()) {
                    edit_note.setText(anniversary.note)
                }
                switch_isYearGiven.isChecked = anniversary.hasStartYear

                title.text = resources.getText(R.string.toolbar_title_edit_anniversary)
                btn_fragment_anniversary_instance_delete.visibility = Button.VISIBLE
                btn_fragment_anniversary_instance_delete.setOnClickListener {

                    val alert_builder = AlertDialog.Builder(context)
                    alert_builder.setTitle(resources.getString(R.string.alert_dialog_title_delete_anniversary))
                    alert_builder.setMessage(resources.getString(R.string.alert_dialog_body_message_anniversary))

                    val anniversary_temp = EventHandler.event_list[itemID].second
                    val context_temp = context

                    // Set a positive button and its click listener on alert dialog
                    alert_builder.setPositiveButton(resources.getString(R.string.alert_dialog_accept_delete)) { dialog, which ->
                        // delete anniversary on positive button
                        Snackbar
                            .make(
                                view,
                                resources.getString(R.string.anniversary_deleted_notification, edit_name.text),
                                Snackbar.LENGTH_LONG
                            )
                            .setAction(R.string.snackbar_undo_action_title, View.OnClickListener {
                                EventHandler.addEvent(anniversary_temp, context_temp!!, true)
                                //get last fragment in stack list, which should be eventlistfragment, so we can update the recycler view
                                val fragment =
                                    (context_temp as MainActivity).supportFragmentManager.fragments[(context_temp).supportFragmentManager.backStackEntryCount]
                                if (fragment is EventListFragment) {
                                    fragment.recyclerView.adapter!!.notifyDataSetChanged()
                                }
                            })
                            .show()

                        EventHandler.removeEventByKey(EventHandler.event_list[itemID].first, true)
                        closeBtnPressed()
                    }
                    // dont do anything on negative button
                    alert_builder.setNegativeButton(resources.getString(R.string.alert_dialog_dismiss_delete)) { dialog, which ->
                    }
                    // Finally, make the alert dialog using builder
                    val dialog: AlertDialog = alert_builder.create()
                    // Display the alert dialog on app interface
                    dialog.show()
                }
            }
        } else {
            title.text = resources.getText(R.string.toolbar_title_add_anniversary)
            btn_fragment_anniversary_instance_delete.visibility = Button.INVISIBLE
        }

        edit_date.setOnClickListener {
            showDatePickerDialog()
        }

        switch_isYearGiven.setOnCheckedChangeListener { _, isChecked ->
            if (edit_date.text.isNotBlank()) {
                //year is given
                if (isChecked) {
                    val date = EventDate.parseStringToDate(
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
                    edit_date.hint = context!!.resources.getString(R.string.edit_anniversary_date_hint_with_year)
                } else {
                    edit_date.hint = context!!.resources.getString(R.string.edit_anniversary_date_hint_without_year)
                }
            }
        }
    }

    /**
     * showDatePickerDialog shows a standard android date picker dialog
     * The choosen date in the dialog is set to the edit_date field
     */
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
                        context!!.resources.getText(R.string.future_anniversary_error),
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

    /**
     * acceptBtnPressed is a function which is called when the toolbars accept button is pressed
     */
    override fun acceptBtnPressed() {
        val name = edit_name.text.toString()
        val date = edit_date.text.toString()
        val note = edit_note.text.toString()
        val isYearGiven = switch_isYearGiven.isChecked

        if (name.isBlank() || date.isBlank()) {
            Toast.makeText(context, context!!.resources.getText(R.string.empty_fields_error), Toast.LENGTH_LONG)
                .show()
        } else {
            val anniversary: EventAnniversary
            if (switch_isYearGiven.isChecked) {
                anniversary =
                    EventAnniversary(EventDate.parseStringToDate(date, DateFormat.FULL), name, isYearGiven)
            } else {
                anniversary = EventAnniversary(
                    EventDate.parseStringToDate(
                        date + (Calendar.getInstance().get(Calendar.YEAR) - 1),
                        DateFormat.DATE_FIELD
                    ), name, isYearGiven
                )
            }

            if (note.isNotBlank()) {
                anniversary.note = note
            }

            //new bithday entry, just add a new entry in map
            if (!isEditAnniversary) {
                EventHandler.addEvent(anniversary, context!!, true)

                Snackbar
                    .make(
                        view!!,
                        context!!.resources.getString(R.string.anniversary_added_notification, name),
                        Snackbar.LENGTH_LONG
                    )
                    .show()
                closeBtnPressed()

                //already existant birthday entry, overwrite old entry in map
            } else {
                if (wasChangeMade(EventHandler.event_list[itemID].second as EventAnniversary)) {
                    EventHandler.changeEventAt(EventHandler.event_list[itemID].first, anniversary, context!!, true)
                    Snackbar.make(
                        view!!,
                        context!!.resources.getString(R.string.anniversary_changed_notification, name),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                closeBtnPressed()
            }
        }
    }

    /**
     * wasChangeMade checks wether a change to the edit fields was made or not
     * This is used to avoid unnecessary operations
     * @param event: EventAnniversary, is the comparative object to check against the TextEdit fields
     * @return Boolean, returns false if nothing has changed
     */
    private fun wasChangeMade(event: EventAnniversary): Boolean {
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
        if (edit_name.text.toString() != event.name) return true
        if (switch_isYearGiven.isChecked != event.hasStartYear) return true
        //if nothing has changed return false
        return false
    }

    companion object {
        /**
         * ANNIVERSARY_INSTANCE is the fragments tag as String
         */
        val ANNIVERSARY_INSTANCE_FRAGMENT_TAG = "ANNIVERSARY_INSTANCE"

        /**
         * newInstance returns a new instance of AnniversaryInstanceFragment
         */
        @JvmStatic
        fun newInstance(): AnniversaryInstanceFragment {
            return AnniversaryInstanceFragment()
        }
    }
}