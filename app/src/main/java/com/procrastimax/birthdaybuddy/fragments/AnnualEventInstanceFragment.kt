package com.procrastimax.birthdaybuddy.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.procrastimax.birthdaybuddy.MainActivity
import com.procrastimax.birthdaybuddy.R
import com.procrastimax.birthdaybuddy.handler.EventHandler
import com.procrastimax.birthdaybuddy.models.AnnualEvent
import com.procrastimax.birthdaybuddy.models.EventDate
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_annual_instance.*
import kotlinx.android.synthetic.main.fragment_event_list.*
import java.text.DateFormat
import java.util.*

/**
 * AnnualEventInstanceFragment is a fragment class for adding/editing an instance of AnnualEvent
 * This fragment shows up, when the users wants to add a new AnnualEvent or edit an existing one
 * The fragment consists of several TextEdits to manage user data input
 *
 * This class inherits from android.support.v4.app.Fragment
 */
class AnnualEventInstanceFragment : EventInstanceFragment() {

    /**
     * isEditAnnualEvent is a boolean flag to indicate wether this fragment is intended to edit or add an instance of AnnualEvent
     * this is later used to fill TextEdits with existing data of an AnnualEvent instance
     */
    var isEditAnnualEvent = false

    /**
     * eventID is the id the AnnualEvent has in the EventHandler - eventlist
     * In other words this id is the index of the clicked item from the EventListFragment recyclerview
     */
    var eventID = -1

    /**
     * edit_name is the TextEdit used for editing/ showing the name of the annual event
     * It is lazy initialized
     */
    val edit_name: EditText by lazy {
        view!!.findViewById<EditText>(R.id.edit_add_fragment_name_annual_event)
    }

    /**
     * edit_date is the TextEdit used for editing/ showing the date of the annual_event
     * It is lazy initialized
     */
    val edit_date: TextView by lazy {
        view!!.findViewById<TextView>(R.id.edit_add_fragment_date_annual_event)
    }

    /**
     * edit_note is the TextEdit used for editing/ showing the note of the annual_event
     * It is lazy initialized
     */
    val edit_note: EditText by lazy {
        view!!.findViewById<EditText>(R.id.edit_add_fragment_note_annual_event)
    }

    /**
     * switch_isYearGiven is the Switch to indicate wether the user wants to provide a date with a year or without a year
     * It is lazy initialized
     */
    val switch_isYearGiven: Switch by lazy {
        view!!.findViewById<Switch>(R.id.sw_is_year_given_annual_event)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_annual_instance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //retrieve fragment parameter when edited instance
        if (arguments != null) {
            isEditAnnualEvent = true

            setToolbarTitle(context!!.resources.getString(R.string.toolbar_title_edit_annual_event))

            eventID = (arguments!!.getInt(ITEM_ID_PARAM_EVENTID))
            EventHandler.getEventToEventIndex(eventID)?.let { annualEvent ->
                if (annualEvent is AnnualEvent) {

                    this.eventDate = annualEvent.eventDate

                    if (annualEvent.hasStartYear) {
                        edit_date.text = EventDate.parseDateToString(this.eventDate, DateFormat.DEFAULT)
                    } else {
                        edit_date.text = EventDate.getLocalizedDayAndMonth(context!!, this.eventDate)
                    }

                    edit_name.setText(annualEvent.name)
                    if (!annualEvent.note.isNullOrBlank()) {
                        edit_note.setText(annualEvent.note)
                    }
                    switch_isYearGiven.isChecked = annualEvent.hasStartYear

                    btn_fragment_annual_event_instance_delete.visibility = Button.VISIBLE
                    btn_fragment_annual_event_instance_delete.setOnClickListener {

                        val alertBuilder = AlertDialog.Builder(context)
                        alertBuilder.setTitle(resources.getString(R.string.alert_dialog_title_delete_annual_event))
                        alertBuilder.setMessage(resources.getString(R.string.alert_dialog_body_message_annual_event))

                        val annualEventTemp = annualEvent
                        val contextTemp = context

                        // Set a positive button and its click listener on alert dialog
                        alertBuilder.setPositiveButton(resources.getString(R.string.alert_dialog_accept_delete)) { _, _ ->
                            // delete annual_event on positive button
                            Snackbar
                                .make(
                                    view,
                                    resources.getString(R.string.annual_event_deleted_notification, edit_name.text),
                                    Snackbar.LENGTH_LONG
                                )
                                .setAction(R.string.snackbar_undo_action_title, View.OnClickListener {
                                    EventHandler.addEvent(
                                        annualEventTemp, contextTemp!!,
                                        true
                                    )
                                    //get last fragment in stack list, when its eventlistfragment, we can update the recycler view
                                    val fragment =
                                        (contextTemp as MainActivity).supportFragmentManager.fragments[(contextTemp).supportFragmentManager.backStackEntryCount]
                                    if (fragment is EventListFragment) {
                                        fragment.recyclerView.adapter!!.notifyDataSetChanged()
                                    }
                                })
                                .show()

                            EventHandler.removeEventByID(eventID, context!!, true)
                            closeBtnPressed()
                        }
                        alertBuilder.setNegativeButton(resources.getString(R.string.alert_dialog_dismiss_delete)) { dialog, _ -> dialog.dismiss() }
                        // Finally, make the alert dialog using builder
                        val dialog: AlertDialog = alertBuilder.create()
                        // Display the alert dialog on app interface
                        dialog.show()
                    }
                }
            }
        } else {
            setToolbarTitle(context!!.resources.getString(R.string.toolbar_title_add_annual_event))
            btn_fragment_annual_event_instance_delete.visibility = Button.INVISIBLE
            (context as MainActivity).progress_bar_main.visibility = ProgressBar.GONE
            edit_date.hint = resources.getString(
                R.string.annual_event_instance_fragment_date_edit_hint,
                EventDate.parseDateToString(this.eventDate, DateFormat.DEFAULT)
            )
        }

        edit_date.setOnClickListener {
            showDatePickerDialog()
        }

        switch_isYearGiven.setOnCheckedChangeListener { _, isChecked ->
            if (edit_date.text.isNotBlank()) {
                //year is given
                if (isChecked) {
                    edit_date.text = EventDate.parseDateToString(this.eventDate, DateFormat.DEFAULT)
                    //year is not given
                } else {
                    edit_date.text = EventDate.getLocalizedDayAndMonth(context!!, this.eventDate)
                }
            } else {
                if (isChecked) {
                    edit_date.hint = resources.getString(
                        R.string.annual_event_instance_fragment_date_edit_hint,
                        EventDate.parseDateToString(this.eventDate, DateFormat.DEFAULT)
                    )
                } else {
                    edit_date.hint = resources.getString(
                        R.string.annual_event_instance_fragment_date_edit_hint,
                        EventDate.getLocalizedDayAndMonth(context!!, this.eventDate)
                    )
                }
            }
        }
    }

    /**
     * showDatePickerDialog shows a standard android date picker dialog
     * The chosen date in the dialog is set to the edit_date field
     */
    private fun showDatePickerDialog() {
        val c = Calendar.getInstance()
        //set calendar to the date which is stored in the edit field, when the edit is not empty
        if (!edit_date.text.isNullOrBlank()) {
            c.time = this.eventDate
        }
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd =
            DatePickerDialog(
                context!!,
                DatePickerDialog.OnDateSetListener { view, year_, monthOfYear, dayOfMonth ->
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
                        this.eventDate = c.time
                        if (switch_isYearGiven.isChecked) {
                            edit_date.text = EventDate.parseDateToString(c.time, DateFormat.DEFAULT)
                        } else {
                            edit_date.text =
                                EventDate.getLocalizedDayAndMonth(context!!, c.time)
                        }
                    }
                },
                year,
                month,
                day
            )
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
            Toast.makeText(
                context,
                context!!.resources.getText(R.string.empty_fields_error_annual_event),
                Toast.LENGTH_LONG
            )
                .show()
        } else {

            val annualEvent = AnnualEvent(this.eventDate, name, isYearGiven)

            if (note.isNotBlank()) {
                annualEvent.note = note
            }

            //new annual event entry, just add a new entry in map
            if (!isEditAnnualEvent) {
                EventHandler.addEvent(annualEvent, this.context!!, true)
                Snackbar
                    .make(
                        view!!,
                        context!!.resources.getString(R.string.annual_event_added_notification, name),
                        Snackbar.LENGTH_LONG
                    )
                    .show()
                closeBtnPressed()

                //already annual event entry, overwrite old entry in map
            } else {
                EventHandler.getEventToEventIndex(eventID)?.let { event ->
                    if (event is AnnualEvent && wasChangeMade(event)) {
                        EventHandler.changeEventAt(eventID, annualEvent, context!!, true)
                        Snackbar.make(
                            view!!,
                            context!!.resources.getString(R.string.annual_event_changed_notification, name),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    closeBtnPressed()
                }
            }
        }
    }

    /**
     * wasChangeMade checks wether a change to the edit fields was made or not
     * This is used to avoid unnecessary operations
     * @param event: AnnualEvent, is the comparative object to check against the TextEdit fields
     * @return Boolean, returns false if nothing has changed
     */
    private fun wasChangeMade(event: AnnualEvent): Boolean {
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
         * ANNUAL_EVENT_INSTANCE_FRAGMENT_TAG is the fragments tag as String
         */
        val ANNUAL_EVENT_INSTANCE_FRAGMENT_TAG = "ANNUAL_EVENT_INSTANCE"

        /**
         * newInstance returns a new instance of AnnualEventInstanceFragment
         */
        @JvmStatic
        fun newInstance(): AnnualEventInstanceFragment {
            return AnnualEventInstanceFragment()
        }
    }
}
