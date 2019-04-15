package com.procrastimax.birthdaybuddy.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.procrastimax.birthdaybuddy.MainActivity
import com.procrastimax.birthdaybuddy.R
import com.procrastimax.birthdaybuddy.handler.EventHandler

const val ITEM_ID_PARAM = "ITEMID"

abstract class ShowEventFragment : Fragment() {

    var eventID: Int = -1
    var position: Int = -1

    val toolbar: Toolbar by lazy {
        activity!!.findViewById<Toolbar>(R.id.toolbar)
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

    abstract fun editEvent()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (context as MainActivity).setSupportActionBar(toolbar)
        setHasOptionsMenu(true)

        (context as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        (context as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setToolbarTitle(context!!.resources.getString(R.string.app_name))
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.toolbar_show_event, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                closeButtonPressed()
            }
            R.id.toolbar_share -> {
                shareEvent()
            }
            R.id.toolbar_edit -> {
                editEvent()
                //when leave fragment, change status of home button
                (context as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun setToolbarTitle(title: String) {
        toolbar.title = title
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
        (context as MainActivity).supportFragmentManager.popBackStackImmediate()
    }
}