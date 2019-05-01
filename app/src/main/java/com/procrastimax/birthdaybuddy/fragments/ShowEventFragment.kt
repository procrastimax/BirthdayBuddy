package com.procrastimax.birthdaybuddy.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.procrastimax.birthdaybuddy.MainActivity
import com.procrastimax.birthdaybuddy.R
import com.procrastimax.birthdaybuddy.handler.EventHandler
import kotlinx.android.synthetic.main.activity_main.*

abstract class ShowEventFragment : Fragment() {

    var eventID: Int = -1

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
        setHasOptionsMenu(true)

        (context as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        (context as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setToolbarTitle(context!!.resources.getString(R.string.app_name))

        //to show the information about the instance, the fragment has to be bundled with an argument
        //fragment was already instantiated
        if (eventID >= 0) {

            //if a event was deleted in the edit_event fragment "above"
            //then we land at this point, so we have to check whether the event to the ID is existent
            if (EventHandler.getEventToEventIndex(eventID) != null) {
                this.updateUI()
            } else {
                closeButtonPressed()
            }
        } else if (arguments != null) {
            //position = arguments!!.getInt(ITEM_ID_PARAM)
            eventID = arguments!!.getInt(MainActivity.FRAGMENT_EXTRA_TITLE_EVENTID)
            updateUI()
        }
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
        (context as MainActivity).scrollable_toolbar.title = title
    }

    /**
     * closeButtonPressed emulated a press on androids "back button" to close/ detach a fragment
     */
    private fun closeButtonPressed() {
        (context as MainActivity).supportFragmentManager.popBackStack()
    }
}