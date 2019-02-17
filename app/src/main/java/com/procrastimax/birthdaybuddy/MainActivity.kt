package com.procrastimax.birthdaybuddy

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.procrastimax.birthdaybuddy.models.EventBirthday
import com.procrastimax.birthdaybuddy.models.EventDay
import com.procrastimax.birthdaybuddy.models.EventHandler

import kotlinx.android.synthetic.main.activity_main.*
import java.text.DateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        EventDataIO.registerIO(this.applicationContext)

        val textView: TextView = findViewById<TextView>(R.id.textView)

        textView.text = EventHandler.getEvents().toString()

        fab.setOnClickListener { view ->

            EventHandler.addEvent(
                EventBirthday(
                    EventDay.parseStringToDate("06.02.00", DateFormat.SHORT, Locale.GERMAN),
                    "Procrastimax",
                    EventHandler.getLastIndex().toString(),
                    false
                )
            )

            EventDataIO.writeEventToFile(
                EventHandler.getLastIndex(),
                EventHandler.getValueToKey(EventHandler.getLastIndex())!!
            )

            textView.text = EventHandler.getEvents().toString()

            Snackbar.make(view, "BirthdayEventAdded", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
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
}
