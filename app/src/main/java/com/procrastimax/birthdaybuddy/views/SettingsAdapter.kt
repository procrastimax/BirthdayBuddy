package com.procrastimax.birthdaybuddy.views

import android.Manifest
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Environment
import android.support.constraint.ConstraintLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.procrastimax.birthdaybuddy.MainActivity
import com.procrastimax.birthdaybuddy.R
import com.procrastimax.birthdaybuddy.handler.EventHandler
import com.procrastimax.birthdaybuddy.handler.IOHandler
import com.procrastimax.birthdaybuddy.models.EventDate
import kotlinx.android.synthetic.main.card_view_settings_extras.view.*
import kotlinx.android.synthetic.main.card_view_settings_notification.view.*
import java.util.*

class SettingsAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * itemList is a list of 4 integers to imply the settings card view
     */
    private val itemList = listOf(1, 2, 3, 4)

    class SettingCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    class SettingExtraCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    override fun getItemViewType(position: Int): Int {
        return this.itemList[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            1, 2, 3 -> {
                val cardView =
                    LayoutInflater.from(parent.context).inflate(R.layout.card_view_settings_notification, parent, false)
                return SettingCardViewHolder(cardView)
            }
            4 -> {
                val cardView =
                    LayoutInflater.from(parent.context).inflate(R.layout.card_view_settings_extras, parent, false)
                return SettingExtraCardViewHolder(cardView)
            }
            else -> {
                val cardViewExtraSettings =
                    LayoutInflater.from(parent.context).inflate(R.layout.card_view_settings_notification, parent, false)
                return SettingCardViewHolder(cardViewExtraSettings)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            // BIRTHDAY NOTIFICATION SETTINGS
            1 -> {
                holder.itemView.tv_settings_title.text = context.getText(R.string.event_type_birthday)

                val isEnabled = IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotificationOnBirthday)!!
                if (!isEnabled) changeEnabledStatus(holder.itemView, isEnabled)

                holder.itemView.sw_settings_notifcations.isChecked = isEnabled
                holder.itemView.sw_settings_notifcations.setOnCheckedChangeListener { _, isChecked ->
                    changeEnabledStatus(holder.itemView, isChecked)
                    IOHandler.writeSetting(IOHandler.SharedPrefKeys.key_isNotificationOnBirthday, isChecked)
                }

                //sound switch
                holder.itemView.sw_settings_sound.isChecked =
                    IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotificationSoundOnBirthday)!!
                holder.itemView.sw_settings_sound.setOnCheckedChangeListener { _, isChecked ->
                    IOHandler.writeSetting(IOHandler.SharedPrefKeys.key_isNotificationSoundOnBirthday, isChecked)
                }

                //vibration switch
                holder.itemView.sw_settings_vibration.isChecked =
                    IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotificationVibrationOnBirthday)!!
                holder.itemView.sw_settings_vibration.setOnCheckedChangeListener { _, isChecked ->
                    IOHandler.writeSetting(IOHandler.SharedPrefKeys.key_isNotificationVibrationOnBirthday, isChecked)
                }

                //set notification time
                holder.itemView.tv_settings_notificaton_time_value.text =
                    IOHandler.getStringFromKey(IOHandler.SharedPrefKeys.key_strNotificationTimeBirthday)
                //set time picker dialog on click
                holder.itemView.tv_settings_notificaton_time_value.setOnClickListener {
                    showTimePickerDialog(it as TextView, IOHandler.SharedPrefKeys.key_strNotificationTimeBirthday)
                }

                val notificationDateArray: BooleanArray = booleanArrayOf(
                    IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_month_beforeBirthday)!!,
                    IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_week_beforeBirthday)!!,
                    IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_day_beforeBirthday)!!,
                    IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_eventdayBirthday)!!
                )

                val constrLayoutNotificationDay =
                    holder.itemView.findViewById<ConstraintLayout>(R.id.constrLayout_settings_notification_day)
                //show checkbox dialog on click
                constrLayoutNotificationDay.setOnClickListener {
                    val alertDialogBuilder = AlertDialog.Builder(context)
                    alertDialogBuilder.setTitle(R.string.settings_title_notification_day)
                        .setPositiveButton(R.string.apply) { _, _ ->

                            holder.itemView.tv_settings_notification_day_value.text =
                                getNotificationDateValueStringFromBooleanArray(notificationDateArray)

                            IOHandler.writeSetting(
                                IOHandler.SharedPrefKeys.key_isRemindedDay_month_beforeBirthday,
                                notificationDateArray[0]
                            )
                            IOHandler.writeSetting(
                                IOHandler.SharedPrefKeys.key_isRemindedDay_week_beforeBirthday,
                                notificationDateArray[1]
                            )
                            IOHandler.writeSetting(
                                IOHandler.SharedPrefKeys.key_isRemindedDay_day_beforeBirthday,
                                notificationDateArray[2]
                            )
                            IOHandler.writeSetting(
                                IOHandler.SharedPrefKeys.key_isRemindedDay_eventdayBirthday,
                                notificationDateArray[3]
                            )
                        }
                        .setMultiChoiceItems(
                            arrayOf(
                                context.getText(R.string.tv_notification_interval_month),
                                context.getText(R.string.tv_notification_interval_week),
                                context.getText(R.string.tv_notification_interval_day),
                                context.getText(R.string.tv_notification_interval_eventday)
                            ),
                            notificationDateArray
                        ) { _, which, isChecked ->
                            notificationDateArray[which] = isChecked
                        }
                        .show()
                }

                var notificationLight =
                    IOHandler.getIntFromKey(IOHandler.SharedPrefKeys.key_notificationLightBirthday)!!
                val constrLayoutNotificationLight =
                    holder.itemView.findViewById<ConstraintLayout>(R.id.constraint_layout_settings_notification_light)
                //show checkbox dialog on click
                constrLayoutNotificationLight.setOnClickListener {
                    val alertDialogBuilder = AlertDialog.Builder(context)
                    alertDialogBuilder.setTitle(R.string.dialog_title_notification_light)
                        .setPositiveButton(R.string.apply) { _, _ ->

                            holder.itemView.tv_settings_notification_light_value.text =
                                getNotifcationLightValueFromInt(notificationLight)

                            IOHandler.writeSetting(
                                IOHandler.SharedPrefKeys.key_notificationLightBirthday,
                                notificationLight
                            )
                        }
                        .setSingleChoiceItems(
                            context.resources.getStringArray(R.array.light_modes),
                            notificationLight
                        ) { _: DialogInterface?, which: Int ->
                            notificationLight = which
                        }
                        .show()
                }

                holder.itemView.tv_settings_notification_light_value.text =
                    getNotifcationLightValueFromInt(notificationLight)

                holder.itemView.tv_settings_notification_day_value.text =
                    getNotificationDateValueStringFromBooleanArray(notificationDateArray)
            }

            // ANNUAL NOTIFICATION SETTINGS
            2 -> {
                holder.itemView.tv_settings_title.text = context.getText(R.string.event_type_annual_event)

                val isEnabled = IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotificationOnAnnual)!!
                if (!isEnabled) changeEnabledStatus(holder.itemView, isEnabled)

                holder.itemView.sw_settings_notifcations.isChecked = isEnabled
                holder.itemView.sw_settings_notifcations.setOnCheckedChangeListener { _, isChecked ->
                    changeEnabledStatus(holder.itemView, isChecked)
                    IOHandler.writeSetting(IOHandler.SharedPrefKeys.key_isNotificationOnAnnual, isChecked)
                }

                //sound switch
                holder.itemView.sw_settings_sound.isChecked =
                    IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotificationSoundOnAnnual)!!
                holder.itemView.sw_settings_sound.setOnCheckedChangeListener { _, isChecked ->
                    IOHandler.writeSetting(IOHandler.SharedPrefKeys.key_isNotificationSoundOnAnnual, isChecked)
                }

                //vibration switch
                holder.itemView.sw_settings_vibration.isChecked =
                    IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotificationVibrationOnAnnual)!!
                holder.itemView.sw_settings_vibration.setOnCheckedChangeListener { _, isChecked ->
                    IOHandler.writeSetting(IOHandler.SharedPrefKeys.key_isNotificationVibrationOnAnnual, isChecked)
                }

                //set notification time
                holder.itemView.tv_settings_notificaton_time_value.text =
                    IOHandler.getStringFromKey(IOHandler.SharedPrefKeys.key_strNotificationTimeAnnual)
                //set time picker dialog on click
                holder.itemView.tv_settings_notificaton_time_value.setOnClickListener {
                    showTimePickerDialog(it as TextView, IOHandler.SharedPrefKeys.key_strNotificationTimeAnnual)
                }

                val notificationDateArray: BooleanArray = booleanArrayOf(
                    IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_month_beforeAnnual)!!,
                    IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_week_beforeAnnual)!!,
                    IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_day_beforeAnnual)!!,
                    IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_eventdayAnnual)!!
                )

                val constrLayoutNotificationDay =
                    holder.itemView.findViewById<ConstraintLayout>(R.id.constrLayout_settings_notification_day)
                //show checkbox dialog on click
                constrLayoutNotificationDay.setOnClickListener {
                    val alertDialogBuilder = AlertDialog.Builder(context)
                    alertDialogBuilder.setTitle(R.string.settings_title_notification_day)
                        .setPositiveButton(R.string.apply) { _, _ ->

                            holder.itemView.tv_settings_notification_day_value.text =
                                getNotificationDateValueStringFromBooleanArray(notificationDateArray)

                            IOHandler.writeSetting(
                                IOHandler.SharedPrefKeys.key_isRemindedDay_month_beforeAnnual,
                                notificationDateArray[0]
                            )
                            IOHandler.writeSetting(
                                IOHandler.SharedPrefKeys.key_isRemindedDay_week_beforeAnnual,
                                notificationDateArray[1]
                            )
                            IOHandler.writeSetting(
                                IOHandler.SharedPrefKeys.key_isRemindedDay_day_beforeAnnual,
                                notificationDateArray[2]
                            )
                            IOHandler.writeSetting(
                                IOHandler.SharedPrefKeys.key_isRemindedDay_eventdayAnnual,
                                notificationDateArray[3]
                            )
                        }
                        .setMultiChoiceItems(
                            arrayOf(
                                context.getText(R.string.tv_notification_interval_month),
                                context.getText(R.string.tv_notification_interval_week),
                                context.getText(R.string.tv_notification_interval_day),
                                context.getText(R.string.tv_notification_interval_eventday)
                            ),
                            notificationDateArray
                        ) { _, which, isChecked ->
                            notificationDateArray[which] = isChecked
                        }
                        .show()
                }

                var notificationLight =
                    IOHandler.getIntFromKey(IOHandler.SharedPrefKeys.key_notificationLightAnnual)!!
                val constrLayoutNotificationLight =
                    holder.itemView.findViewById<ConstraintLayout>(R.id.constraint_layout_settings_notification_light)
                //show checkbox dialog on click
                constrLayoutNotificationLight.setOnClickListener {
                    val alertDialogBuilder = AlertDialog.Builder(context)
                    alertDialogBuilder.setTitle(R.string.dialog_title_notification_light)
                        .setPositiveButton(R.string.apply) { _, _ ->

                            holder.itemView.tv_settings_notification_light_value.text =
                                getNotifcationLightValueFromInt(notificationLight)

                            IOHandler.writeSetting(
                                IOHandler.SharedPrefKeys.key_notificationLightAnnual,
                                notificationLight
                            )
                        }
                        .setSingleChoiceItems(
                            context.resources.getStringArray(R.array.light_modes),
                            notificationLight
                        ) { _: DialogInterface?, which: Int ->
                            notificationLight = which
                        }
                        .show()
                }

                holder.itemView.tv_settings_notification_light_value.text =
                    getNotifcationLightValueFromInt(notificationLight)

                holder.itemView.tv_settings_notification_day_value.text =
                    getNotificationDateValueStringFromBooleanArray(notificationDateArray)
            }

            // ONE-TIME NOTIFICATION SETTINGS
            3 -> {
                holder.itemView.tv_settings_title.text = context.getText(R.string.event_type_one_time_event)

                val isEnabled = IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotificationOnOneTime)!!
                if (!isEnabled) changeEnabledStatus(holder.itemView, isEnabled)

                holder.itemView.sw_settings_notifcations.isChecked = isEnabled
                holder.itemView.sw_settings_notifcations.setOnCheckedChangeListener { _, isChecked ->
                    changeEnabledStatus(holder.itemView, isChecked)
                    IOHandler.writeSetting(IOHandler.SharedPrefKeys.key_isNotificationOnOneTime, isChecked)
                }

                //sound switch
                holder.itemView.sw_settings_sound.isChecked =
                    IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotificationSoundOnOneTime)!!
                holder.itemView.sw_settings_sound.setOnCheckedChangeListener { _, isChecked ->
                    IOHandler.writeSetting(IOHandler.SharedPrefKeys.key_isNotificationSoundOnOneTime, isChecked)
                }

                //vibration switch
                holder.itemView.sw_settings_vibration.isChecked =
                    IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotificationVibrationOnOneTime)!!
                holder.itemView.sw_settings_vibration.setOnCheckedChangeListener { _, isChecked ->
                    IOHandler.writeSetting(IOHandler.SharedPrefKeys.key_isNotificationVibrationOnOneTime, isChecked)
                }

                //set notification time
                holder.itemView.tv_settings_notificaton_time_value.text =
                    IOHandler.getStringFromKey(IOHandler.SharedPrefKeys.key_strNotificationTimeOneTime)
                //set time picker dialog on click
                holder.itemView.tv_settings_notificaton_time_value.setOnClickListener {
                    showTimePickerDialog(it as TextView, IOHandler.SharedPrefKeys.key_strNotificationTimeOneTime)
                }

                val notificationDateArray: BooleanArray = booleanArrayOf(
                    IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_month_beforeOneTime)!!,
                    IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_week_beforeOneTime)!!,
                    IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_day_beforeOneTime)!!,
                    IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_eventdayOneTime)!!
                )

                val constrLayoutNotificationDay =
                    holder.itemView.findViewById<ConstraintLayout>(R.id.constrLayout_settings_notification_day)
                //show checkbox dialog on click
                constrLayoutNotificationDay.setOnClickListener {
                    val alertDialogBuilder = AlertDialog.Builder(context)
                    alertDialogBuilder.setTitle(R.string.settings_title_notification_day)
                        .setPositiveButton(R.string.apply) { _, _ ->

                            holder.itemView.tv_settings_notification_day_value.text =
                                getNotificationDateValueStringFromBooleanArray(notificationDateArray)

                            IOHandler.writeSetting(
                                IOHandler.SharedPrefKeys.key_isRemindedDay_month_beforeOneTime,
                                notificationDateArray[0]
                            )
                            IOHandler.writeSetting(
                                IOHandler.SharedPrefKeys.key_isRemindedDay_week_beforeOneTime,
                                notificationDateArray[1]
                            )
                            IOHandler.writeSetting(
                                IOHandler.SharedPrefKeys.key_isRemindedDay_day_beforeOneTime,
                                notificationDateArray[2]
                            )
                            IOHandler.writeSetting(
                                IOHandler.SharedPrefKeys.key_isRemindedDay_eventdayOneTime,
                                notificationDateArray[3]
                            )
                        }
                        .setMultiChoiceItems(
                            arrayOf(
                                context.getText(R.string.tv_notification_interval_month),
                                context.getText(R.string.tv_notification_interval_week),
                                context.getText(R.string.tv_notification_interval_day),
                                context.getText(R.string.tv_notification_interval_eventday)
                            ),
                            notificationDateArray
                        ) { _, which, isChecked ->
                            notificationDateArray[which] = isChecked
                        }
                        .show()
                }

                var notificationLight =
                    IOHandler.getIntFromKey(IOHandler.SharedPrefKeys.key_notificationLightOneTime)!!
                val constrLayoutNotificationLight =
                    holder.itemView.findViewById<ConstraintLayout>(R.id.constraint_layout_settings_notification_light)
                //show checkbox dialog on click
                constrLayoutNotificationLight.setOnClickListener {
                    val alertDialogBuilder = AlertDialog.Builder(context)
                    alertDialogBuilder.setTitle(R.string.dialog_title_notification_light)
                        .setPositiveButton(context.getText(R.string.apply)) { _, _ ->

                            holder.itemView.tv_settings_notification_light_value.text =
                                getNotifcationLightValueFromInt(notificationLight)

                            IOHandler.writeSetting(
                                IOHandler.SharedPrefKeys.key_notificationLightOneTime,
                                notificationLight
                            )
                        }
                        .setSingleChoiceItems(
                            context.resources.getStringArray(R.array.light_modes),
                            notificationLight
                        ) { _: DialogInterface?, which: Int ->
                            notificationLight = which
                        }
                        .show()
                }

                holder.itemView.tv_settings_notification_light_value.text =
                    getNotifcationLightValueFromInt(notificationLight)

                holder.itemView.tv_settings_notification_day_value.text =
                    getNotificationDateValueStringFromBooleanArray(notificationDateArray)
            }
            4 -> {
                //delete all layout was pressed
                holder.itemView.layout_delete_all_data.setOnClickListener {
                    showDeletAllDialog()
                }

                //export layout was pressed
                holder.itemView.layout_export_data.setOnClickListener {
                    showExportDialog()
                }

                //import layout was pressed
                holder.itemView.layout_import_data.setOnClickListener {
                    showImportDialog()
                }
            }
        }
    }

    private fun getNotificationDateValueStringFromBooleanArray(array: BooleanArray)
            : String {
        var reminderString = ""
        if (array[0]) {
            reminderString += "${context.getText(R.string.tv_notification_interval_month)}\n"
        }
        if (array[1]) {
            reminderString += "${context.getText(R.string.tv_notification_interval_week)}\n"
        }
        if (array[2]) {
            reminderString += "${context.getText(R.string.tv_notification_interval_day)}\n"
        }
        if (array[3]) {
            reminderString += "${context.getText(R.string.tv_notification_interval_eventday)}"
        }
        return reminderString
    }

    private fun getNotifcationLightValueFromInt(value: Int)
            : String {
        // 0 = no light
        // 1 = white light
        // 2 = red light
        // 3 = green light
        // 4 = blue light
        return context.resources.getStringArray(R.array.light_modes)[value]
    }

    private fun changeEnabledStatus(view: View, isEnabled: Boolean) {
        val constraintLayout = view.findViewById<ConstraintLayout>(R.id.constraintLayout_card_view)
        for (i in 3 until constraintLayout.childCount) {
            if (isEnabled) {
                constraintLayout.getChildAt(i).visibility = View.VISIBLE
            } else {
                constraintLayout.getChildAt(i).visibility = View.GONE
            }
        }
    }

    private fun showTimePickerDialog(tv_notification_time: TextView, notifcationTimeKey: String) {
        //split tv string into hour and minute
        val hour = tv_notification_time.text.split(":")[0].toInt()
        val minute = tv_notification_time.text.split(":")[1].toInt()

        val tpd = TimePickerDialog(
            this.context,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minuteOfHour ->

                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minuteOfHour)

                val timeString = EventDate.parseTimeToString(cal.time)

                tv_notification_time.text = timeString
                IOHandler.writeSetting(
                    notifcationTimeKey,
                    timeString
                )
            },
            hour,
            minute,
            true
        )
        tpd.show()
    }

    override fun getItemCount()
            : Int {
        return this.itemList.size
    }

    private fun showDeletAllDialog() {
        val dialogBuilder = AlertDialog.Builder(this.context)
        dialogBuilder.setTitle(R.string.delete_all_dialog_title)
        dialogBuilder.setMessage(R.string.delete_all_dialog_body)
        dialogBuilder.setPositiveButton(R.string.yes) { _, _ ->
            Toast.makeText(context, R.string.delete_all_dialog_confirmation, Toast.LENGTH_LONG).show()
            EventHandler.deleteAllEntriesAndImages(context, true)
            (context as MainActivity).addMonthDivider()
            (context).supportFragmentManager.popBackStack()
        }
        dialogBuilder.setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
        dialogBuilder.setIcon(R.drawable.ic_error_outline)
        dialogBuilder.show()
    }

    private fun showImportDialog() {
        val dialogBuilder = AlertDialog.Builder(this.context)
        dialogBuilder.setTitle(R.string.dialog_import_data_title)
        dialogBuilder.setMessage(
            context.getString(
                R.string.dialog_import_data_text,
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath
            )
        )

        dialogBuilder.setPositiveButton(R.string.cntnue) { _, _ ->
            importData()
        }
        dialogBuilder.setNegativeButton(R.string.abort) { dialog, _ ->
            dialog.dismiss()
        }
        dialogBuilder.setIcon(R.drawable.ic_info)
        dialogBuilder.show()
    }

    private fun showExportDialog() {
        val dialogBuilder = AlertDialog.Builder(this.context)
        dialogBuilder.setTitle(R.string.dialog_export_data_title)
        dialogBuilder.setMessage(
            context.getString(
                R.string.dialog_export_data_text,
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath
            )
        )

        dialogBuilder.setPositiveButton(R.string.cntnue) { _, _ ->
            exportData()
        }
        dialogBuilder.setNegativeButton(R.string.abort) { dialog, _ ->
            dialog.dismiss()
        }
        dialogBuilder.setIcon(R.drawable.ic_info)
        dialogBuilder.show()
    }

    private fun exportData() {
        //check permission for API>=23
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // only for gingerbread and newer versions
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED
            ) {
                // Permission is not granted
                //ask user for permission
                (context as MainActivity).requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    6001
                )
                return
            }
        }
        (context as MainActivity).writeDataToExternal()
    }

    private fun importData() {
        //check permission for API>=23
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // only for gingerbread and newer versions
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED
            ) {
                // Permission is not granted
                //ask user for permission
                ActivityCompat.requestPermissions(
                    context as MainActivity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    6002
                )
                return
            }
        }
        (context as MainActivity).importDataFromExternal()
    }
}