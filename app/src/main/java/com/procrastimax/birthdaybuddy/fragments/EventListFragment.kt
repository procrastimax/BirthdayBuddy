package com.procrastimax.birthdaybuddy.fragments

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.procrastimax.birthdaybuddy.MainActivity
import com.procrastimax.birthdaybuddy.R
import com.procrastimax.birthdaybuddy.handler.DrawableHandler
import com.procrastimax.birthdaybuddy.handler.EventHandler
import com.procrastimax.birthdaybuddy.handler.IOHandler
import com.procrastimax.birthdaybuddy.handler.NotificationHandler
import com.procrastimax.birthdaybuddy.models.EventBirthday
import com.procrastimax.birthdaybuddy.models.EventDate
import com.procrastimax.birthdaybuddy.views.EventAdapter
import com.procrastimax.birthdaybuddy.views.RecycleViewItemDivider
import kotlinx.android.synthetic.main.fragment_event_list.*
import java.util.*

class EventListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var isFABOpen = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (context as MainActivity).changeToolbarState(MainActivity.Companion.ToolbarState.Default)

        isFABOpen = false

        fab_layout_add_annual_event.visibility = ConstraintLayout.INVISIBLE
        fab_layout_add_birthday.visibility = ConstraintLayout.INVISIBLE
        fab_layout_add_one_time.visibility = ConstraintLayout.INVISIBLE

        viewManager = LinearLayoutManager(view.context)
        viewAdapter = EventAdapter(view.context)

        val toolbar = activity!!.findViewById<android.support.v7.widget.Toolbar>(R.id.toolbar)
        toolbar.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
        toolbar.setContentInsetsAbsolute(0, 0)

        val settings_btn = toolbar.findViewById<ImageView>(R.id.iv_more_vert)

        settings_btn.setOnClickListener {
            val popup = PopupMenu(activity!!, settings_btn, Gravity.END)
            popup.menuInflater.inflate(R.menu.main_menu, popup.menu)
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.item_settings -> {
                        closeFABMenu(true)
                        val ft = fragmentManager!!.beginTransaction()
                        ft.replace(
                            R.id.fragment_placeholder,
                            SettingsFragment.newInstance()
                        )
                        ft.addToBackStack(null)
                        ft.commit()
                        true
                    }
                    R.id.item_about -> {
                        Toast.makeText(context, "about was pressed", Toast.LENGTH_LONG).show()
                        true
                    }
                    R.id.item_delete_all -> {
                        //TODO: add user confirmation
                        Toast.makeText(context, "delete all was pressed", Toast.LENGTH_LONG).show()
                        EventHandler.deleteAllEntries(context!!, true)
                        (context as MainActivity).addMonthDivider()
                        viewAdapter.notifyDataSetChanged()
                        true
                    }
                    R.id.item_export -> {
                        Toast.makeText(context, "export was pressed", Toast.LENGTH_LONG).show()
                        true
                    }
                    R.id.item_import -> {
                        Toast.makeText(context, "import was pressed", Toast.LENGTH_LONG).show()
                        true
                    }
                    R.id.item_notification -> {
                        buildNotification(
                            context!!,
                            EventBirthday(Calendar.getInstance().time, "Max", "Mustermann", false),
                            1
                        )
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
            popup.show()
        }

        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
            scrollToPosition(traverseForFirstMonthEntry())
        }
        recyclerView.addItemDecoration(RecycleViewItemDivider(view.context))
        recyclerView.setPadding(
            recyclerView.paddingLeft,
            recyclerView.paddingTop,
            recyclerView.paddingRight,
            (resources.getDimension(R.dimen.fab_margin) + resources.getDimension(R.dimen.fab_size_bigger)).toInt()
        )

        fab_show_fab_menu.setOnClickListener {
            if (isFABOpen) {
                closeFABMenu()
            } else {
                showFABMenu()
            }
        }

        fab_add_birthday.setOnClickListener {
            closeFABMenu(true)
            val ft = fragmentManager!!.beginTransaction()
            ft.replace(
                R.id.fragment_placeholder,
                BirthdayInstanceFragment.newInstance()
            )
            ft.addToBackStack(null)
            ft.commit()
        }

        fab_add_annual_event.setOnClickListener {
            closeFABMenu(true)
            val ft = fragmentManager!!.beginTransaction()
            ft.replace(
                R.id.fragment_placeholder,
                AnnualEventInstanceFragment.newInstance()
            )
            ft.addToBackStack(null)
            ft.commit()
        }

        fab_layout_add_one_time.setOnClickListener {
            closeFABMenu(true)
            val ft = fragmentManager!!.beginTransaction()
            ft.replace(
                R.id.fragment_placeholder,
                OneTimeEventInstanceFragment.newInstance()
            )
            ft.addToBackStack(null)
            ft.commit()
        }
    }

    private fun buildNotification(context: Context, event: EventDate, notificationID: Int) {

        // Create an explicit intent for an Activity, so the activity starts when notification is clicked
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //val notificationId = 100
        //val channelId = "channel-01"
        //val channelName = "Channel Name"

        //new channel ID system for android oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = context.getString(R.string.notification_channel_name)
            val descriptionText = context.getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("channel-birthdaybuddy", channelName, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)
        }

        //switch event type

        if (event is EventBirthday) {

            var drawable: Drawable?
            if (event.avatarImageUri != null) {
                IOHandler.registerIO(context)
                IOHandler.readAll(context)
                DrawableHandler.loadAllDrawables(context)
                drawable = DrawableHandler.getDrawableAt(event.eventID)
                if (drawable == null) {
                    drawable = ContextCompat.getDrawable(context, R.drawable.ic_birthday_person)
                }
            } else {
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_birthday_person)
            }

            val builder = NotificationCompat.Builder(context, "channel-birthdaybuddy")
                //TODO: set small icon to app icon
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
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setStyle(NotificationCompat.BigTextStyle())
                .setDefaults(Notification.DEFAULT_ALL)
                .setLargeIcon(DrawableHandler.convertToBitmap(drawable!!, true, 128, 128))

            with(notificationManager) {
                notify(notificationID, builder.build())
            }
        }
    }

    private fun fireNotification() {

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val notificationManager: NotificationManager =
            context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationId = 1
        val channelId = "channel-01"
        val channelName = "Channel Name"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //val name = context!!.getString(R.string.notification_channel_name)
            val descriptionText = context!!.getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context!!, channelId)
            .setSmallIcon(R.drawable.ic_birthday_person)
            .setContentText(
                context!!.getString(
                    R.string.notification_content_one_time,
                    "Maxi",
                    2
                )
            )
            .setContentTitle(
                context!!.getString(
                    R.string.notification_title_one_time,
                    "Maxis Geburtstag"
                )
            )
            //TODO: add longer detailed text
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            //for avatar images
            //.setLargeIcon(convertToBitmap(DrawableHandler.getAllDrawables().last().mutate().constantState!!.newDrawable(), 64 , 64 ))
            .setLargeIcon(
                convertToBitmap(ContextCompat.getDrawable(context!!, R.drawable.ic_error_outline)!!, 64, 64)
            )


        with(notificationManager) {
            notify(notificationId, builder.build())
        }
    }

    fun convertToBitmap(drawable: Drawable, widthPixels: Int, heightPixels: Int): Bitmap {
        val mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(mutableBitmap)
        drawable.setBounds(0, 0, widthPixels, heightPixels)
        drawable.draw(canvas)
        return mutableBitmap
    }

    private fun showFABMenu() {
        isFABOpen = true
        fab_show_fab_menu.isClickable = false
        //show layouts
        fab_layout_add_annual_event.visibility = ConstraintLayout.VISIBLE
        fab_layout_add_birthday.visibility = ConstraintLayout.VISIBLE
        fab_layout_add_one_time.visibility = ConstraintLayout.VISIBLE

        this.recyclerView.animate().alpha(0.15f).apply {
            duration = 200
        }

        //move layouts
        //move add birthday layout up
        fab_layout_add_birthday.animate().translationYBy(-resources.getDimension(R.dimen.standard_55)).apply {
            duration = 100
        }

        //move add annual event layout up
        fab_layout_add_annual_event.animate().translationYBy(-resources.getDimension(R.dimen.standard_105)).apply {
            duration = 100
        }

        //move add one time event layout up
        fab_layout_add_one_time.animate().translationYBy(-resources.getDimension(R.dimen.standard_155)).apply {
            duration = 100
        }

        fab_show_fab_menu.animate().duration = 75
        //some fancy overrotated animation
        fab_show_fab_menu.animate().rotationBy(75.0f).withEndAction {
            fab_show_fab_menu.animate().rotationBy(-30.0f).withEndAction {
                fab_show_fab_menu.isClickable = true
            }
        }
        //disable all click events on eventview adapter
        (this.recyclerView.adapter as EventAdapter).isClickable = false
    }

    /**
     * @param immediateAction : Boolean indicates wether an action should take place after the animation
     */
    private fun closeFABMenu(immediateAction: Boolean = false) {
        isFABOpen = false
        //show layouts
        if (!immediateAction) {
            fab_show_fab_menu.isClickable = false
        }

        this.recyclerView.animate().alpha(1.0f)

        //move add birthday event layout down
        fab_layout_add_birthday.animate().translationYBy(resources.getDimension(R.dimen.standard_55)).withEndAction {
            if (!immediateAction) {
                fab_layout_add_birthday.visibility = ConstraintLayout.INVISIBLE
            }
        }

        //move add annual event layout down
        fab_layout_add_annual_event.animate().translationYBy(resources.getDimension(R.dimen.standard_105))
            .withEndAction {
                if (!immediateAction) {
                    fab_layout_add_annual_event.visibility = ConstraintLayout.INVISIBLE
                }
            }

        //move add one time event layout down
        fab_layout_add_one_time.animate().translationYBy(resources.getDimension(R.dimen.standard_155))
            .withEndAction {
                if (!immediateAction) {
                    fab_layout_add_one_time.visibility = ConstraintLayout.INVISIBLE
                }
            }

        fab_show_fab_menu.animate().rotationBy(-45.0f).withEndAction {
            if (!immediateAction) {
                fab_show_fab_menu.isClickable = true
            }
        }
        (this.recyclerView.adapter as EventAdapter).isClickable = true
    }

    /**
     * traverseForFirstMonthEntry is a function to get the position of the month item position of the current month
     * TODO: maybe there is a better way to find the current month item, but for small amount of entries this may work out well
     */
    private fun traverseForFirstMonthEntry(): Int {
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        for (i in 0 until EventHandler.getList().size) {
            if (EventHandler.getList()[i].getMonth() == currentMonth)
                return i
        }
        return 0
    }

    companion object {

        val EVENT_LIST_FRAGMENT_TAG = "EVENT_LIST"

        @JvmStatic
        fun newInstance(): EventListFragment {
            return EventListFragment()
        }
    }
}
