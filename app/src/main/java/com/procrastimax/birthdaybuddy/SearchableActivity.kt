package com.procrastimax.birthdaybuddy

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.widget.TextView
import com.procrastimax.birthdaybuddy.handler.SearchHandler
import com.procrastimax.birthdaybuddy.views.EventAdapterSearching
import com.procrastimax.birthdaybuddy.views.RecycleViewItemDivider
import kotlinx.android.synthetic.main.activity_searchable.*

class SearchableActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var eventIndexList = emptyList<Int>().toMutableList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchable)

        setSupportActionBar(toolbar_searchable)

        // Verify the action and get the query
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                search(query)
            }
        }

        if (this.eventIndexList.size == 0) {
            tv_failed_search.visibility = TextView.VISIBLE
            recyclerView_search.visibility = RecyclerView.GONE
        } else {
            tv_failed_search.visibility = TextView.GONE
            recyclerView_search.visibility = RecyclerView.VISIBLE
        }

        this.supportActionBar?.setDisplayShowHomeEnabled(true)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar?.setHomeButtonEnabled(true)

        viewManager = LinearLayoutManager(this)
        viewAdapter = EventAdapterSearching(this, this.eventIndexList)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView_search).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        recyclerView.addItemDecoration(RecycleViewItemDivider(this))
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun search(query: String) {
        supportActionBar?.title = this.resources.getString(R.string.searching_toolbar_title, query)
        val searchTerms = SearchHandler.splitStringToList(query)
        searchTerms?.forEach {
            this.eventIndexList.addAll(SearchHandler.searchOnEventData(it))
        }
        this.eventIndexList = this.eventIndexList.distinct().toMutableList()
    }
}
