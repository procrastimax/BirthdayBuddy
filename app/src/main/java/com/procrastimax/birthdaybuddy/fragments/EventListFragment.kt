package com.procrastimax.birthdaybuddy.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.procrastimax.birthdaybuddy.R
import com.procrastimax.birthdaybuddy.views.EventAdapter
import com.procrastimax.birthdaybuddy.views.RecycleViewItemDivider
import kotlinx.android.synthetic.main.fragment_event_list.*

class EventListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var isFABOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewManager = LinearLayoutManager(view.context)
        viewAdapter = EventAdapter(view.context)

        val toolbar = activity!!.findViewById<android.support.v7.widget.Toolbar>(R.id.toolbar)
        toolbar.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
        toolbar.setContentInsetsAbsolute(24, 0)

        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
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
            val ft = fragmentManager!!.beginTransaction()
            ft.replace(
                R.id.fragment_placeholder,
                BirthdayInstanceFragment.newInstance()
            )
            ft.addToBackStack(null)
            ft.commit()
            closeFABMenu()
        }

        fab_add_anniversary.setOnClickListener {
            val ft = fragmentManager!!.beginTransaction()
            ft.replace(
                R.id.fragment_placeholder,
                AnniversaryInstanceFragment.newInstance()
            )
            ft.addToBackStack(null)
            ft.commit()
            closeFABMenu()
        }
    }

    private fun showFABMenu() {
        isFABOpen = true
        fab_add_birthday.show()
        fab_add_anniversary.show()
        fab_add_birthday.animate().translationY(-resources.getDimension(R.dimen.standard_55))
        fab_add_anniversary.animate().translationY(-resources.getDimension(R.dimen.standard_105))

        fab_show_fab_menu.animate().rotationBy(45.0f)
    }

    private fun closeFABMenu() {
        isFABOpen = false
        fab_add_birthday.hide()
        fab_add_anniversary.hide()
        fab_add_birthday.animate().translationY(0.toFloat())
        fab_add_anniversary.animate().translationY(0.toFloat())
        fab_show_fab_menu.animate().rotationBy(-45.0f)
    }

    companion object {
        @JvmStatic
        fun newInstance(): EventListFragment {
            return EventListFragment()
        }
    }
}
