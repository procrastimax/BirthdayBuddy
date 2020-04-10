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
     * isEditAnnualEvent is a boolean flag to indicate whether this fragment is intended to edit or add an instance of AnnualEvent
     * this is later used to fill TextEdits with existing data of an AnnualEvent instance
     */
    private var isEditAnnualEvent = false

    /**
     * eventID is the id the AnnualEvent has in the EventHandler - EventList
     * In other words this id is the index of the clicked item from the EventListFragment RecyclerView
     */
    var eventID = -1

    /**
     * edit_name is the TextEdit used for editing/ showing the name of the annual event
     * It is lazy initialized
     */
    private val editName: EditText by lazy {
        view!!.findViewById<EditText>(R.id.edit_add_fragment_name_annual_event)
    }

    /**
     * editDate is the TextEdit used for editing/ showing the date of the annual_event
     * It is lazy initialized
     */
    private val editDate: TextView by lazy {
        view!!.findViewById<TextView>(R.id.edit_add_fragment_date_annual_event)
    }

    /**
     * edit_note is the TextEdit used for editing/ showing the note of the annual_event
     * It is lazy initialized
     */
    private val editNote: EditText by lazy {
        view!!.findViewById<EditText>(R.id.edit_add_fragment_note_annual_event)
    }

    /**
     * switch_isYearGiven is the Switch to indicate whether the user wants to provide a date with a year or without a year
     * It is lazy initialized
     */
    private val switchIsYearGiven: Switch by lazy {
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

        editName.hint =
            "${context?.getText(R.string.edit_annual_event_name_hint)} ${context?.getText(R.string.necessary)}"

        //retrieve fragment parameter when edited instance
        if (arguments != null) {
            isEditAnnualEvent = true

            setToolbarTitle(context!!.resources.getString(R.string.toolbar_title_edit_annual_event))

            eventID = (arguments!!.getInt(MainActivity.FRAGMENT_EXTRA_TITLE_EVENTID))
            EventHandler.getEventToEventIndex(eventID)?.let { annualEvent ->
                if (annualEvent is AnnualEvent) {

                    this.eventDate = annualEvent.eventDate

                    if (annualEvent.hasStartYear) {
                        editDate.text =
                            EventDate.getLocalizedDayMonthYearString(this.eventDate)
                    } else {
                        editDate.text = EventDate.getLocalizedDayAndMonthString(this.eventDate)
                    }

                    editName.setText(annualEvent.name)
                    if (!annualEvent.note.isNullOrBlank()) {
                        editNote.setText(annualEvent.note)
                    }
                    switchIsYearGiven.isChecked = annualEvent.hasStartYear

                    btn_fragment_annual_event_instance_delete.visibility = Button.VISIBLE
                    btn_fragment_annual_event_instance_delete.setOnClickListener {

                        val alertBuilder = AlertDialog.Builder(context)
                        alertBuilder.setTitle(resources.getString(R.string.btn_annual_event_delete))
                        alertBuilder.setMessage(resources.getString(R.string.alert_dialog_body_message_annual_event))

                        val contextTemp = context

                        // Set a positive button and its click listener on alert dialog
                        alertBuilder.setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                            // delete annual_event on positive button
                            Snackbar
                                .make(
                                    view,
                                    resources.getString(
                                        R.string.annual_event_deleted_notification,
                                        editName.text
                                    ),
                                    Snackbar.LENGTH_LONG
                                )
                                .setAction(R.string.undo) {
                                    EventHandler.addEvent(
                                        annualEvent, contextTemp!!,
                                        true
                                    )
                                    //get last fragment in stack list, when its eventlistfragment, we can update the recycler view
                                    val fragment =
                                        (contextTemp as MainActivity).supportFragmentManager.fragments.last()
                                    if (fragment is EventListFragment) {
                                        fragment.recyclerView.adapter!!.notifyDataSetChanged()
                                        fragment.tv_no_events.visibility = TextView.GONE
                                    }
                                }
                                .show()

                            EventHandler.removeEventByID(eventID, contextTemp!!, true)
                            closeBtnPressed()
                        }
                        alertBuilder.setNegativeButton(resources.getString(R.string.no)) { dialog, _ -> dialog.dismiss() }
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
            editDate.hint = EventDate.getLocalizedDayMonthYearString(this.eventDate)
        }

        editDate.setOnClickListener {
            showDatePickerDialog()
        }

        switchIsYearGiven.setOnCheckedChangeListener { _, isChecked ->
            if (editDate.text.isNotBlank()) {
                //year is given
                if (isChecked) {
                    val cal = Calendar.getInstance()
                    if (this.eventDate.after(cal.time)) {
                        cal.time = this.eventDate
                        cal.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) - 1)
                        this.eventDate = cal.time
                    }

                    editDate.text = EventDate.getLocalizedDayMonthYearString(this.eventDate)
                    //year is not given
                } else {
                    editDate.text = EventDate.getLocalizedDayAndMonthString(this.eventDate)
                }
            } else {
                if (isChecked) {
                    editDate.hint = EventDate.getLocalizedDayMonthYearString(this.eventDate)

                } else {
                    editDate.hint = EventDate.getLocalizedDayAndMonthString(this.eventDate)
                }
            }
        }
    }

    /**
     * showDatePickerDialog shows a standard android date picker dialog
     * The chosen date in the dialog is set to the editDate field
     */
    private fun showDatePickerDialog() {
        val c = Calendar.getInstance()
        //set calendar to the date which is stored in the edit field, when the edit is not empty
        if (!editDate.text.isNullOrBlank()) {
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

                    if (c.time.after(Calendar.getInstance().time) && switchIsYearGiven.isChecked) {
                        Toast.makeText(
                            view.context,
                            context!!.resources.getText(R.string.future_annual_event_error),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        this.eventDate = c.time
                        if (switchIsYearGiven.isChecked) {
                            editDate.text = EventDate.getLocalizedDayMonthYearString(this.eventDate)
                        } else {
                            editDate.text =
                                EventDate.getLocalizedDayAndMonthString(this.eventDate)
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
        val name = editName.text.toString()
        val date = editDate.text.toString()
        val note = editNote.text.toString()
        val isYearGiven = switchIsYearGiven.isChecked

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
                        context!!.resources.getString(
                            R.string.annual_event_added_notification,
                            name
                        ),
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
                            context!!.resources.getString(
                                R.string.annual_event_changed_notification,
                                name
                            ),
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
        if (switchIsYearGiven.isChecked) {
            if (editDate.text != event.dateToPrettyString(DateFormat.FULL)) return true
        } else {
            if (editDate.text != event.dateToPrettyString(DateFormat.DATE_FIELD).subSequence(0..5)
                    .toString()
            ) return true
        }

        if (editNote.text.isNotBlank() && event.note == null) {
            return true
        } else {
            if (event.note != null) {
                if (editNote.text.toString() != event.note!!) return true
            }
        }
        if (editName.text.toString() != event.name) return true
        if (switchIsYearGiven.isChecked != event.hasStartYear) return true
        //if nothing has changed return false
        return false
    }

    companion object {
        /**
         * ANNUAL_EVENT_INSTANCE_FRAGMENT_TAG is the fragments tag as String
         */
        const val ANNUAL_EVENT_INSTANCE_FRAGMENT_TAG = "ANNUAL_EVENT_INSTANCE"

        /**
         * newInstance returns a new instance of AnnualEventInstanceFragment
         */
        @JvmStatic
        fun newInstance(): AnnualEventInstanceFragment {
            return AnnualEventInstanceFragment()
        }
    }
}
