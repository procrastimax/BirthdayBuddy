package com.procrastimax.birthdaybuddy.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import com.procrastimax.birthdaybuddy.MainActivity
import com.procrastimax.birthdaybuddy.R
import com.procrastimax.birthdaybuddy.handler.EventHandler
import com.procrastimax.birthdaybuddy.models.EventBirthday
import com.procrastimax.birthdaybuddy.models.EventDay
import java.time.Year
import java.util.*

/**
 * TODO:
 *  - move accept/close button in statusbar
 */
class AddNewBirthdayFragment : Fragment() {

    val edit_forename: EditText by lazy {
        view!!.findViewById<EditText>(R.id.edit_add_fragment_forename)
    }

    val edit_surname: EditText by lazy {
        view!!.findViewById<EditText>(R.id.edit_add_fragment_surname)
    }

    val edit_date: EditText by lazy {
        view!!.findViewById<EditText>(R.id.edit_add_fragment_date)
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

        val toolbar = activity!!.findViewById<android.support.v7.widget.Toolbar>(R.id.toolbar)

        val closeBtn = toolbar.findViewById<ImageView>(R.id.btn_add_fragment_close)

        closeBtn.setOnClickListener {
            context
            println("pressed")
            closeButtonPressed()
        }

        val acceptBtn = toolbar.findViewById<ImageView>(R.id.btn_add_fragment_accept)
        acceptBtn.setOnClickListener { acceptButtonPressed() }

        // dont show softkeyboard on press
        edit_date.inputType = InputType.TYPE_NULL
        edit_date.setOnClickListener {
            showDatePickerDialog()
        }

        edit_date.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                showDatePickerDialog()
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
                //TODO: dont add future birthdays
                edit_date.setText(EventDay.parseDateToString(c.time))

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
            Toast.makeText(context, "Make sure to fill all empty fields!", Toast.LENGTH_LONG).show()
        } else {
            val birthday = EventBirthday(EventDay.parseStringToDate(date), forename, surname, isYearGiven)
            if (note.isNotBlank()) {
                birthday.note = note
            }
            EventHandler.addEvent(birthday, true)

            //TODO: use resource string, add undo action
            Snackbar.make(view!!, "$forename's Birthday added", Snackbar.LENGTH_LONG).show()
            closeButtonPressed()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): AddNewBirthdayFragment {
            return AddNewBirthdayFragment()
        }
    }
}
