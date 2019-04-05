package com.procrastimax.birthdaybuddy.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.procrastimax.birthdaybuddy.MainActivity
import com.procrastimax.birthdaybuddy.R
import com.procrastimax.birthdaybuddy.handler.EventHandler

const val ITEM_ID_PARAM = "ITEMID"

abstract class ShowEventFragment : Fragment() {

    var eventID: Int = -1
    var position : Int = -1

    val toolbar: Toolbar by lazy {
        activity!!.findViewById<android.support.v7.widget.Toolbar>(R.id.toolbar)
    }


    /**
     * updateUI updates all TextViews and other views to the current instance(Anniversary, Birthday) data
     */
    abstract fun updateUI()

    /**
     * shareEvent a function which is called after the share button has been pressed
     * It provides a simple intent to share data as plain text in other apps
     */
    abstract fun shareEvent()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (context as MainActivity).changeToolbarState(MainActivity.Companion.ToolbarState.ShowEvent)


        val closeBtn: ImageView = toolbar.findViewById<ImageView>(R.id.iv_toolbar_show_event_back)


        val shareBtn: ImageView = toolbar.findViewById<ImageView>(R.id.iv_toolbar_show_event_share)


        closeBtn.setOnClickListener {
            closeButtonPressed()
        }

        shareBtn.setOnClickListener {
            shareEvent()
        }
    }

    override fun onDetach() {
        super.onDetach()
        (context as MainActivity).changeToolbarState(MainActivity.Companion.ToolbarState.Default)
    }

    fun getEventID(position: Int): Int {
        if (EventHandler.getList().isNotEmpty() && (position in 0 until EventHandler.getList().size)) {
            return EventHandler.getList()[position].eventID
        } else {
            return -1
        }
    }

    /**
     * closeButtonPressed emulated a press on androids "back button" to close/ detach a fragment
     */
    fun closeButtonPressed() {
        (context as MainActivity).onBackPressed()
    }
}