package com.procrastimax.birthdaybuddy.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.procrastimax.birthdaybuddy.MainActivity
import com.procrastimax.birthdaybuddy.R
import com.procrastimax.birthdaybuddy.handler.BitmapHandler
import com.procrastimax.birthdaybuddy.handler.EventHandler
import com.procrastimax.birthdaybuddy.models.EventBirthday
import com.procrastimax.birthdaybuddy.models.EventDate
import kotlinx.android.synthetic.main.content_main.*
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
 *  - control behaviour when hold in portrait mode
 *
 *  - add possibility to take new pictures with camera
 */
class BirthdayInstanceFragment : EventInstanceFragment() {

    /**
     * isEditedBirthday is a boolean flag to indicate wether this fragment is in "edit" mode aka. the user wants to edit an existing instance of EventBirthday
     */
    var isEditedBirthday: Boolean = false

    /**
     * eventID is the index of the clicked item in EventListFragments recyclerview, this is handy to get the birthday instance from the EventHandler
     */
    var eventID = -1

    /**
     * birthday_avatar_uri is a string to store the user picked image for the avatar
     */
    var birthday_avatar_uri: String? = null

    /**
     * avatar_img_was_edited is a boolean flag to store the information wether the avatar img has been changed
     */
    var avatar_img_was_edited = false

    /**
     * REQUEST_IMAGE_GET is an intent code used for open the photo gallery
     */
    val REQUEST_IMAGE_GET = 1

    /**
     * edit_forename is the TextEdit used for editing/ showing the forename of the birthday
     * It is lazy initialized
     */
    val edit_forename: EditText by lazy {
        view!!.findViewById<EditText>(R.id.edit_add_fragment_forename)
    }

    /**
     * edit_nickname is the TextEdit used for editing/ showing the nickname of the birthday
     * It is lazy initialized
     */
    val edit_nickname: EditText by lazy {
        view!!.findViewById<EditText>(R.id.edit_add_fragment_nickname)
    }

    /**
     * edit_surname is the TextEdit used for editing/ showing the surname of the birthday
     * It is lazy initialized
     */
    val edit_surname: EditText by lazy {
        view!!.findViewById<EditText>(R.id.edit_add_fragment_surname)
    }

    /**
     * edit_date is the TextEdit used for editing/ showing the date of the birthday
     * It is lazy initialized
     */
    val edit_date: TextView by lazy {
        view!!.findViewById<TextView>(R.id.edit_add_fragment_date)
    }

    /**
     * edit_note is the TextEdit used for editing/ showing the note of the birthday
     * It is lazy initialized
     */
    val edit_note: EditText by lazy {
        view!!.findViewById<EditText>(R.id.edit_add_fragment_note)
    }

    /**
     * switch_isYearGiven is the Switch to indicate wether the user wants to provide a date with a year or without a year
     * It is lazy initialized
     */
    val switch_isYearGiven: Switch by lazy {
        view!!.findViewById<Switch>(R.id.sw_is_year_given)
    }

    /**
     * wasChangeMade checks wether a change to the edit fields was made or not
     * This is used to avoid unnecessary operations
     * @param event: EventBirthday, is the comparative object to check against the TextEdit fields
     * @return Boolean, returns false if nothing has changed
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

        if (edit_nickname.text.isNotBlank() && event.nickname == null) {
            return true
        } else {
            if (event.nickname != null) {
                if (edit_nickname.text.toString() != event.nickname!!) return true
            }
        }

        if (edit_forename.text.toString() != event.forename) return true
        if (edit_surname.text.toString() != event.surname) return true
        if (switch_isYearGiven.isChecked != event.isYearGiven) return true
        if (avatar_img_was_edited) return true

        return false
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
        (context as MainActivity).setSupportActionBar(toolbar)
        setHasOptionsMenu(false)

        //retrieve fragment parameter when edited instance
        if (arguments != null) {
            isEditedBirthday = true

            setToolbarTitle(context!!.resources.getString(R.string.toolbar_title_edit_birthday))

            eventID = (arguments!!.getInt(ITEM_ID_PARAM_EVENTID))
            EventHandler.getEventToEventIndex(eventID)?.let { birthday ->
                if (birthday is EventBirthday) {
                    if (birthday.isYearGiven) {
                        edit_date.text = EventDate.parseDateToString(birthday.eventDate, DateFormat.FULL)
                    } else {
                        edit_date.text =
                            EventDate.parseDateToString(birthday.eventDate, DateFormat.DATE_FIELD).substring(0..5)
                    }

                    edit_surname.setText(birthday.surname)
                    edit_forename.setText(birthday.forename)
                    switch_isYearGiven.isChecked = birthday.isYearGiven
                    birthday_avatar_uri = birthday.avatarImageUri

                    if (!birthday.note.isNullOrBlank()) {
                        edit_note.setText(birthday.note)
                    }

                    if (!birthday.nickname.isNullOrBlank()) {
                        //cb_nickname.isChecked = true
                        edit_nickname.setText(birthday.nickname)
                        edit_nickname.visibility = EditText.VISIBLE
                    }

                    //title.text = resources.getText(R.string.toolbar_title_edit_birthday)
                    btn_birthday_add_fragment_delete.visibility = Button.VISIBLE
                    //delete functionality
                    btn_birthday_add_fragment_delete.setOnClickListener {
                        val alert_builder = AlertDialog.Builder(context)
                        alert_builder.setTitle(resources.getString(R.string.alert_dialog_title_delete_birthday))
                        alert_builder.setMessage(resources.getString(R.string.alert_dialog_body_message))

                        val context_temp = context
                        val birthday_pair_temp = birthday

                        // Set a positive button and its click listener on alert dialog
                        alert_builder.setPositiveButton(resources.getString(R.string.alert_dialog_accept_delete)) { _, _ ->
                            // delete birthday on positive button
                            Snackbar.make(
                                view,
                                resources.getString(R.string.person_deleted_notification, edit_forename.text),
                                Snackbar.LENGTH_LONG
                            )
                                .setAction(R.string.snackbar_undo_action_title, View.OnClickListener {
                                    EventHandler.addEvent(birthday_pair_temp, context_temp!!, true)
                                    //get last fragment in stack list, which should be eventlistfragment, so we can update the recycler view
                                    val fragment =
                                        (context_temp as MainActivity).supportFragmentManager.fragments[(context_temp).supportFragmentManager.backStackEntryCount]
                                    if (fragment is EventListFragment) {
                                        fragment.recyclerView.adapter!!.notifyDataSetChanged()
                                    }
                                })
                                .show()
                            EventHandler.removeEventByID(eventID, context!!, true)
                            closeBtnPressed()
                        }

                        // dont do anything on negative button
                        alert_builder.setNegativeButton(resources.getString(R.string.alert_dialog_dismiss_delete)) { _, _ ->
                        }

                        // Finally, make the alert dialog using builder
                        val dialog: AlertDialog = alert_builder.create()

                        // Display the alert dialog on app interface
                        dialog.show()
                    }

                    if ((context as MainActivity).isLoading) {
                        this.iv_add_avatar_btn.setImageResource(R.drawable.ic_birthday_person)
                        this.iv_add_avatar_btn.isEnabled = false
                    } else {
                        this.updateAvatarImage()
                    }
                }
            }

            //new birthday is going to be added
        } else {
            setToolbarTitle(context!!.resources.getString(R.string.toolbar_title_add_birthday))
            btn_birthday_add_fragment_delete.visibility = Button.INVISIBLE
            (context as MainActivity).progress_bar_main.visibility = ProgressBar.GONE
            edit_date.hint = resources.getString(
                R.string.birthday_instance_fragment_date_edit_hint,
                EventDate.parseDateToString(Calendar.getInstance().time, DateFormat.FULL)
            )
        }

        edit_date.setOnClickListener {
            showDatePickerDialog()
        }

        //add image from gallery
        this.frame_layout_add_avatar_image.setOnClickListener {
            val bottomSheetDialog = layoutInflater.inflate(R.layout.fragment_bottom_sheet_dialog, null)

            val dialog = BottomSheetDialog(context!!)
            dialog.setContentView(bottomSheetDialog)

            val layoutChooseImg = dialog.findViewById<ConstraintLayout>(R.id.layout_bottom_sheet_choose)
            val layoutDeleteImg = dialog.findViewById<ConstraintLayout>(R.id.layout_bottom_sheet_delete)

            dialog.show()

            //when clicked, that an image from a file should be taken
            if (layoutChooseImg != null) {
                layoutChooseImg.setOnClickListener {
                    dialog.dismiss()
                    getImageFromFiles()
                }
            }

            //delete current image, and reference to BitmapHandler when clicked
            if (layoutDeleteImg != null) {
                layoutDeleteImg.setOnClickListener {
                    dialog.dismiss()
                    if (isEditedBirthday && this.birthday_avatar_uri != null && (EventHandler.getEventToEventIndex(
                            eventID
                        ) as EventBirthday).avatarImageUri != null
                    ) {
                        this.iv_add_avatar_btn.setImageResource(R.drawable.ic_birthday_person)
                        this.avatar_img_was_edited = true
                        this.birthday_avatar_uri = null
                        BitmapHandler.removeBitmap(eventID, context!!)
                    } else {
                        this.iv_add_avatar_btn.setImageResource(R.drawable.ic_birthday_person)
                        this.birthday_avatar_uri = null
                    }
                }
            }
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
                    edit_date.text =
                        EventDate.parseDateToString(date, DateFormat.DATE_FIELD).substring(0..5)
                }
            } else {
                if (isChecked) {
                    edit_date.hint = resources.getString(
                        R.string.birthday_instance_fragment_date_edit_hint,
                        EventDate.parseDateToString(Calendar.getInstance().time, DateFormat.FULL)
                    )
                } else {
                    edit_date.hint =
                        resources.getString(
                            R.string.birthday_instance_fragment_date_edit_hint,
                            EventDate.parseDateToString(
                                Calendar.getInstance().time,
                                DateFormat.DATE_FIELD
                            ).substring(0..5)
                        )
                }
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
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                    or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
        )
        if (intent.resolveActivity(context!!.packageManager) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET)
        }
        return "0"
    }

    /**
     * onActivityResult is the result of the gallery intent from above, here the uri of the photo is processed
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //handle image/photo file choosing
        if (requestCode == REQUEST_IMAGE_GET && resultCode == Activity.RESULT_OK) {
            val fullPhotoUri: Uri = data!!.data!!

            val takeFlags =
                (data.flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION))
            context!!.contentResolver.takePersistableUriPermission(fullPhotoUri, takeFlags)

            Thread(Runnable {
                val bitmap = MediaStore.Images.Media.getBitmap(context!!.contentResolver, fullPhotoUri)
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

            birthday_avatar_uri = fullPhotoUri.toString()
            avatar_img_was_edited = true
        }
    }

    /**
     * acceptBtnPressed is a function which is called when the toolbars accept button is pressed
     */
    override fun acceptBtnPressed() {
        val forename = edit_forename.text.toString()
        val surname = edit_surname.text.toString()
        val date = edit_date.text.toString()
        val note = edit_note.text.toString()
        val nickname = edit_nickname.text.toString()
        val isYearGiven = switch_isYearGiven.isChecked

        if (forename.isBlank() || surname.isBlank() || date.isBlank()) {
            Toast.makeText(
                context,
                context!!.resources.getText(R.string.empty_fields_error_birthday),
                Toast.LENGTH_LONG
            )
                .show()
        } else {

            //create new instance from edit fields
            val birthday: EventBirthday
            if (switch_isYearGiven.isChecked) {
                birthday =
                    EventBirthday(
                        EventDate.parseStringToDate(date, DateFormat.FULL),
                        forename,
                        surname,
                        isYearGiven
                    )
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

            if (nickname.isNotBlank()) {
                birthday.nickname = nickname
            } else {
                birthday.nickname = null
                edit_nickname.text.clear()
            }

            if (birthday_avatar_uri != null) {
                birthday.avatarImageUri = birthday_avatar_uri
            }

            //new bithday entry, just add a new entry in map
            if (!isEditedBirthday) {
                EventHandler.addEvent(birthday, this.context!!, true)
                Snackbar.make(
                    view!!,
                    context!!.resources.getString(R.string.person_added_notification, forename),
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
                            context!!.resources.getString(R.string.person_changed_notification, forename),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    closeBtnPressed()
                }
            }
        }
    }

    fun updateAvatarImage() {
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
     * showDatePickerDialog shows a dialog to let the user pick a date for the edit_date
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

                        if (switch_isYearGiven.isChecked) {
                            edit_date.text = EventDate.parseDateToString(c.time, DateFormat.FULL)
                        } else {
                            edit_date.text =
                                EventDate.parseDateToString(c.time, DateFormat.DATE_FIELD).substring(0..5)
                        }
                    }
                },
                year,
                month,
                day
            )
        dpd.show()
    }

    companion object {
        /**
         * BIRTHDAY_INSTANCE_FRAGMENT_TAG is the fragments tag as String
         */
        val BIRTHDAY_INSTANCE_FRAGMENT_TAG = "BIRTHDAY_INSTANCE"

        /**
         * newInstance returns a new instance of BirthdayInstanceFragment
         */
        @JvmStatic
        fun newInstance(): BirthdayInstanceFragment {
            return BirthdayInstanceFragment()
        }
    }
}
