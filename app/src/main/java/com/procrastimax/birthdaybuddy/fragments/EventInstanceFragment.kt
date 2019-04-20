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


/**
 * EventInstanceFragment abstract base class for all fragments which edit/add an instance of EventDate
 */
abstract class EventInstanceFragment : Fragment() {

    /**
     * toolbar is the changed toolbar for this fragment to provide accept/ close functionality
     */
    val toolbar: Toolbar by lazy {
        activity!!.findViewById<Toolbar>(R.id.toolbar)
    }

    var toolbarContentInsentLeft = 56

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (context as MainActivity).setSupportActionBar(toolbar)
        setHasOptionsMenu(false)

        //use 16dp as left content insent
        toolbarContentInsentLeft = MainActivity.convertDpToPx(context!!, 16.toFloat())

        //check if toolbar already has a view inflated
        var toolbar_view: View? = toolbar.getChildAt(0)
        if (toolbar_view != null && toolbar_view.id == R.id.constrLayout_toolbar_edit) {
            toolbar.getChildAt(0).visibility = View.VISIBLE
            toolbar.setContentInsetsAbsolute(0, toolbar.contentInsetRight)
        } else {
            //when toolbar doesnt have child of custom view, then inflate and add it to toolbar, also set some params
            toolbar_view = layoutInflater.inflate(R.layout.toolbar_edit_event, null)
            val actionBar_params =
                ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT)
            actionBar_params.gravity = Gravity.CENTER
            toolbar.setContentInsetsAbsolute(0, toolbar.contentInsetRight)
            toolbar.addView(toolbar_view, 0, actionBar_params)
        }

        toolbar_view?.findViewById<ImageView>(R.id.btn_edit_event_accept).apply {
            this?.setOnClickListener {
                acceptBtnPressed()
            }
        }
        toolbar_view?.findViewById<ImageView>(R.id.btn_edit_event_close).apply {
            this?.setOnClickListener {
                closeBtnPressed()
            }
        }
    }

    fun setToolbarTitle(title: String) {
        val toolbar_view: View? = toolbar.getChildAt(0)
        if (toolbar_view != null && toolbar_view.id == R.id.constrLayout_toolbar_edit) {
            val tv_title = toolbar_view.findViewById<TextView>(R.id.tv_edit_fragment_title)
            tv_title.text = title
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
        toolbar.setBackgroundColor(ContextCompat.getColor(context as MainActivity, android.R.color.transparent))
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