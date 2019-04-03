package com.procrastimax.birthdaybuddy.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.procrastimax.birthdaybuddy.MainActivity
import com.procrastimax.birthdaybuddy.R
import com.procrastimax.birthdaybuddy.views.AboutAdapter

class AboutFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        super.onViewCreated(view, savedInstanceState)
        (context as MainActivity).changeToolbarState(MainActivity.Companion.ToolbarState.Fragment)
        val toolbar = activity!!.findViewById<android.support.v7.widget.Toolbar>(R.id.toolbar)
        val tv_toolbar_title = toolbar.findViewById<TextView>(R.id.tv_title_toolbar_inFragment)
        tv_toolbar_title.text = resources.getString(R.string.main_menu_item_about)
        val backBtn = toolbar.findViewById<ImageView>(R.id.iv_back_arrow)
        backBtn.setOnClickListener {
            backPressed()
        }

        viewManager = LinearLayoutManager(view.context)
        viewAdapter = AboutAdapter()

        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView_about).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun backPressed() {
        (context as MainActivity).onBackPressed()
    }

    companion object {
        fun newInstance(): AboutFragment {
            return AboutFragment()
        }
    }
}
