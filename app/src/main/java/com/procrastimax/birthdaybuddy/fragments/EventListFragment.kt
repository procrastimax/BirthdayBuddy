package com.procrastimax.birthdaybuddy.fragments

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.procrastimax.birthdaybuddy.MainActivity
import com.procrastimax.birthdaybuddy.R
import com.procrastimax.birthdaybuddy.handler.EventHandler
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
                        //open settings fragment
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
                        //open about fragment
                        closeFABMenu(true)
                        val ft = fragmentManager!!.beginTransaction()
                        ft.replace(
                            R.id.fragment_placeholder,
                            AboutFragment.newInstance()
                        )
                        ft.addToBackStack(null)
                        ft.commit()
                        true
                    }
                    R.id.item_help -> {
                        //open about fragment
                        closeFABMenu(true)
                        val ft = fragmentManager!!.beginTransaction()
                        ft.replace(
                            R.id.fragment_placeholder,
                            AboutFragment.newInstance()
                        )
                        ft.addToBackStack(null)
                        ft.commit()
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

        val searchBtn = toolbar.findViewById<ImageView>(R.id.iv_search)
        searchBtn.setOnClickListener {
            Toast.makeText(context, "Search was clicked", Toast.LENGTH_SHORT).show()
            searchStarted()
        }

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

    override fun onResume() {
        super.onResume()
        //when no items except of the 12 month items are in the event list, then display text message
        if (EventHandler.getList().size - 12 == 0) {
            tv_no_events.visibility = TextView.VISIBLE
        } else {
            tv_no_events.visibility = TextView.GONE
        }
    }

    private fun showFABMenu() {
        isFABOpen = true
        fab_show_fab_menu.isClickable = false
        //show layouts
        fab_layout_add_annual_event.visibility = ConstraintLayout.VISIBLE
        fab_layout_add_birthday.visibility = ConstraintLayout.VISIBLE
        fab_layout_add_one_time.visibility = ConstraintLayout.VISIBLE

        this.recyclerView.animate().alpha(0.15f).apply {
            duration = 175
        }

        //move layouts
        //move add birthday layout up
        fab_layout_add_birthday.animate().translationYBy(-resources.getDimension(R.dimen.standard_55) - 20).apply {
            duration = 100
        }.withEndAction {
            fab_layout_add_birthday.animate().translationYBy(20.toFloat()).apply {
                duration = 75
            }
        }

        //move add annual event layout up
        fab_layout_add_annual_event.animate().translationYBy(-resources.getDimension(R.dimen.standard_105) - 40)
            .apply {
                duration = 100
            }.withEndAction {
                fab_layout_add_annual_event.animate().translationYBy(40.toFloat()).apply {
                    duration = 75
                }
            }

        //move add one time event layout up
        fab_layout_add_one_time.animate().translationYBy(-resources.getDimension(R.dimen.standard_155) - 60).apply {
            duration = 100
        }.withEndAction {
            fab_layout_add_one_time.animate().translationYBy(60.toFloat()).apply {
                duration = 75
            }
        }

        fab_show_fab_menu.animate().duration = 100
        //some fancy overrotated animation
        fab_show_fab_menu.animate().rotationBy(80.0f).withEndAction {
            fab_show_fab_menu.animate().rotationBy(-35.0f).apply {
                duration = 75
            }.withEndAction {
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
        fab_layout_add_birthday.animate().translationYBy(resources.getDimension(R.dimen.standard_55))
            .withEndAction {
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

    private fun searchStarted(){

    }

    companion object {

        val EVENT_LIST_FRAGMENT_TAG = "EVENT_LIST"

        @JvmStatic
        fun newInstance(): EventListFragment {
            return EventListFragment()
        }
    }
}
