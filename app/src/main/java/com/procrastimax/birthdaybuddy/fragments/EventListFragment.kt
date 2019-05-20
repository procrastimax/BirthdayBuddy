package com.procrastimax.birthdaybuddy.fragments

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.TextView
import com.procrastimax.birthdaybuddy.MainActivity
import com.procrastimax.birthdaybuddy.R
import com.procrastimax.birthdaybuddy.handler.EventHandler
import com.procrastimax.birthdaybuddy.views.EventAdapter
import com.procrastimax.birthdaybuddy.views.RecycleViewItemDivider
import kotlinx.android.synthetic.main.activity_main.*
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
        setHasOptionsMenu(true)
        (context as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (context as MainActivity).supportActionBar?.setDisplayShowHomeEnabled(false)

        (context as MainActivity).scrollable_toolbar.isTitleEnabled = false
        (context as MainActivity).toolbar.title = getString(R.string.app_name)

        isFABOpen = false

        fab_layout_add_annual_event.visibility = ConstraintLayout.INVISIBLE
        fab_layout_add_birthday.visibility = ConstraintLayout.INVISIBLE
        fab_layout_add_one_time.visibility = ConstraintLayout.INVISIBLE

        viewManager = LinearLayoutManager(view.context)
        viewAdapter = EventAdapter(view.context, this.fragmentManager!!)

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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.toolbar_main, menu)

        // Get the SearchView and set the searchable configuration
        val searchManager = context?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu?.findItem(R.id.toolbar_search)?.actionView as android.support.v7.widget.SearchView).apply {
            //Assume current activity is the searchable activity
            setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))

            setIconifiedByDefault(true)

            //submit button in action bar disabled
            isSubmitButtonEnabled = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.toolbar_search -> {

            }
            R.id.item_help -> {
                helpClicked()
            }
            R.id.item_about -> {
                aboutClicked()
            }
            R.id.item_settings -> {
                settingsClicked()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun helpClicked() {
        //open about fragment
        closeFABMenu(true)
        val ft = fragmentManager!!.beginTransaction()
        ft.replace(
            R.id.fragment_placeholder,
            HelpFragment.newInstance()
        )
        ft.addToBackStack(null)
        ft.commit()
    }

    private fun aboutClicked() {
        //open about fragment
        closeFABMenu(true)
        val ft = fragmentManager!!.beginTransaction()
        ft.replace(
            R.id.fragment_placeholder,
            AboutFragment.newInstance()
        )
        ft.addToBackStack(null)
        ft.commit()
    }

    private fun settingsClicked() {
        //open settings fragment
        closeFABMenu(true)
        val ft = fragmentManager!!.beginTransaction()
        ft.replace(
            R.id.fragment_placeholder,
            SettingsFragment.newInstance()
        )
        ft.addToBackStack(null)
        ft.commit()
    }

    companion object {
        @JvmStatic
        fun newInstance(): EventListFragment {
            return EventListFragment()
        }
    }
}
