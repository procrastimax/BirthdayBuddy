package com.procrastimax.birthdaybuddy.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.procrastimax.birthdaybuddy.MainActivity
import com.procrastimax.birthdaybuddy.R
import com.procrastimax.birthdaybuddy.handler.BitmapHandler
import com.procrastimax.birthdaybuddy.handler.EventHandler
import com.procrastimax.birthdaybuddy.handler.IOHandler
import com.procrastimax.birthdaybuddy.models.EventBirthday
import com.procrastimax.birthdaybuddy.models.EventDate
import kotlinx.android.synthetic.main.fragment_add_new_birthday.*
import kotlinx.android.synthetic.main.fragment_event_list.*
import java.text.DateFormat
import java.util.*


/**
 *
 * BirthdayInstanceFragment is a fragment class for adding/editing an instance of EventBirthday
 * This fragment shows up, when the users wants to add a new EventBirthday or edit an existing one
 * The fragment consists of several TextEdits to manage user data input
 *
 * This class inherits from android.support.v4.app.Fragment
 *
 * TODO:
 *  - add possibility to take new pictures with camera
 */
class BirthdayInstanceFragment : EventInstanceFragment() {

    /**
     * isEditedBirthday is a boolean flag to indicate whether this fragment is in "edit" mode aka. the user wants to edit an existing instance of EventBirthday
     */
    private var isEditedBirthday: Boolean = false

    /**
     * eventID is the index of the clicked item in EventListFragments RecyclerView, this is handy to get the birthday instance from the EventHandler
     */
    var eventID = -1

    /**
     * birthdayAvatarUri is a string to store the user picked image for the avatar
     */
    private var birthdayAvatarUri: String? = null

    /**
     * avatarImgWasEdited is a boolean flag to store the information whether the avatar img has been changed
     */
    private var avatarImgWasEdited = false

    /**
     * REQUEST_IMAGE_GET is an intent code used for open the photo gallery
     */
    private val REQUEST_IMAGE_GET = 1

    /**
     * editForename is the TextEdit used for editing/ showing the forename of the birthday
     * It is lazy initialized
     */
    private val editForename: EditText by lazy {
        view!!.findViewById<EditText>(R.id.edit_add_fragment_forename)
    }

    /**
     * editNickname is the TextEdit used for editing/ showing the nickname of the birthday
     * It is lazy initialized
     */
    private val editNickname: EditText by lazy {
        view!!.findViewById<EditText>(R.id.edit_add_fragment_nickname)
    }

    /**
     * editSurname is the TextEdit used for editing/ showing the surname of the birthday
     * It is lazy initialized
     */
    private val editSurname: EditText by lazy {
        view!!.findViewById<EditText>(R.id.edit_add_fragment_surname)
    }

    /**
     * editDateCalendarview is the TextEdit used for editing/ showing the date of the birthday but reprensented by the android calendar view
     * It is lazy initialized
     */
    private val editDateCalendarview: TextView by lazy {
        view!!.findViewById<TextView>(R.id.edit_add_fragment_date_calendarview)
    }

    /**
     * editDate is the TextEdit used for editing/ showing the date of the birthday
     * It is lazy initialized
     */
    private val editDate: TextView by lazy {
        view!!.findViewById<TextView>(R.id.edit_add_fragment_date)
    }

    /**
     * editNote is the TextEdit used for editing/ showing the note of the birthday
     * It is lazy initialized
     */
    private val editNote: EditText by lazy {
        view!!.findViewById<EditText>(R.id.edit_add_fragment_note)
    }

    private val dateEditRegexNoYear = Regex("""\d\d\W\d\d\W""")
    private val dateEditRegexWithYear = Regex("""\d\d\W\d\d\W\d\d\d\d""")

    /**
     * switchIsYearGiven is the Switch to indicate wether the user wants to provide a date with a year or without a year
     * It is lazy initialized
     */
    private val switchIsYearGiven: Switch by lazy {
        view!!.findViewById<Switch>(R.id.sw_is_year_given)
    }

    private var isCalendarViewSelected: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_new_birthday, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(false)

        if (IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_date_as_calendar_view) == false) {
            isCalendarViewSelected = false
            editDateCalendarview.visibility = EditText.INVISIBLE
            editDate.visibility = EditText.VISIBLE

        } else {
            isCalendarViewSelected = true
            editDateCalendarview.visibility = EditText.VISIBLE
            editDate.visibility = EditText.INVISIBLE
        }

        editForename.hint =
            "${context?.getText(R.string.event_property_forename)} ${context?.getText(R.string.necessary)}"

        //retrieve fragment parameter when edited instance
        if (arguments != null) {
            isEditedBirthday = true

            setToolbarTitle(context!!.resources.getString(R.string.toolbar_title_edit_birthday))

            eventID = (arguments!!.getInt(MainActivity.FRAGMENT_EXTRA_TITLE_EVENTID))
            EventHandler.getEventToEventIndex(eventID)?.let { birthday ->
                if (birthday is EventBirthday) {
                    this.eventDate = birthday.eventDate
                    if (this.eventDate.after(Calendar.getInstance().time)) {
                        val cal = Calendar.getInstance()
                        cal.time = this.eventDate
                        cal.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) - 1)
                        this.eventDate = cal.time
                    }

                    // the value which should be assigned to the date edit box
                    val startDate: String?

                    if (birthday.isYearGiven) {
                        startDate =
                            EventDate.getLocalizedDayMonthYear(this.eventDate)
                    } else {
                        startDate =
                            EventDate.getLocalizedDayAndMonth(this.eventDate)
                    }

                    if (!isCalendarViewSelected) {
                        editDate.text = startDate
                        editDate.hint = startDate
                    } else {
                        editDateCalendarview.text = startDate
                        editDateCalendarview.hint = startDate
                    }

                    editSurname.setText(birthday.surname)
                    editForename.setText(birthday.forename)
                    switchIsYearGiven.isChecked = birthday.isYearGiven
                    birthdayAvatarUri = birthday.avatarImageUri

                    if (!birthday.note.isNullOrBlank()) {
                        editNote.setText(birthday.note)
                    }

                    if (!birthday.nickname.isNullOrBlank()) {
                        //cb_nickname.isChecked = true
                        editNickname.setText(birthday.nickname)
                        editNickname.visibility = EditText.VISIBLE
                    }

                    //title.text = resources.getText(R.string.toolbar_title_edit_birthday)
                    btn_birthday_add_fragment_delete.visibility = Button.VISIBLE
                    //delete functionality
                    btn_birthday_add_fragment_delete.setOnClickListener {
                        val alertBuilder = AlertDialog.Builder(context)
                        alertBuilder.setTitle(resources.getString(R.string.alert_dialog_title_delete_birthday))
                        alertBuilder.setMessage(resources.getString(R.string.alert_dialog_body_message))

                        val contextTemp = context

                        // Set a positive button and its click listener on alert dialog
                        alertBuilder.setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                            // delete birthday on positive button
                            Snackbar
                                .make(
                                    view,
                                    resources.getString(R.string.person_deleted_notification),
                                    Snackbar.LENGTH_LONG
                                )
                                .setAction(R.string.undo) {
                                    EventHandler.addEvent(birthday, contextTemp!!, true)
                                    //get last fragment in stack list, which should be EventListFragment, so we can update the recycler view
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

                        // don't do anything on negative button
                        alertBuilder.setNegativeButton(resources.getString(R.string.no)) { _, _ ->
                        }

                        // Finally, make the alert dialog using builder
                        val dialog: AlertDialog = alertBuilder.create()

                        // Display the alert dialog on app interface
                        dialog.show()
                    }
                }

                this.updateAvatarImage()
            }
            //new birthday is going to be added
        } else {
            setToolbarTitle(context!!.resources.getString(R.string.toolbar_title_add_birthday))
            btn_birthday_add_fragment_delete.visibility = Button.INVISIBLE

            if (isCalendarViewSelected) {
                editDateCalendarview.hint = EventDate.getLocalizedDayMonthYear(this.eventDate)
            } else {
                editDate.hint = EventDate.getLocalizedDayMonthYear(this.eventDate)
            }
        }

        editDate.setOnFocusChangeListener { editTextView, hasFocus ->
            if (!hasFocus) {
                if (!validateAndSetEditTextDateInput((editTextView as EditText).text.toString())) {
                    editTextView.setTextColor(Color.RED)
                } else {
                    editDate.setTextColor(
                        ContextCompat.getColor(
                            context!!,
                            R.color.textVeryDark
                        )
                    )
                }
            }
        }

        editDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    if (switchIsYearGiven.isChecked) {
                        if (!dateEditRegexWithYear.matches(s)) {
                            editDate.setTextColor(Color.RED)
                        } else {
                            editDate.setTextColor(
                                ContextCompat.getColor(
                                    context!!,
                                    R.color.textVeryDark
                                )
                            )
                        }
                    } else {
                        if (!dateEditRegexNoYear.matches(s)) {
                            editDate.setTextColor(Color.RED)
                        } else {
                            editDate.setTextColor(
                                ContextCompat.getColor(
                                    context!!,
                                    R.color.textVeryDark
                                )
                            )
                        }
                    }
                }
            }
        })

        editDateCalendarview.setOnClickListener {
            showDatePickerDialog(switchIsYearGiven.isChecked)
        }

        //add image from gallery
        this.frame_layout_add_avatar_image.setOnClickListener {
            val bottomSheetDialog =
                layoutInflater.inflate(R.layout.fragment_bottom_sheet_dialog, null)

            val dialog = BottomSheetDialog(context!!)
            dialog.setContentView(bottomSheetDialog)

            dialog.findViewById<ConstraintLayout>(R.id.layout_bottom_sheet_choose).apply {
                this?.setOnClickListener {
                    dialog.dismiss()
                    getImageFromFiles()
                }
            }

            dialog.findViewById<ConstraintLayout>(R.id.layout_bottom_sheet_delete).apply {
                this?.setOnClickListener {
                    dialog.dismiss()
                    if (isEditedBirthday && birthdayAvatarUri != null && (EventHandler.getEventToEventIndex(
                            eventID
                        ) as EventBirthday).avatarImageUri != null
                    ) {
                        iv_add_avatar_btn.setImageResource(R.drawable.ic_birthday_person)
                        avatarImgWasEdited = true
                        birthdayAvatarUri = null
                        BitmapHandler.removeBitmap(eventID, context!!)
                    } else {
                        iv_add_avatar_btn.setImageResource(R.drawable.ic_birthday_person)
                        birthdayAvatarUri = null
                    }
                }
            }

            dialog.show()
        }

        switchIsYearGiven.setOnCheckedChangeListener { _, isChecked ->
            var dateText: String
            var dateHint: String
            //year is given
            if (isChecked) {
                val cal = Calendar.getInstance()
                if (this.eventDate.after(cal.time)) {
                    cal.time = this.eventDate
                    cal.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) - 1)
                    this.eventDate = cal.time
                }

                dateText =
                    EventDate.getLocalizedDayMonthYear(this.eventDate)
                dateHint =
                    EventDate.getLocalizedDayMonthYear(Calendar.getInstance().time)

                //expand existing date string in dateEdit with year
                if (!isCalendarViewSelected) {
                    if (dateEditRegexNoYear.matches(editDate.text)) {
                        dateText =
                            editDate.text.toString() + Calendar.getInstance().get(Calendar.YEAR).toString()
                    }
                }

                //year is not given
            } else {
                dateText = EventDate.getLocalizedDayAndMonth(this.eventDate)
                dateHint = EventDate.getLocalizedDayAndMonth(Calendar.getInstance().time)

                //truncate existing date string in dateEdit
                if (!isCalendarViewSelected) {
                    if (dateEditRegexWithYear.matches(editDate.text)) {
                        dateText = editDate.text.substring(0..5)
                    }
                }
            }

            if (isCalendarViewSelected) {
                if (editDate.text.isNotBlank()) editDateCalendarview.text = dateText
                editDateCalendarview.hint = dateHint
            } else {
                if (editDate.text.isNotBlank()) editDate.text = dateText
                editDate.hint = dateHint
            }
        }
    }

    /**
     * getImageFromFiles opens an intent to request a photo from the gallery
     * This function is called after the user clicks on the iv_add_avatar_btn
     */
    private fun getImageFromFiles(): String {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
        }
        intent.addFlags(
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
        if (intent.resolveActivity(context!!.packageManager) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET)
        }
        return "0"
    }

    /**
     * onActivityResult is the result of the gallery intent, here the uri of the photo is processed
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //handle image/photo file choosing
        if (requestCode == REQUEST_IMAGE_GET && resultCode == Activity.RESULT_OK) {
            val fullPhotoUri: Uri = data!!.data!!

            Thread(Runnable {
                val bitmap =
                    MediaStore.Images.Media.getBitmap(context!!.contentResolver, fullPhotoUri)
                (context as MainActivity).runOnUiThread {
                    iv_add_avatar_btn.setImageBitmap(
                        BitmapHandler.getCircularBitmap(
                            BitmapHandler.getScaledBitmap(
                                bitmap
                            ), resources
                        )
                    )
                }
            }).start()

            birthdayAvatarUri = fullPhotoUri.toString()
            avatarImgWasEdited = true
        }
    }

    /**
     * acceptBtnPressed is a function which is called when the toolbars accept button is pressed
     */
    override fun acceptBtnPressed() {
        val forename = editForename.text.toString()
        val surname = editSurname.text.toString()

        val date = if (isCalendarViewSelected) {
            editDateCalendarview.text.toString()
        } else {
            if (!validateAndSetEditTextDateInput(editDate.text.toString())) return
            editDate.text.toString()
        }

        val note = editNote.text.toString()
        val nickname = editNickname.text.toString()
        val isYearGiven = switchIsYearGiven.isChecked

        if (forename.isBlank() || date.isBlank()) {
            Toast.makeText(
                context,
                context!!.resources.getText(R.string.empty_fields_error_birthday),
                Toast.LENGTH_LONG
            )
                .show()
        } else {

            //create new instance from edit fields
            val birthday = EventBirthday(
                this.eventDate,
                forename,
                isYearGiven
            )

            if (surname.isNotBlank()) {
                birthday.surname = surname
            }

            if (note.isNotBlank()) {
                birthday.note = note
            }

            if (nickname.isNotBlank()) {
                birthday.nickname = nickname
            }

            if (birthdayAvatarUri != null) {
                birthday.avatarImageUri = birthdayAvatarUri
            }

            //new bithday entry, just add a new entry in map
            if (!isEditedBirthday) {
                EventHandler.addEvent(birthday, this.context!!, true)
                Snackbar.make(
                    view!!,
                    context!!.resources.getString(R.string.person_added_notification),
                    Snackbar.LENGTH_LONG
                ).show()
                closeBtnPressed()

                //already existent birthday entry, overwrite old entry in map
            } else {
                EventHandler.getEventToEventIndex(eventID)?.let { event ->
                    if (event is EventBirthday && wasChangeMade(event)) {
                        EventHandler.changeEventAt(eventID, birthday, context!!, true)
                        Snackbar.make(
                            view!!,
                            context!!.resources.getString(R.string.person_changed_notification),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    closeBtnPressed()
                }
            }
        }
    }

    private fun updateAvatarImage() {
        if (this.iv_add_avatar_btn != null && this.eventID >= 0) {
            //load maybe already existent avatar photo
            EventHandler.getEventToEventIndex(eventID)?.let { event ->
                if (event is EventBirthday && event.avatarImageUri != null) {
                    this.iv_add_avatar_btn.setImageBitmap(BitmapHandler.getBitmapAt(eventID))
                    this.iv_add_avatar_btn.isEnabled = true
                }
            }
        }
    }

    /**
     * showDatePickerDialog shows a dialog to let the user pick a date for the editDateCalendarview
     */
    private fun showDatePickerDialog(showYear: Boolean) {
        val c = Calendar.getInstance()
        //set calendar to the date which is stored in the edit field, when the edit is not empty
        if (!editDateCalendarview.text.isNullOrBlank()) {
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

                    if (c.time.after(Calendar.getInstance().time) && showYear) {
                        showFutureDateErrorToast(view.context)
                    } else {
                        this.eventDate = c.time
                        if (showYear) {
                            editDateCalendarview.text =
                                EventDate.parseDateToString(c.time, DateFormat.DEFAULT)
                        } else {
                            editDateCalendarview.text =
                                EventDate.getLocalizedDayAndMonth(c.time)
                        }
                    }
                },
                year,
                month,
                day
            )
        dpd.show()
    }

    private fun showFutureDateErrorToast(context: Context) {
        Toast.makeText(
            context,
            context.resources.getText(R.string.future_birthday_error),
            Toast.LENGTH_LONG
        ).show()
    }

    /**
     * wasChangeMade checks whether a change to the edit fields was made or not
     * This is used to avoid unnecessary operations
     * @param event: EventBirthday, is the comparative object to check against the TextEdit fields
     * @return Boolean, returns false if nothing has changed
     */
    private fun wasChangeMade(event: EventBirthday): Boolean {
        if (switchIsYearGiven.isChecked) {
            if (editDateCalendarview.text != event.dateToPrettyString(DateFormat.FULL)) return true
            if (editDate.text != EventDate.getLocalizedDayMonthYear(event.eventDate)) return true
        } else {
            if (editDateCalendarview.text != event.dateToPrettyString(DateFormat.DATE_FIELD).subSequence(
                    0..5
                ).toString()
            ) return true
            if (editDate.text != EventDate.getLocalizedDayAndMonth(event.eventDate)) return true
        }

        if (editNote.text.isNotBlank() && event.note == null) {
            return true
        } else {
            if (event.note != null) {
                if (editNote.text.toString() != event.note!!) return true
            }
        }

        if (editNickname.text.isNotBlank() && event.nickname == null) {
            return true
        } else {
            if (event.nickname != null) {
                if (editNickname.text.toString() != event.nickname!!) return true
            }
        }

        if (editSurname.text.isNotBlank() && event.surname == null) {
            return true
        } else {
            if (event.surname != null) {
                if (editSurname.text.toString() != event.surname!!) return true
            }
        }

        if (editForename.text.toString() != event.forename) return true
        if (switchIsYearGiven.isChecked != event.isYearGiven) return true
        if (avatarImgWasEdited) return true

        return false
    }

    private fun validateAndSetEditTextDateInput(dateInput: String): Boolean {
        if (dateInput.isBlank()) return false
        val dateRegEx: Regex?
        if (switchIsYearGiven.isChecked) {
            dateRegEx = dateEditRegexWithYear
        } else {
            dateRegEx = dateEditRegexNoYear
        }

        // date input does not match the required regex -> show error
        if (!dateInput.matches(dateRegEx)) {
            Toast.makeText(
                context,
                resources.getString(
                    R.string.verification_edit_date_input,
                    editDate.hint.toString()
                ),
                Toast.LENGTH_LONG
            ).show()
            return false
        } else {
            // input matches regex, then set it as birthdayevent date

            val dateValues = dateInput.split("""\W""".toRegex())
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR, 12)
            this.eventDate = if (switchIsYearGiven.isChecked) {
                cal.set(Calendar.DAY_OF_MONTH, dateValues[0].toInt() - 1)
                cal.set(Calendar.MONTH, dateValues[1].toInt() - 1)

                if (dateValues[2].toInt() == 0) {
                    Toast.makeText(context!!, "Man, that's too old.", Toast.LENGTH_SHORT).show()
                    cal.set(Calendar.YEAR, 1)
                } else {
                    cal.set(Calendar.YEAR, dateValues[2].toInt())
                }

                cal.time
            } else {
                cal.set(Calendar.DAY_OF_MONTH, dateValues[0].toInt() - 1)
                cal.set(Calendar.MONTH, dateValues[1].toInt() - 1)
                cal.set(Calendar.YEAR, 2016)

                cal.time
            }
        }
        return true
    }

    companion object {
        /**
         * BIRTHDAY_INSTANCE_FRAGMENT_TAG is the fragments tag as String
         */
        const val BIRTHDAY_INSTANCE_FRAGMENT_TAG = "BIRTHDAY_INSTANCE"

        /**
         * newInstance returns a new instance of BirthdayInstanceFragment
         */
        @JvmStatic
        fun newInstance(): BirthdayInstanceFragment {
            return BirthdayInstanceFragment()
        }
    }
}
