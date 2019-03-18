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
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.procrastimax.birthdaybuddy.MainActivity
import com.procrastimax.birthdaybuddy.R
import com.procrastimax.birthdaybuddy.handler.DrawableHandler
import com.procrastimax.birthdaybuddy.handler.EventHandler
import com.procrastimax.birthdaybuddy.models.EventBirthday
import com.procrastimax.birthdaybuddy.models.EventDate
import kotlinx.android.synthetic.main.fragment_add_new_birthday.*
import java.text.DateFormat
import java.util.*


/**
 * TODO:
 *  - move accept/close button in statusbar
 *  - add animations for accept/close  button
 *  - control behaviour when hold in portrait mode
 */
class BirthdayInstanceFragment : Fragment() {

    var isEditedBirthday: Boolean = false
    var itemID = -1
    var birthday_avatar_uri: String? = null
    val REQUEST_IMAGE_GET = 1
    var avatar_img_was_edited = false

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
        if (avatar_img_was_edited) return true

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

        (context as MainActivity).changeToolbarState(MainActivity.Companion.ToolbarState.EditEvent)

        //add image from gallery
        iv_add_avatar_btn.setOnClickListener {
            val view_ = layoutInflater.inflate(R.layout.fragment_bottom_sheet_dialog, null)

            val dialog = BottomSheetDialog(context!!)
            dialog.setContentView(view_)

            val layout_choose_img = dialog.findViewById<ConstraintLayout>(R.id.layout_bottom_sheet_choose)
            val layout_take_new_img = dialog.findViewById<ConstraintLayout>(R.id.layout_bottom_sheet_take_new)
            val layout_delete_img = dialog.findViewById<ConstraintLayout>(R.id.layout_bottom_sheet_delete)

            dialog.show()

            //when clicked, that an image from a file should be taken
            if (layout_choose_img != null) {
                layout_choose_img.setOnClickListener {
                    dialog.hide()
                    getImageFromFiles()
                }
            }

            if (layout_delete_img != null) {
                layout_delete_img.setOnClickListener {
                    dialog.hide()
                    if ((this.birthday_avatar_uri != null) || ((EventHandler.event_list[itemID].second as EventBirthday).avatarImageUri != null)) {
                        this.iv_add_avatar_btn.setImageResource(R.drawable.ic_person_add_img)
                        this.avatar_img_was_edited = true
                        this.birthday_avatar_uri = null
                        DrawableHandler.removeDrawable(EventHandler.event_list[itemID].first)
                    }
                }
            }
        }

        val toolbar = activity!!.findViewById<android.support.v7.widget.Toolbar>(R.id.toolbar)
        val closeBtn = toolbar.findViewById<ImageView>(R.id.btn_edit_event_close)
        val acceptBtn = toolbar.findViewById<ImageView>(R.id.btn_edit_event_accept)
        val title = toolbar.findViewById<TextView>(R.id.tv_add_fragment_title)

        //make delete button invisible/or not
        if (isEditedBirthday) {
            title.text = resources.getText(R.string.toolbar_title_edit_birthday)
            btn_birthday_add_fragment_delete.visibility = Button.VISIBLE
            //delete functionality
            btn_birthday_add_fragment_delete.setOnClickListener {
                val alert_builder = AlertDialog.Builder(context)
                alert_builder.setTitle(resources.getString(R.string.alert_dialog_title_delete_birthday))
                alert_builder.setMessage(resources.getString(R.string.alert_dialog_body_message))

                // Set a positive button and its click listener on alert dialog
                alert_builder.setPositiveButton(resources.getString(R.string.alert_dialog_accept_delete)) { dialog, which ->
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
                alert_builder.setNegativeButton(resources.getString(R.string.alert_dialog_dismiss_delete)) { dialog, which ->
                }

                // Finally, make the alert dialog using builder
                val dialog: AlertDialog = alert_builder.create()

                // Display the alert dialog on app interface
                dialog.show()
            }

            //load maybe already existent avatar photo
            if ((EventHandler.event_list[itemID].second as EventBirthday).avatarImageUri != null) {
                iv_add_avatar_btn.setImageDrawable(DrawableHandler.getDrawableAt((EventHandler.event_list[itemID].first)))
            }

        } else {
            title.text = resources.getText(R.string.toolbar_title_add_birthday)
            btn_birthday_add_fragment_delete.visibility = Button.INVISIBLE
        }

        closeBtn.setOnClickListener {
            closeButtonPressed()
        }

        acceptBtn.setOnClickListener {
            acceptButtonPressed()
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
                    edit_date.hint = context!!.resources.getString(R.string.edit_birthday_date_hint_with_year)
                } else {
                    edit_date.hint = context!!.resources.getString(R.string.edit_birthday_date_hint_without_year)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_GET && resultCode == Activity.RESULT_OK) {
            //val thumbnail: Bitmap = data!!.getParcelableExtra("data")
            val fullPhotoUri: Uri = data!!.data!!

            val take_flags =
                (data.flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION))
            context!!.contentResolver.takePersistableUriPermission(fullPhotoUri, take_flags)

            //TODO: dont run bmp loading on ui thread
            val bitmap = MediaStore.Images.Media.getBitmap(context!!.contentResolver, fullPhotoUri)
            this.iv_add_avatar_btn.setImageDrawable(DrawableHandler.getCircularDrawable(bitmap, resources))

            birthday_avatar_uri = fullPhotoUri.toString()
            avatar_img_was_edited = true
        }
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

            //create new instance from edit fields
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

            if (birthday_avatar_uri != null) {
                birthday.avatarImageUri = birthday_avatar_uri
            }

            //new bithday entry, just add a new entry in map
            if (!isEditedBirthday) {
                EventHandler.addEvent(birthday, context!!, true)
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
