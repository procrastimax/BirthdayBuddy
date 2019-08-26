package com.procrastimax.birthdaybuddy.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.procrastimax.birthdaybuddy.MainActivity
import com.procrastimax.birthdaybuddy.R
import com.procrastimax.birthdaybuddy.handler.EventHandler
import com.procrastimax.birthdaybuddy.handler.NotificationHandler
import com.procrastimax.birthdaybuddy.views.SettingsAdapter


class SettingsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    //save a context instance to use it later in a runnable
    private lateinit var settingsContext: Context

    private val toolbar: Toolbar by lazy {
        activity!!.findViewById<Toolbar>(R.id.toolbar)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (context as MainActivity).setSupportActionBar(toolbar)
        setHasOptionsMenu(true)

        (context as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        (context as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        this.settingsContext = context!!

        toolbar.setTitle(R.string.settings_title)

        viewManager = LinearLayoutManager(view.context)
        viewAdapter = SettingsAdapter(view.context)

        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview_settings).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                (context as MainActivity).supportFragmentManager.popBackStackImmediate()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        Thread(Runnable {
            NotificationHandler.cancelAllNotifications(this.settingsContext, EventHandler.getList())
            NotificationHandler.scheduleListEventNotifications(
                this.settingsContext,
                EventHandler.getList()
            )
        }).start()
        super.onPause()
    }

    companion object {
        @JvmStatic
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
}
