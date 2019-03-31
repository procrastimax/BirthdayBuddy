package com.procrastimax.birthdaybuddy.fragments

import android.app.TimePickerDialog
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.procrastimax.birthdaybuddy.MainActivity
import com.procrastimax.birthdaybuddy.R
import com.procrastimax.birthdaybuddy.handler.EventHandler
import com.procrastimax.birthdaybuddy.handler.IOHandler
import com.procrastimax.birthdaybuddy.handler.NotificationHandler
import com.procrastimax.birthdaybuddy.models.EventDate
import kotlinx.android.synthetic.main.fragment_settings.*
import java.util.*


class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (context as MainActivity).changeToolbarState(MainActivity.Companion.ToolbarState.Settings)
        val toolbar = activity!!.findViewById<android.support.v7.widget.Toolbar>(R.id.toolbar)
        val backBtn = toolbar.findViewById<ImageView>(R.id.iv_back_arrow)
        backBtn.setOnClickListener {
            backPressed()
        }

        loadAllSettings()

        this.tv_notification_time_value.setOnClickListener {
            this.showTimePickerDialog()
        }

        //instantly change settings in shared preferences on click of switch
        this.sw_isNotificationOn.setOnCheckedChangeListener { _, isChecked ->
            setNotificationStatus(isChecked)
            IOHandler.writeSetting(IOHandler.SharedPrefKeys.key_isNotificationOn, isChecked)
        }
    }

    private fun backPressed() {
        (context as MainActivity).onBackPressed()
    }

    private fun showTimePickerDialog() {
        //split tv string into hour and minute
        val hour = this.tv_notification_time_value.text.split(":")[0].toInt()
        val minute = this.tv_notification_time_value.text.split(":")[1].toInt()

        val tpd = TimePickerDialog(
            this.context,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minuteOfHour ->

                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minuteOfHour)

                val timeString = EventDate.parseTimeToString(cal.time)

                this.tv_notification_time_value.text = timeString
                IOHandler.writeSetting(
                    IOHandler.SharedPrefKeys.key_strNotificationTime,
                    timeString
                )
            },
            hour,
            minute,
            true
        )
        tpd.show()
    }

    private fun loadAllSettings() {
        //read current settings and set switch state to them
        IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotificationOn).let {
            if (it != null) {
                this.sw_isNotificationOn.isChecked = it
                this.setNotificationStatus(it)
            }
        }

        //sound
        IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotificationSoundOn).let {
            if (it != null)
                this.sw_isNotificationSoundOn.isChecked = it
        }

        //vibration
        IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotificationVibrationOn).let {
            if (it != null)
                this.sw_isNotificationVibrationOn.isChecked = it
        }

        //notification time
        IOHandler.getStringFromKey(IOHandler.SharedPrefKeys.key_strNotificationTime).let {
            if (it != null)
                this.tv_notification_time_value.text = it
        }

        // -----------------------------------------
        // intervall checkboxes
        IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isIntervall_Month).let {
            if (it != null)
                this.cb_notification_month_before.isChecked = it
        }

        IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isIntervall_Week).let {
            if (it != null)
                this.cb_notification_week_before.isChecked = it
        }

        IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isIntervall_Day).let {
            if (it != null)
                this.cb_notification_day_before.isChecked = it
        }

        IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isIntervall_EventDay).let {
            if (it != null)
                this.cb_notification_eventday_before.isChecked = it
        }
        // -----------------------------------------
        // event type checkboxes
        IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotification_Birthday).let {
            if (it != null)
                this.cb_notifications_what_remind_birthdays.isChecked = it
        }
        IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotification_AnnualEvent).let {
            if (it != null)
                this.cb_notifications_what_remind_annual_events.isChecked = it
        }
        IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotification_OneTimeEvent).let {
            if (it != null)
                this.cb_notifications_what_remind_one_time_events.isChecked = it
        }
    }


    override fun onPause() {
        super.onPause()

        //save all settings
        IOHandler.writeSetting(
            IOHandler.SharedPrefKeys.key_isNotificationSoundOn,
            sw_isNotificationSoundOn.isChecked
        )

        IOHandler.writeSetting(
            IOHandler.SharedPrefKeys.key_isNotificationVibrationOn,
            sw_isNotificationVibrationOn.isChecked
        )

        //intervall checkboxes
        IOHandler.writeSetting(
            IOHandler.SharedPrefKeys.key_isIntervall_Month,
            cb_notification_month_before.isChecked
        )

        IOHandler.writeSetting(IOHandler.SharedPrefKeys.key_isIntervall_Week, cb_notification_week_before.isChecked)

        IOHandler.writeSetting(IOHandler.SharedPrefKeys.key_isIntervall_Day, cb_notification_day_before.isChecked)

        IOHandler.writeSetting(
            IOHandler.SharedPrefKeys.key_isIntervall_EventDay,
            cb_notification_eventday_before.isChecked
        )

        //notification event type checkboxes
        IOHandler.writeSetting(
            IOHandler.SharedPrefKeys.key_isNotification_Birthday,
            cb_notifications_what_remind_birthdays.isChecked
        )

        IOHandler.writeSetting(
            IOHandler.SharedPrefKeys.key_isNotification_AnnualEvent,
            cb_notifications_what_remind_annual_events.isChecked
        )

        IOHandler.writeSetting(
            IOHandler.SharedPrefKeys.key_isNotification_OneTimeEvent,
            cb_notifications_what_remind_one_time_events.isChecked
        )

        //update current running notifications
        NotificationHandler.cancelAllNotifications(context!!, EventHandler.getList())
        NotificationHandler.scheduleListEventNotifications(context!!, EventHandler.getList())
    }

    /**
     * setNotificationStatus enables or disables all elements in the sub notification layout
     */
    private fun setNotificationStatus(enabled: Boolean) {
        val subLayout = this.view!!.findViewById<ConstraintLayout>(R.id.constrLayout_sub_notifications)
        for (i in 0 until subLayout.childCount) {
            val view = subLayout.getChildAt(i)
            view.isEnabled = enabled
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
}
