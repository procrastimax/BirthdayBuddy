package com.procrastimax.birthdaybuddy.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.ActionBar
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.procrastimax.birthdaybuddy.MainActivity
import com.procrastimax.birthdaybuddy.R
import java.util.*


/**
 * EventInstanceFragment abstract base class for all fragments which edit/add an instance of EventDate
 */
abstract class EventInstanceFragment : Fragment() {

    /**
     * toolbar is the changed toolbar for this fragment to provide accept/ close functionality
     */
    private val toolbar: Toolbar by lazy {
        activity!!.findViewById<Toolbar>(R.id.toolbar)
    }

    private var toolbarContentInsentLeft = 56

    protected var eventDate: Date = Calendar.getInstance().time

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (context as MainActivity).setSupportActionBar(toolbar)
        setHasOptionsMenu(false)

        //use 16dp as left content insent
        toolbarContentInsentLeft = MainActivity.convertDpToPx(context!!, 16.toFloat())

        //check if toolbar already has a view inflated
        var toolbarView: View? = toolbar.getChildAt(0)
        if (toolbarView != null && toolbarView.id == R.id.constrLayout_toolbar_edit) {
            toolbar.getChildAt(0).visibility = View.VISIBLE
            toolbar.setContentInsetsAbsolute(0, toolbar.contentInsetRight)
        } else {
            //when toolbar doesnt have child of custom view, then inflate and add it to toolbar, also set some params
            toolbarView = layoutInflater.inflate(R.layout.toolbar_edit_event, null)
            val actionBarParams =
                ActionBar.LayoutParams(
                    ActionBar.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.MATCH_PARENT
                )
            actionBarParams.gravity = Gravity.CENTER
            toolbar.setContentInsetsAbsolute(0, toolbar.contentInsetRight)
            toolbar.addView(toolbarView, 0, actionBarParams)
        }

        toolbarView?.findViewById<ImageView>(R.id.btn_edit_event_accept).apply {
            this?.setOnClickListener {
                acceptBtnPressed()
            }
        }
        toolbarView?.findViewById<ImageView>(R.id.btn_edit_event_close).apply {
            this?.setOnClickListener {
                closeBtnPressed()
            }
        }
    }

    fun setToolbarTitle(title: String) {
        val toolbarView: View? = toolbar.getChildAt(0)
        if (toolbarView != null && toolbarView.id == R.id.constrLayout_toolbar_edit) {
            toolbarView.findViewById<TextView>(R.id.tv_edit_fragment_title).apply {
                text = title
            }
        }
    }

    /**
     * onDetach is called after the fragment has been detached
     * changes the toolbar state back to default
     */
    override fun onDetach() {
        super.onDetach()
        toolbar.getChildAt(0).visibility = View.GONE
        toolbar.setContentInsetsAbsolute(this.toolbarContentInsentLeft, toolbar.contentInsetRight)
        toolbar.setBackgroundColor(
            ContextCompat.getColor(
                context as MainActivity,
                android.R.color.transparent
            )
        )
    }

    /**
     * closeBtnPressed emulates a click on androids "back button" to close a fragment
     */
    fun closeBtnPressed() {
        if (context != null) {
            (context as MainActivity).supportFragmentManager.popBackStack()
        }
    }

    /**
     * acceptBtnPressed is the function which is called when the toolbars accept btn has been clicked
     */
    abstract fun acceptBtnPressed()

}