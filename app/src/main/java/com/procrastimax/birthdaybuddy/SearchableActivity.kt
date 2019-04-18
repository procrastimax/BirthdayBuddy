package com.procrastimax.birthdaybuddy

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import com.procrastimax.birthdaybuddy.handler.SearchHandler
import com.procrastimax.birthdaybuddy.views.EventAdapter_Searching
import com.procrastimax.birthdaybuddy.views.RecycleViewItemDivider
import kotlinx.android.synthetic.main.activity_searchable.*

//TODO: home button isnt clickable
class SearchableActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private val eventIndexList = emptyList<Int>().toMutableList()

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

        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar?.setDisplayShowHomeEnabled(true)
        this.supportActionBar?.setHomeButtonEnabled(true)

        viewManager = LinearLayoutManager(this)
        viewAdapter = EventAdapter_Searching(this, this.eventIndexList)

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
        supportActionBar?.title = "Search for: $query"
        this.eventIndexList.addAll(SearchHandler.searchOnEventData(query).distinct())
    }
}
