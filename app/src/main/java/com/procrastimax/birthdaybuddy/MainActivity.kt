package com.procrastimax.birthdaybuddy

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.ViewDebug
import com.procrastimax.birthdaybuddy.models.EventBirthday
import com.procrastimax.birthdaybuddy.models.EventDay
import com.procrastimax.birthdaybuddy.handler.EventHandler
import com.procrastimax.birthdaybuddy.views.EventAdapter
import com.procrastimax.birthdaybuddy.views.RecycleViewItemDivider

import kotlinx.android.synthetic.main.activity_main.*
import java.text.DateFormat
import java.util.*

/**
 *
 * TODO:
 *  - bug when localization is changed after first start of app
 */
class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private val settings_shared_pref_file_name = "com.procrasticmax.birthdaybuddy.settings_shared_pref"
    private val settings_shared_pref_isFirstStart = "isFirstStart"
    private val shared_pref_settings by lazy {
        this.getSharedPreferences(
            this.settings_shared_pref_file_name,
            Context.MODE_PRIVATE
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        EventDataIO.registerIO(this.applicationContext)

        EventHandler.addMap(EventDataIO.readAll())

        if (isFirstStart()) {
            EventHandler.addEvent(
                EventBirthday(
                    EventDay.parseStringToDate("06.02.00", DateFormat.SHORT, Locale.GERMAN),
                    "Procrastimax",
                    EventHandler.getLastIndex().toString(),
                    false
                ),
                true
            )
        }

        viewManager = LinearLayoutManager(this)
        viewAdapter = EventAdapter(this.applicationContext)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        recyclerView.addItemDecoration(RecycleViewItemDivider(this.applicationContext))

        fab.setOnClickListener { view ->
            //directly write after adding
            EventHandler.generateRandomEventDates(1, true)
            recyclerView.adapter!!.notifyItemInserted(EventHandler.getLastIndex())

            Snackbar.make(view, "BirthdayEventAdded", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    private fun isFirstStart(): Boolean {
        return if (shared_pref_settings.getBoolean(settings_shared_pref_isFirstStart, true)) {
            //change isFirstStatus in shared prefs to false
            this.shared_pref_settings.edit().putBoolean(settings_shared_pref_isFirstStart, false).apply()
            true
        } else {
            false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        fun convertPxToDp(context: Context, dp : Float) : Float{
            return dp * context.resources.displayMetrics.density
        }
    }
}
