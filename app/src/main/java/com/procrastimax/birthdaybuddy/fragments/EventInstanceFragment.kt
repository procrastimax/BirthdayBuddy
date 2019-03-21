package com.procrastimax.birthdaybuddy.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.procrastimax.birthdaybuddy.MainActivity
import com.procrastimax.birthdaybuddy.R

/**
 * EventInstanceFragment abstract base class for all fragments which edit/add an instance of EventDate
 */
abstract class EventInstanceFragment : Fragment() {

    /**
     * toolbar is the changed toolbar for this fragment to provide accept/ close functionality
     */
    val toolbar: Toolbar by lazy {
        activity!!.findViewById<android.support.v7.widget.Toolbar>(R.id.toolbar)
    }

    /**
     * title is the title of the toolbar
     */
    val title: TextView by lazy {
        toolbar.findViewById<TextView>(R.id.tv_add_fragment_title)
    }

    /**
     * onViewCreated is called after the fragments view has been created
     * changes the toolbar view to EditEvent
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (context as MainActivity).changeToolbarState(MainActivity.Companion.ToolbarState.EditEvent)

        val closeBtn = toolbar.findViewById<ImageView>(R.id.btn_edit_event_close)
        val acceptBtn = toolbar.findViewById<ImageView>(R.id.btn_edit_event_accept)

        closeBtn.setOnClickListener {
            closeBtnPressed()
        }

        acceptBtn.setOnClickListener { acceptBtnPressed() }
    }

    /**
     * onDetach is called after the fragment has been detached
     * changes the toolbar state back to default
     */
    override fun onDetach() {
        super.onDetach()
        (context as MainActivity).changeToolbarState(MainActivity.Companion.ToolbarState.Default)
    }

    /**
     * closeBtnPressed emulates a click on androids "back button" to close a fragment
     */
    fun closeBtnPressed() {
        (context as MainActivity).onBackPressed()
    }

    /**
     * acceptBtnPressed is the function which is called when the toolbars accept btn has been clicked
     */
    abstract fun acceptBtnPressed()

}