package com.procrastimax.birthdaybuddy

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.procrastimax.birthdaybuddy.handler.BitmapHandler
import com.procrastimax.birthdaybuddy.handler.IOHandler
import com.procrastimax.birthdaybuddy.handler.NotificationHandler
import com.procrastimax.birthdaybuddy.models.AnnualEvent
import com.procrastimax.birthdaybuddy.models.EventBirthday
import com.procrastimax.birthdaybuddy.models.EventDate
import com.procrastimax.birthdaybuddy.models.OneTimeEvent

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        //register IOHandler, really important, really
        IOHandler.registerIO(context!!)

        val event = IOHandler.convertStringToEventDate(intent!!.getStringExtra("EVENTSTRING"))
        val notificationID = intent.getIntExtra("NOTIFICATIONID", 0)
        val eventID = intent.getIntExtra("EVENTID", 0)
        event?.eventID = eventID

        when (event) {
            is EventBirthday -> {
                buildNotification(context, event, notificationID, eventID)
            }
            is AnnualEvent -> {
                buildNotification(context, event, notificationID, eventID)
            }
            is OneTimeEvent -> {
                buildNotification(context, event, notificationID, eventID)
            }
            else -> {
                Toast.makeText(context, "unidentified toast made", Toast.LENGTH_SHORT).show()
                println("unidentified toast made")
            }
        }

        //create new notification events for this event
        if (event != null) {
            NotificationHandler.cancelNotification(context, event)
            NotificationHandler.scheduleNotification(context, event)
        }
    }

    private fun buildNotification(context: Context, event: EventDate, notificationID: Int, eventID: Int) {

        // Create an explicit intent for an Activity, so the activity starts when notification is clicked
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //new channel ID system for android oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //val name = context!!.getString(R.string.notification_channel_name)
            val channelName = context.getString(R.string.notification_channel_name)
            val descriptionText = context.getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(NotificationHandler.CHANNEL_ID, channelName, importance).apply {
                description = descriptionText
            }

            //setting the notification light
            when (event) {
                // 0 = no light
                // 1 = white light
                // 2 = red light
                // 3 = green light
                // 4 = blue light
                is EventBirthday -> {
                    val lightColor = getLightColor(event, context)
                    if (lightColor != null) {
                        channel.enableLights(true)
                        channel.lightColor = lightColor
                    } else {
                        channel.enableLights(false)
                    }
                }
                is AnnualEvent -> {
                    val lightColor = getLightColor(event, context)
                    if (lightColor != null) {
                        channel.enableLights(true)
                        channel.lightColor = lightColor
                    } else {
                        channel.enableLights(false)
                    }
                }
                is OneTimeEvent -> {
                    val lightColor = getLightColor(event, context)
                    if (lightColor != null) {
                        channel.enableLights(true)
                        channel.lightColor = lightColor
                    } else {
                        channel.enableLights(false)
                    }
                }
            }
            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)
        }

        //switch event type
        // EVENT BIRTHDAY
        if (event is EventBirthday) {
            var bitmap: Bitmap? = null
            if (event.avatarImageUri != null) {
                bitmap = BitmapHandler.getBitmapFromFile(context, eventID)
                if (bitmap != null) {
                    bitmap = BitmapHandler.getCircularBitmap(bitmap, context.resources)
                }
            }
            if (event.avatarImageUri == null || bitmap == null) {
                val drawable = ContextCompat.getDrawable(context, R.drawable.ic_birthday_person)
                bitmap = BitmapHandler.drawableToBitmap(drawable!!)
                //bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_birthday_person)
            }

            var defaults = Notification.DEFAULT_ALL

            val builder = NotificationCompat.Builder(context, NotificationHandler.CHANNEL_ID)
                //TODO: set small icon to app icon
                .setSmallIcon(R.drawable.ic_launcher_foreground)
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
                .setStyle(NotificationCompat.BigTextStyle())
            if (bitmap != null) {
                builder.setLargeIcon(bitmap)
            }

            if (!IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotificationVibrationOnBirthday)!!) {
                defaults -= Notification.DEFAULT_VIBRATE
            }

            if (!IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotificationSoundOnBirthday)!!) {
                defaults -= Notification.DEFAULT_SOUND
            }

            val lightColor = getLightColor(event, context)
            if (lightColor != null) {
                defaults -= Notification.DEFAULT_LIGHTS
                builder.setLights(lightColor, 500, 500)
            } else {
                defaults -= Notification.DEFAULT_LIGHTS
            }

            builder.setDefaults(defaults)

            with(notificationManager) {
                notify(notificationID, builder.build())
            }

            // ANNUAL EVENT
        } else if (event is AnnualEvent) {

            var defaults = Notification.DEFAULT_ALL

            val drawable = ContextCompat.getDrawable(context, R.drawable.ic_birthday_person)
            val bitmap = BitmapHandler.drawableToBitmap(drawable!!)

            val builder = NotificationCompat.Builder(context, NotificationHandler.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
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
                        event.name
                    )
                )
                //TODO: add longer detailed text
                .setPriority(NotificationCompat.PRIORITY_MAX)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setLargeIcon(bitmap)

            if (!IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotificationVibrationOnAnnual)!!) {
                defaults -= Notification.DEFAULT_VIBRATE
            }

            if (!IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotificationSoundOnAnnual)!!) {
                defaults -= Notification.DEFAULT_SOUND
            }

            val lightColor = getLightColor(event, context)
            if (lightColor != null) {
                defaults -= Notification.DEFAULT_LIGHTS
                builder.setLights(lightColor, 500, 500)
            } else {
                defaults -= Notification.DEFAULT_LIGHTS
            }

            builder.setDefaults(defaults)

            with(notificationManager) {
                notify(notificationID, builder.build())
            }

            // ONE TIME EVENT
        } else if (event is OneTimeEvent) {

            var defaults = Notification.DEFAULT_ALL

            val drawable = ContextCompat.getDrawable(context, R.drawable.ic_birthday_person)
            val bitmap = BitmapHandler.drawableToBitmap(drawable!!)

            val builder = NotificationCompat.Builder(context, NotificationHandler.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
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
                        event.name
                    )
                )
                //TODO: add longer detailed text
                .setPriority(NotificationCompat.PRIORITY_MAX)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setLargeIcon(bitmap)

            if (!IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotificationVibrationOnOneTime)!!) {
                defaults -= Notification.DEFAULT_VIBRATE
            }

            if (!IOHandler.getBooleanFromKey(IOHandler.SharedPrefKeys.key_isNotificationSoundOnOneTime)!!) {
                defaults -= Notification.DEFAULT_SOUND
            }

            val lightColor = getLightColor(event, context)
            if (lightColor != null) {
                defaults -= Notification.DEFAULT_LIGHTS
                builder.setLights(lightColor, 500, 500)
            } else {
                defaults -= Notification.DEFAULT_LIGHTS
            }

            builder.setDefaults(defaults)

            with(notificationManager) {
                notify(notificationID, builder.build())
            }
        }
    }

    private fun getLightColor(event: EventDate, context: Context): Int? {
        when (event) {
            is EventBirthday -> {
                val lightValue = IOHandler.getIntFromKey(IOHandler.SharedPrefKeys.key_notificationLightBirthday)!!
                return getLightARGBFromColorValue(lightValue, context)
            }
            is AnnualEvent -> {
                val lightValue = IOHandler.getIntFromKey(IOHandler.SharedPrefKeys.key_notificationLightAnnual)!!
                return getLightARGBFromColorValue(lightValue, context)
            }
            is OneTimeEvent -> {
                val lightValue = IOHandler.getIntFromKey(IOHandler.SharedPrefKeys.key_notificationLightOneTime)!!
                return getLightARGBFromColorValue(lightValue, context)
            }
            else -> {
                // no light
                return getLightARGBFromColorValue(0, context)
            }
        }
    }

    private fun getLightARGBFromColorValue(colorValue: Int, context: Context): Int? {
        when (colorValue) {
            0 -> {
                return null
            }
            1 -> {
                return ContextCompat.getColor(context, R.color.notification_light_white)
            }
            2 -> {
                return ContextCompat.getColor(context, R.color.notification_light_red)
            }
            3 -> {
                return ContextCompat.getColor(context, R.color.notification_light_green)
            }
            4 -> {
                return ContextCompat.getColor(context, R.color.notification_light_blue)
            }
            else -> {
                return null
            }
        }
    }
}