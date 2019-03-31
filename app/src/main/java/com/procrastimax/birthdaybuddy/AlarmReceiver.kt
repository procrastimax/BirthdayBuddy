package com.procrastimax.birthdaybuddy

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.widget.Toast
import com.procrastimax.birthdaybuddy.handler.DrawableHandler
import com.procrastimax.birthdaybuddy.handler.IOHandler
import com.procrastimax.birthdaybuddy.handler.NotificationHandler
import com.procrastimax.birthdaybuddy.models.AnnualEvent
import com.procrastimax.birthdaybuddy.models.EventBirthday
import com.procrastimax.birthdaybuddy.models.EventDate
import com.procrastimax.birthdaybuddy.models.OneTimeEvent

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val event = IOHandler.convertStringToEventDate(intent!!.getStringExtra("EVENTSTRING"))
        val notificationID = intent.getIntExtra("NOTIFICATIONID", 0)

        when (event) {
            is EventBirthday -> {
                buildNotification(context!!, event, notificationID)
                println("notification builded")
            }
            is AnnualEvent -> {
                buildNotification(context!!, event, notificationID)
                println("notification builded")
            }
            is OneTimeEvent -> {
                buildNotification(context!!, event, notificationID)
                println("notification builded")
            }
            else -> {
                Toast.makeText(context, "unidentified toast made", Toast.LENGTH_SHORT).show()
                println("unidentified toast made")
            }
        }
    }

    private fun buildNotification(context: Context, event: EventDate, notificationID: Int) {
        if (event is EventBirthday) {
            // Create an explicit intent for an Activity, so the activity starts when notification is clicked
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

            var drawable: Drawable? = null
            if (event.avatarImageUri != null) {
                IOHandler.registerIO(context)
                IOHandler.readAll(context)
                DrawableHandler.loadAllDrawables(context)
                drawable = DrawableHandler.getDrawableAt(event.eventID)
            }

            createNotificationChannel(context)

            val builder = NotificationCompat.Builder(context, NotificationHandler.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_birthday_person)
                .setContentText(
                    context.getString(
                        R.string.notification_content_birthday,
                        event.forename,
                        event.getDaysUntil(),
                        event.forename,
                        event.getYearsSince()
                    )
                )
                .setContentTitle(
                    context.getString(
                        R.string.notification_title_birthday,
                        "${event.forename} ${event.surname}"
                    )
                )
                //TODO: add longer detailed text
                .setPriority(NotificationCompat.PRIORITY_MAX)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)


            if (drawable != null) {
                builder.setLargeIcon((drawable as BitmapDrawable).bitmap)
                println("drawable not null")
            } else {
                builder.setLargeIcon(
                    BitmapFactory.decodeResource(
                        context.resources
                        , R.drawable.ic_birthday_person
                    )
                )
            }

            with(NotificationManagerCompat.from(context)) {
                notify(notificationID, builder.build())
            }
        } else if (event is AnnualEvent) {

            // Create an explicit intent for an Activity, so the activity starts when notification is clicked
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

            createNotificationChannel(context)

            val builder = NotificationCompat.Builder(context, NotificationHandler.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_birthday_person)
                .setContentText(
                    context.getString(
                        R.string.notification_content_annual,
                        event.name,
                        event.getDaysUntil()
                    )
                )
                .setContentTitle(
                    context.getString(
                        R.string.notification_title_annual,
                        "${event.name}"
                    )
                )
                //TODO: add longer detailed text
                .setPriority(NotificationCompat.PRIORITY_MAX)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)

            builder.setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources
                    , R.drawable.ic_date_range
                )
            )

            with(NotificationManagerCompat.from(context)) {
                notify(notificationID, builder.build())

            }
        } else if (event is OneTimeEvent) {

            // Create an explicit intent for an Activity, so the activity starts when notification is clicked
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

            createNotificationChannel(context)

            val builder = NotificationCompat.Builder(context, NotificationHandler.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_birthday_person)
                .setContentText(
                    context.getString(
                        R.string.notification_content_one_time,
                        event.name,
                        event.getDaysUntil()
                    )
                )
                .setContentTitle(
                    context.getString(
                        R.string.notification_title_one_time,
                        "${event.name}"
                    )
                )
                //TODO: add longer detailed text
                .setPriority(NotificationCompat.PRIORITY_MAX)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)

            builder.setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources
                    , R.drawable.ic_looks_one_time
                )
            )

            with(NotificationManagerCompat.from(context)) {
                notify(notificationID, builder.build())
            }
        }
    }
}

private fun createNotificationChannel(context: Context): NotificationChannel? {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = context.getString(R.string.notification_channel_name)
        val descriptionText = context.getString(R.string.notification_channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(NotificationHandler.CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        return notificationManager.getNotificationChannel(NotificationHandler.CHANNEL_ID)
    }
    return null
}