package com.procrastimax.birthdaybuddy.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.procrastimax.birthdaybuddy.MainActivity
import com.procrastimax.birthdaybuddy.R
import com.procrastimax.birthdaybuddy.views.SettingsAdapter


class SettingsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (context as MainActivity).changeToolbarState(MainActivity.Companion.ToolbarState.Settings)
        val toolbar = activity!!.findViewById<android.support.v7.widget.Toolbar>(R.id.toolbar)
        val backBtn = toolbar.findViewById<ImageView>(R.id.iv_back_arrow)
        backBtn.setOnClickListener {
            backPressed()
        }

        viewManager = LinearLayoutManager(view.context)
        viewAdapter = SettingsAdapter(view.context)
        
        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview_settings).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun backPressed() {
        (context as MainActivity).onBackPressed()
    }

    companion object {

        @JvmStatic
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
}
