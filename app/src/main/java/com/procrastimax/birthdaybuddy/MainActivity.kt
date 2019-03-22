package com.procrastimax.birthdaybuddy

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.ProgressBar
import com.procrastimax.birthdaybuddy.fragments.BirthdayInstanceFragment
import com.procrastimax.birthdaybuddy.fragments.EventListFragment
import com.procrastimax.birthdaybuddy.fragments.ShowBirthdayEvent
import com.procrastimax.birthdaybuddy.handler.DrawableHandler
import com.procrastimax.birthdaybuddy.handler.EventHandler
import com.procrastimax.birthdaybuddy.models.EventBirthday
import com.procrastimax.birthdaybuddy.models.EventDate
import com.procrastimax.birthdaybuddy.models.MonthDivider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_add_new_birthday.*
import kotlinx.android.synthetic.main.fragment_event_list.*
import java.text.DateFormat
import java.util.*
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout

/**
 *
 * TODO:
 *  - workout different localizations, f.e. curently the dates include substrings for german locales like .substring(0..5) => obviously dont do this
 *  - bug when localization is changed after first start of app -> add possibility to change all encodings at app start when error occurs -> fix this by only use one format for saving
 *  - when language of devices changes, month divider names should also change -> save localization and compare to last start?
 *  - dont show last seperation character in list view ( -> first point)
 *  - add checking for existing forename/surname pair when adding a new birthday/event
 *  - BUG: app closes when switched to potrait mode and changing fragments
 *  - maybe delete all vector darwables and use android defaults in library, to save memory space
 *  - maybe change collapsable toolbar to invisible, and change with standard toolbar
 */
class MainActivity : AppCompatActivity() {

    private val settings_shared_pref_file_name = "com.procrasticmax.birthdaybuddy.settings_shared_pref"
    private val settings_shared_pref_isFirstStart = "isFirstStart"
    private val shared_pref_settings by lazy {
        this.getSharedPreferences(
            this.settings_shared_pref_file_name,
            Context.MODE_PRIVATE
        )
    }

    var isLoading: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //get application context and set shared pref context
        EventDataIO.registerIO(this.applicationContext)

        //read all data from shared prefs
        EventHandler.addMap(EventDataIO.readAll())

        if (isFirstStart()) {
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, 1)
            cal.set(Calendar.DAY_OF_MONTH, 1)
            cal.set(Calendar.HOUR_OF_DAY, 1)
            for (i in 0 until 12) {
                cal.set(Calendar.MONTH, i)
                EventHandler.addEvent(
                    MonthDivider(cal.time, resources.getStringArray(R.array.month_names)[i]),
                    this.applicationContext,
                    true
                )
            }

            EventHandler.addEvent(
                EventBirthday(
                    EventDate.parseStringToDate("06.02.00", DateFormat.SHORT, Locale.GERMAN),
                    "Procrastimax",
                    "Dev",
                    false
                ),
                this.applicationContext,
                true
            )
        }

        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.add(R.id.fragment_placeholder, EventListFragment.newInstance(), EventListFragment.EVENT_LIST_FRAGMENT_TAG)
        ft.commit()

        //start loading bitmap drawables in other thread to not block ui
        Thread(Runnable {
            isLoading = true
            //import all drawables
            val success = DrawableHandler.loadAllDrawables(this.applicationContext)
            isLoading = false

            runOnUiThread {

                if (success == false) {
                    DrawableHandler.showMissingImageAlertDialog(this)
                }

                if (recyclerView != null) {
                    recyclerView.adapter!!.notifyDataSetChanged()
                }

                //update avatar images from other fragments, when all drawables have been loaded
                if (supportFragmentManager.backStackEntryCount > 0) {
                    val current_fragment =
                        supportFragmentManager.fragments[supportFragmentManager.backStackEntryCount - 1]

                    //current fragment is ShowBirthdayEvent fragment
                    if (current_fragment is ShowBirthdayEvent) {
                        (current_fragment).updateAvatarImage()

                        //current fragment is BirthdayInstanceFragment
                    } else if (current_fragment is BirthdayInstanceFragment) {
                        (current_fragment).updateAvatarImage()
                        (current_fragment).iv_add_avatar_btn.isEnabled = true
                    }
                }

                progress_bar_main.visibility = ProgressBar.GONE
            }
        }).start()
    }

    /**
     * getMonthFromIndex returns a month name specified in the string resources by an index
     * starts at 0 with january
     * @param index: Int
     * @return String
     */
    private fun getMonthFromIndex(index: Int): String {
        return resources.getStringArray(R.array.month_names)[index]
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

    fun changeToolbarState(state: ToolbarState) {
        when (state) {
            Companion.ToolbarState.Default -> {

                if (toolbar.childCount > 0) {
                    toolbar.removeAllViews()
                }
                toolbar.addView(
                    layoutInflater.inflate(
                        R.layout.toolbar_default,
                        findViewById(android.R.id.content),
                        false
                    )
                )
                toolbar.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.colorPrimary
                    )
                )

                closeAppBar(false)
                lockAppBar()
            }

            Companion.ToolbarState.EditEvent -> {
                lockAppBar()

                if (toolbar.childCount > 0) {
                    toolbar.removeAllViews()
                }
                toolbar.addView(
                    layoutInflater.inflate(
                        R.layout.toolbar_edit_event,
                        findViewById(android.R.id.content),
                        false
                    )
                )
                toolbar.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.material_light_white_background
                    )
                )

                closeAppBar(false)
                lockAppBar()
            }

            Companion.ToolbarState.ShowEvent -> {
                if (toolbar.childCount > 0) {
                    toolbar.removeAllViews()
                }
                toolbar.addView(
                    layoutInflater.inflate(
                        R.layout.toolbar_show_event,
                        findViewById(android.R.id.content),
                        false
                    )
                )
                toolbar.setBackgroundResource(
                    android.R.color.transparent
                )

                closeAppBar(false)
                lockAppBar()
            }
        }
    }

    fun closeAppBar(animated: Boolean = true) {
        this.app_bar.setExpanded(false, animated)
    }

    fun openAppBar(animated: Boolean = true) {
        this.app_bar.setExpanded(true, animated)
    }


    fun lockAppBar() {

        val params = app_bar.layoutParams as CoordinatorLayout.LayoutParams
        params.behavior = AppBarLayout.Behavior()
        val behavior = params.behavior as AppBarLayout.Behavior
        behavior.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
            override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                return false
            }
        })
    }

    fun unlockAppBar() {

        val params = app_bar.layoutParams as CoordinatorLayout.LayoutParams
        params.behavior = AppBarLayout.Behavior()
        val behavior = params.behavior as AppBarLayout.Behavior
        behavior.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
            override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                return true
            }
        })
    }

    companion object {
        fun convertPxToDp(context: Context, dp: Float): Float {
            return dp * context.resources.displayMetrics.density
        }

        enum class ToolbarState {
            Default,
            EditEvent,
            ShowEvent
        }
    }
}
