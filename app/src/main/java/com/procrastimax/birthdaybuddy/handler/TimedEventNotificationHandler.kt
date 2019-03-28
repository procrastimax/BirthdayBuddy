package com.procrastimax.birthdaybuddy.handler

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import com.procrastimax.birthdaybuddy.MainActivity
import com.procrastimax.birthdaybuddy.R
import com.procrastimax.birthdaybuddy.models.EventBirthday
import com.procrastimax.birthdaybuddy.models.EventDate
import com.procrastimax.birthdaybuddy.models.MonthDivider
import java.util.*
import kotlin.concurrent.fixedRateTimer

/**
 * TimedEventNotificationHandler handles timed reading of future events used for notifications
 *
 * TODO:
 *  - cache event list, and read from cached eventlist, bc. after the activity closes the eventhandler list is empty https://developer.android.com/training/data-storage/files.html#WriteCacheFileInternal
 */
class TimedEventNotificationHandler {

    private lateinit var context: Context

    private var notificationID = 0

    private lateinit var intent: Intent

    /**
     * checkIntervall is the intervall in which the next event has to be checked for notification
     */
    private val checkIntervall = 10000

    private var fixedRateTimer: Timer? = null

    fun startTimer() {
        fixedRateTimer =
            fixedRateTimer(name = "checkEventTimer", initialDelay = 0, period = this.checkIntervall.toLong()) {
                notificateNextBirthdays()
                Log.i(this.toString(), "TIMER DID SOMETHING")
            }
    }

    fun stopTimer() {
        if (fixedRateTimer != null) {
            fixedRateTimer!!.cancel()
            fixedRateTimer = null
        }
    }

    fun registerContext(context: Context) {
        this.context = context
    }

    private fun findNextEvents(): List<Pair<Int, EventDate>> {

        //for how many days should the birthdays be presented
        val intervall: Int = 7
        val eventList = emptyList<Pair<Int, EventDate>>().toMutableList()

        val cal = Calendar.getInstance()
        val currentDate = cal.time
        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

        for (it in EventHandler.getSortedListBy(EventHandler.getList())) {
            if (it.second is MonthDivider) {
                continue
            }

            val dayOfYear = it.second.getDayOfYear()
            if (currentDay <= dayOfYear && currentDay + intervall >= dayOfYear) {
                eventList.add(Pair((it.second.getDayOfYear() - currentDay), it.second))
            }
        }

        return eventList
    }

    private fun notificateNextBirthdays() {

        val list = findNextEvents()
        if (list.isNotEmpty()) {

            val builder: NotificationCompat.Builder

            //for android O provide channel ID
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "channel id izz da" //context.getString(R.string.app_name)
                val descriptionText = "beschreibungs text" //getString(R.string.channel_description)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel("69", name, importance).apply {
                    description = descriptionText
                }
                // Register the channel with the system
                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)

                builder = NotificationCompat.Builder(this.context, channel.id)

            } else {
                //sdk under android oreo
                //channel id under oreo is ignored
                builder = NotificationCompat.Builder(this.context, "")
            }

            builder.setSmallIcon(R.drawable.ic_birthday_person)

            if (list.first().second is EventBirthday) {
                builder.setContentTitle("${(list.first().second as EventBirthday).forename}'s Geburtstag")
                builder.setContentText("In ${(list.first().second as EventBirthday).getDaysUntil()} Tagen!")
            } else {
                builder.setContentTitle("annual event")
                builder.setContentText("okay")
            }

            builder.priority = NotificationCompat.PRIORITY_DEFAULT

            //init open intent
            intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

            builder.setContentIntent(pendingIntent)
            //close on tap
            builder.setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                notify(notificationID, builder.build())
            }
            notificationID++
        }
    }
}