package com.silverkeytech.android_rivers

import com.actionbarsherlock.app.SherlockFragmentActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import com.actionbarsherlock.view.Menu
import com.actionbarsherlock.view.MenuItem

public open class MainWithFragmentsActivity(): SherlockFragmentActivity() {
    class object {
        public val TAG: String = javaClass<MainWithFragmentsActivity>().getSimpleName()
    }

    val DEFAULT_SUBSCRIPTION_LIST = "http://hobieu.apphb.com/api/1/default/riverssubscription"

    var mode: MainActivityMode = MainActivityMode.RIVER

    var currentTheme: Int? = null
    var isOnCreate: Boolean = true

    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        currentTheme = this.getVisualPref().getTheme()
        isOnCreate = true
        setTheme(currentTheme!!)
        getMain().flags.reset()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_with_fragments)

        var actionBar = getSupportActionBar()!!
        actionBar.setDisplayShowHomeEnabled(false) //hide the app icon.
        actionBar.setDisplayShowTitleEnabled(true)

        showAndHide(showRiver = true, showRss = false, showCollection = false)
        setTitle()
    }


    public override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (menu != null){
            val downloadAll = menu.findItem(R.id.main_menu_download_all_rivers).andHide()
            val newCollection = menu.findItem(R.id.main_menu_collection_add_new).andHide()
            val backward = menu.findItem(SWITCH_BACKWARD)
            val sort = menu.findItem(R.id.main_menu_sort).andHide()
            val tryout = menu.findItem(R.id.main_menu_tryout).andHide()

            val addDialog = menu.findItem(R.id.main_menu_show_add_dialog).andHide()
            val refresh = menu.findItem(R.id.main_menu_refresh).andHide()
            val opmlImport = menu.findItem(R.id.main_menu_show_import_opml_dialog).andHide()

            //todo: make a configuration based to turn this on and off
            val updates = menu.findItem(R.id.main_menu_updates).andHide()

            when(mode){
                MainActivityMode.RIVER -> {
                    downloadAll.setVisible(true)
                    backward!!.setEnabled(false)
                    //val nextSort = nextSortCycle()
                    //Log.d(TAG, "The next sort cycle from current ${this.getContentPref().getRiverBookmarksSorting()} is $nextSort")
                    //setSortButtonText(sort, nextSort)
                    sort.setVisible(true)
                    refresh.setVisible(true)
                    addDialog.setVisible(true)
                    addDialog.setTitle("Add River")
                }
                MainActivityMode.RSS -> {
                    backward!!.setEnabled(true)
                    addDialog.setVisible(true)
                    addDialog.setTitle("Add RSS")
                    opmlImport.setVisible(true)
                }
                MainActivityMode.COLLECTION -> {
                    newCollection.setVisible(true)
                    backward!!.setEnabled(true)
                }
                else -> {
                }
            }
        }

        return super.onPrepareOptionsMenu(menu)
    }

    public override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = getSupportMenuInflater()!!
        inflater.inflate(R.menu.main_menu, menu)

        //fixed top menu
        menu?.add(0, SWITCH_BACKWARD, 0, "<")
        ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

        menu?.add(0, SWITCH_FORWARD, 0, ">")
        ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

        menu?.add(0, EXPLORE, 0, "MORE NEWS")
        ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

        return super.onCreateOptionsMenu(menu)
    }

    fun showAndHide(showRiver : Boolean, showRss : Boolean, showCollection : Boolean){
        val riverFragment = this.findFragmentById(R.id.main_rivers_fragment)
        val rssFragment = this.findFragmentById(R.id.main_rss_fragment)
        val collectionFragment = this.findFragmentById(R.id.main_collection_fragment)

        val transaction = this.beginFragmentTransaction()

        if (showRiver) transaction.show(riverFragment) else transaction.hide(riverFragment)
        if (showRss) transaction.show(rssFragment) else transaction.hide(rssFragment)
        if (showCollection) transaction.show(collectionFragment) else transaction.hide(collectionFragment)

        transaction.commit()
    }

    fun setTitle() {
        val actionBar = getSupportActionBar()!!
        when(mode){
            MainActivityMode.RIVER -> actionBar.setTitle("Rivers")
            MainActivityMode.RSS -> actionBar.setTitle("RSS")
            MainActivityMode.COLLECTION -> actionBar.setTitle("Collections")
            else -> {
            }
        }
    }

    public override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.getItemId()) {
            EXPLORE -> {
                return false
            }
            SWITCH_FORWARD -> {
                changeModeForward()
                displayModeContent(mode, false)
                setTitle()
                this.supportInvalidateOptionsMenu()
                return true
            }
            SWITCH_BACKWARD -> {
                changeModeBackward()
                displayModeContent(mode, false)
                setTitle()
                this.supportInvalidateOptionsMenu()
                return true
            }
            R.id.main_menu_help->{
                downloadOpml(this, PreferenceDefaults.CONTENT_OUTLINE_HELP_SOURCE, getString(R.string.help)!!)
                return true
            }
            else ->
                return super.onOptionsItemSelected(item)
        }
    }


    fun displayModeContent(mode: MainActivityMode, downloadRiverBookmarksFromInternetIfNoneExisted: Boolean) {
        when(mode){
            MainActivityMode.RIVER -> {
                showAndHide(showRiver = true, showRss = false, showCollection = false)
            }
            MainActivityMode.RSS -> {
                showAndHide(showRiver = false, showRss = true, showCollection = false)
            }
            MainActivityMode.COLLECTION ->{
                showAndHide(showRiver = false, showRss = false, showCollection = true)
            }
            MainActivityMode.PODCASTS -> {
                startPodcastActivity(this)
                //we have to do this because this navigation involves two activities. Podcast activity is at the end of this navigation
                //When you are at podcast activity, you can only go back. When you click back, it will end podcast activity
                //and it will call the onresume event of MainActivity. Now we have to make sure that this activity is at its last state
                //which is MainActivityMode.COLLECTION
                this.mode = MainActivityMode.COLLECTION //this is the anchor when you return
            }
            else -> {
            }
        }
    }

    //this is one directional forward
    fun changeModeForward() {
        val currentMode = mode
        mode = when (currentMode){
            MainActivityMode.RIVER -> MainActivityMode.RSS
            MainActivityMode.RSS -> MainActivityMode.COLLECTION
            MainActivityMode.COLLECTION -> MainActivityMode.PODCASTS
            else -> MainActivityMode.RIVER
        }
    }

    //this is one directional bacwkward
    fun changeModeBackward() {
        val currentMode = mode
        mode = when (currentMode){
            MainActivityMode.COLLECTION -> MainActivityMode.RSS
            MainActivityMode.RSS -> MainActivityMode.RIVER
            else -> MainActivityMode.RIVER
        }
    }

    val SWITCH_BACKWARD: Int = 0
    val SWITCH_FORWARD: Int = 1
    val EXPLORE: Int = 2

}

fun SherlockFragmentActivity.findFragmentById(id : Int) : Fragment{
    return this.getSupportFragmentManager()!!.findFragmentById(id)!!
}

fun SherlockFragmentActivity.beginFragmentTransaction() : FragmentTransaction {
    return this.getSupportFragmentManager()!!.beginTransaction()!!
}