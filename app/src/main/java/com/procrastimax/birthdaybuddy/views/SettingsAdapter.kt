package com.procrastimax.birthdaybuddy.views

import android.app.TimePickerDialog
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.procrastimax.birthdaybuddy.R
import com.procrastimax.birthdaybuddy.handler.IOHandler
import com.procrastimax.birthdaybuddy.models.EventDate
import kotlinx.android.synthetic.main.settings_card_view.view.*
import java.util.*

class SettingsAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * itemList is a list of 3 integers to imply the settings card view
     */
    val itemList = listOf(1, 2, 3)

    class SettingCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    override fun getItemViewType(position: Int): Int {
        return this.itemList[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val cardView =
            LayoutInflater.from(parent.context).inflate(R.layout.settings_card_view, parent, false)
        return SettingCardViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            1 -> {
                holder.itemView.tv_settings_title.text = "Birthday"

                val isEnabled = IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotificationOnBirthday)!!
                holder.itemView.sw_settings_notifcations.isChecked = isEnabled

                holder.itemView.sw_settings_notifcations.setOnCheckedChangeListener { _, isChecked ->
                    changeEnabledStatus(holder.itemView, isChecked)
                }

                holder.itemView.sw_settings_sound.isChecked =
                    IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotificationSoundOnBirthday)!!
                holder.itemView.sw_settings_vibration.isChecked =
                    IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotificationVibrationOnBirthday)!!

                holder.itemView.tv_settings_notificaton_time_value.text =
                    IOHandler.getStringFromKey(IOHandler.SharedPrefKeys.key_strNotificationTimeBirthday)

                // 0 = no light
                // 1 = white light
                // 2 = red light
                // 3 = green light
                // 4 = blue light
                when (IOHandler.getIntFromKey(IOHandler.SharedPrefKeys.key_notificationLightBirthday)) {
                    0 -> {
                        holder.itemView.tv_settings_notification_light_value.text = "no light"
                    }
                    1 -> {
                        holder.itemView.tv_settings_notification_light_value.text = "white"
                    }
                    2 -> {
                        holder.itemView.tv_settings_notification_light_value.text = "red"
                    }
                    3 -> {
                        holder.itemView.tv_settings_notification_light_value.text = "green"
                    }
                    4 -> {
                        holder.itemView.tv_settings_notification_light_value.text = "blue"
                    }
                }

                var reminderDayString: String = ""
                if (IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_month_beforeBirthday) == true) {
                    reminderDayString += "Month before\n"
                }
                if (IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_week_beforeBirthday) == true) {
                    reminderDayString += "Week before\n"
                }
                if (IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_day_beforeBirthday) == true) {
                    reminderDayString += "Day before\n"
                }
                if (IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_eventdayBirthday) == true) {
                    reminderDayString += "Eventday"
                }
                holder.itemView.tv_settings_notification_day_value.text = reminderDayString

                changeEnabledStatus(holder.itemView, isEnabled)

            }
            2 -> {
                holder.itemView.tv_settings_title.text = "Annual event"

                val isEnabled = IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotificationOnAnnual)!!
                holder.itemView.sw_settings_notifcations.isChecked = isEnabled

                holder.itemView.sw_settings_notifcations.setOnCheckedChangeListener { _, isChecked ->
                    changeEnabledStatus(holder.itemView, isChecked)
                }

                holder.itemView.sw_settings_sound.isChecked =
                    IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotificationSoundOnAnnual)!!
                holder.itemView.sw_settings_vibration.isChecked =
                    IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotificationVibrationOnAnnual)!!

                holder.itemView.tv_settings_notificaton_time_value.text =
                    IOHandler.getStringFromKey(IOHandler.SharedPrefKeys.key_strNotificationTimeAnnual)

                // 0 = no light
                // 1 = white light
                // 2 = red light
                // 3 = green light
                // 4 = blue light
                when (IOHandler.getIntFromKey(IOHandler.SharedPrefKeys.key_notificationLightAnnual)) {
                    0 -> {
                        holder.itemView.tv_settings_notification_light_value.text = "no light"
                    }
                    1 -> {
                        holder.itemView.tv_settings_notification_light_value.text = "white"
                    }
                    2 -> {
                        holder.itemView.tv_settings_notification_light_value.text = "red"
                    }
                    3 -> {
                        holder.itemView.tv_settings_notification_light_value.text = "green"
                    }
                    4 -> {
                        holder.itemView.tv_settings_notification_light_value.text = "blue"
                    }
                }

                var reminderDayString: String = ""
                if (IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_month_beforeAnnual) == true) {
                    reminderDayString += "Month before\n"
                }
                if (IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_week_beforeAnnual) == true) {
                    reminderDayString += "Week before\n"
                }
                if (IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_day_beforeAnnual) == true) {
                    reminderDayString += "Day before\n"
                }
                if (IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_eventdayAnnual) == true) {
                    reminderDayString += "Eventday"
                }
                holder.itemView.tv_settings_notification_day_value.text = reminderDayString
            }

            3 -> {
                holder.itemView.tv_settings_title.text = "One-Time event"

                val isEnabled = IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotificationOnOneTime)!!
                holder.itemView.sw_settings_notifcations.isChecked = isEnabled

                holder.itemView.sw_settings_notifcations.setOnCheckedChangeListener { _, isChecked ->
                    changeEnabledStatus(holder.itemView, isChecked)
                }

                holder.itemView.sw_settings_sound.isChecked =
                    IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotificationSoundOnOneTime)!!
                holder.itemView.sw_settings_vibration.isChecked =
                    IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotificationVibrationOnOneTime)!!

                holder.itemView.tv_settings_notificaton_time_value.text =
                    IOHandler.getStringFromKey(IOHandler.SharedPrefKeys.key_strNotificationTimeOneTime)

                // 0 = no light
                // 1 = white light
                // 2 = red light
                // 3 = green light
                // 4 = blue light
                when (IOHandler.getIntFromKey(IOHandler.SharedPrefKeys.key_notificationLightOneTime)) {
                    0 -> {
                        holder.itemView.tv_settings_notification_light_value.text = "no light"
                    }
                    1 -> {
                        holder.itemView.tv_settings_notification_light_value.text = "white"
                    }
                    2 -> {
                        holder.itemView.tv_settings_notification_light_value.text = "red"
                    }
                    3 -> {
                        holder.itemView.tv_settings_notification_light_value.text = "green"
                    }
                    4 -> {
                        holder.itemView.tv_settings_notification_light_value.text = "blue"
                    }
                }

                var reminderDayString: String = ""
                if (IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_month_beforeOneTime) == true) {
                    reminderDayString += "Month before\n"
                }
                if (IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_week_beforeOneTime) == true) {
                    reminderDayString += "Week before\n"
                }
                if (IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_day_beforeOneTime) == true) {
                    reminderDayString += "Day before\n"
                }
                if (IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isRemindedDay_eventdayOneTime) == true) {
                    reminderDayString += "Eventday"
                }
                holder.itemView.tv_settings_notification_day_value.text = reminderDayString
            }
        }
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

    private fun showTimePickerDialog(tv_notification_time: TextView, notifcationTimeKey: IOHandler.SharedPrefKeys) {
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
                    notifcationTimeKey.toString(),
                    timeString
                )
            },
            hour,
            minute,
            true
        )
        tpd.show()
    }

    override fun getItemCount(): Int {
        return this.itemList.size
    }
}