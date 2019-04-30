package com.procrastimax.birthdaybuddy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.widget.ProgressBar
import com.procrastimax.birthdaybuddy.fragments.*
import com.procrastimax.birthdaybuddy.handler.BitmapHandler
import com.procrastimax.birthdaybuddy.handler.EventHandler
import com.procrastimax.birthdaybuddy.handler.IOHandler
import com.procrastimax.birthdaybuddy.models.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_add_new_birthday.*
import kotlinx.android.synthetic.main.fragment_event_list.*
import java.text.DateFormat
import java.util.*

/**
 *
 * TODO:
 *  - BUG: app closes when switched to potrait mode and changing fragments
 *  - landscape mode, bottom up menu for image seleciton in eventbirthday doesnt scroll completely up
 *  - Import/Export, inform user about androids passive backup/ restoring
 *  - when open softkeyboard, scroll scrollview up
 */
class MainActivity : AppCompatActivity() {

    var isLoading: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        EventHandler.clearData()

        IOHandler.registerIO(this)

        lockAppbar()

        if (!IOHandler.isFirstStart()) {
            //read all data from shared prefs, when app didnt start for the first time
            IOHandler.readAll(this)

        } else {
            //on first start write standard settings to shared prefs
            IOHandler.initializeAllSettings()

            addMonthDivider()

            EventHandler.addEvent(
                EventBirthday(
                    EventDate.parseStringToDate("06.02.19", DateFormat.SHORT, Locale.GERMAN),
                    "Procrastimax",
                    "Dev",
                    false
                ),
                this,
                true
            )
        }

        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.add(
            R.id.fragment_placeholder,
            EventListFragment.newInstance(),
            EventListFragment.EVENT_LIST_FRAGMENT_TAG
        )
        ft.commit()

        //start loading bitmap drawables in other thread to not block ui
        Thread(Runnable {
            isLoading = true
            //import all drawables
            //TODO: add checkings
            val success = BitmapHandler.loadAllBitmaps(this)
            isLoading = false

            runOnUiThread {

                if (recyclerView != null) {
                    recyclerView.adapter!!.notifyDataSetChanged()
                }

                //update avatar images from other fragments, when all drawables have been loaded
                if (supportFragmentManager.backStackEntryCount > 0) {
                    val currentFragment =
                        supportFragmentManager.fragments[supportFragmentManager.backStackEntryCount - 1]

                    //current fragment is ShowBirthdayEvent fragment
                    if (currentFragment is ShowBirthdayEvent) {
                        (currentFragment).updateAvatarImage()

                        //current fragment is BirthdayInstanceFragment
                    } else if (currentFragment is BirthdayInstanceFragment) {
                        (currentFragment).updateAvatarImage()
                        (currentFragment).iv_add_avatar_btn.isEnabled = true
                    }
                }

                progress_bar_main.visibility = ProgressBar.GONE
            }
        }).start()

        if (intent != null) {
            if (intent?.getBooleanExtra(FRAGMENT_EXTRA_TITLE_LOADALL, false) == true) {
                val eventID = intent?.getIntExtra(FRAGMENT_EXTRA_TITLE_EVENTID, -1)
                val type = intent?.getStringExtra(FRAGMENT_EXTRA_TITLE_TYPE)
                if (eventID != null && eventID > -1 && type != null) {
                    startFragments(eventID, type)
                }
            }
            intent = null
        }
    }

    fun unlockAppBar() {
        app_bar.isActivated = true
        setAppBarDragging(true)
    }

    fun lockAppbar() {
        this.app_bar.setExpanded(false, false)
        app_bar.isActivated = false
        setAppBarDragging(false)
    }

    private fun setAppBarDragging(isEnabled: Boolean) {
        val params = this.app_bar.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = AppBarLayout.Behavior()
        behavior.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
            override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                return isEnabled
            }
        })
        params.behavior = behavior
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val eventID = intent?.getIntExtra(FRAGMENT_EXTRA_TITLE_EVENTID, -1)
        val type = intent?.getStringExtra(FRAGMENT_EXTRA_TITLE_TYPE)
        if (toolbar.menu.findItem(R.id.toolbar_search)?.actionView != null) {

            (toolbar.menu.findItem(R.id.toolbar_search)?.actionView as android.support.v7.widget.SearchView).apply {
                //close search view
                toolbar.collapseActionView()
            }

            if (eventID != null && eventID > -1 && type != null) {
                startFragments(eventID, type)
            }
        }
    }

    private fun startFragments(eventID: Int, type: String) {
        val bundle = Bundle()
        //do this in more adaptable way
        bundle.putInt(
            FRAGMENT_EXTRA_TITLE_EVENTID,
            eventID
        )

        EventHandler.getEventToEventIndex(eventID)?.let { event ->

            val eventFragment: Fragment? = when (event) {
                is EventBirthday -> {
                    if (type == FRAGMENT_TYPE_SHOW) {
                        ShowBirthdayEvent.newInstance()
                    } else {
                        BirthdayInstanceFragment.newInstance()
                    }
                }
                is AnnualEvent -> {
                    if (type == FRAGMENT_TYPE_SHOW) {
                        ShowAnnualEvent.newInstance()
                    } else {
                        AnnualEventInstanceFragment.newInstance()
                    }
                }
                is OneTimeEvent -> {
                    if (type == FRAGMENT_TYPE_SHOW) {
                        ShowOneTimeEvent.newInstance()
                    } else {
                        OneTimeEventInstanceFragment.newInstance()
                    }
                }
                else -> {
                    null
                }
            }
            if (eventFragment != null) {
                val ft = supportFragmentManager.beginTransaction()
                // add arguments to fragment
                eventFragment.arguments = bundle
                ft.replace(
                    R.id.fragment_placeholder,
                    eventFragment
                )
                ft.addToBackStack(null)
                ft.commit()
            }
        }
    }

    fun addMonthDivider() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, 1)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        for (i in 0 until 12) {
            cal.set(Calendar.MONTH, i)
            EventHandler.addEvent(
                MonthDivider(cal.time, resources.getStringArray(R.array.month_names)[i]),
                this,
                true
            )
        }
    }

    companion object {
        fun convertPxToDp(context: Context, px: Float): Float {
            val metrics = context.resources.displayMetrics
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, metrics)
        }

        fun convertDpToPx(context: Context, dp: Float): Int {
            val metrics = context.resources.displayMetrics
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics).toInt()
        }

        const val FRAGMENT_TYPE_SHOW = "SHOW"
        const val FRAGMENT_TYPE_EDIT = "EDIT"

        const val FRAGMENT_EXTRA_TITLE_TYPE = "TYPE"
        const val FRAGMENT_EXTRA_TITLE_EVENTID = "EVENTID"
        const val FRAGMENT_EXTRA_TITLE_EVENTSTRING = "EVENTSTRING"
        const val FRAGMENT_EXTRA_TITLE_NOTIFICATIONID = "NOTIFICATIONID"
        const val FRAGMENT_EXTRA_TITLE_POSITION = "POSITION"
        const val FRAGMENT_EXTRA_TITLE_LOADALL = "LOADALL"
    }
}
