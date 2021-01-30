package com.example.oyunmerkezi3

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import com.example.oyunmerkezi3.databinding.ActivityMainBinding
import com.example.oyunmerkezi3.shared_preferences.SharedPreferenceBooleanLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.oyunmerkezi3.database.GameDatabase
import com.example.oyunmerkezi3.database.GamesViewModel
import com.example.oyunmerkezi3.database.GamesViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var gamesViewModel: GamesViewModel
    lateinit var platformSharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("UNUSED_VARIABLE")
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        setContentView(binding.root)

        platformSharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this)
        editor = platformSharedPreferences.edit()

        val application = requireNotNull(this).application
        val dataSource = GameDatabase.getInstance(application).gameDatabaseDao
        val currentPlatform = platformSharedPreferences.getString("current", "PS4")

        val viewModelFactory =
            GamesViewModelFactory(dataSource, application, currentPlatform!!)
        gamesViewModel =
            ViewModelProvider(this, viewModelFactory).get(GamesViewModel::class.java)

        //delete platform games when it is not selected any more
        deletePlatformGames()

        drawerLayout = binding.drawerLayout

        val navController = this.findNavController(R.id.myNavHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)
        // prevent nav gesture if not on start destination
        navController.addOnDestinationChangedListener { nc: NavController, nd: NavDestination, _: Bundle? ->
            if (nd.id == nc.graph.startDestination) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

    }

    private fun deletePlatformGames() {
        val platformsArray: Array<String> = resources.getStringArray(R.array.platforms)
        val oldSharedPreferences: Array<String> =
            resources.getStringArray(R.array.old_shared_preference)

        //observing the changes in shared preferences and delete data accordingly
        var sharedPreferenceStringLiveData: SharedPreferenceBooleanLiveData

        for ((index, platformItem) in platformsArray.withIndex()) {
            sharedPreferenceStringLiveData =
                SharedPreferenceBooleanLiveData(platformSharedPreferences, platformItem, true)
            sharedPreferenceStringLiveData.getBooleanLiveData(platformItem, true)
                .observe(this, Observer { checked ->
                    if (platformSharedPreferences.getBoolean(
                            //TODO change the preference live data to list
                            // or proceed to another item by taking the active items from the games fragment

                            //TODO if the user removes all the platforms and add platform different from
                            // the current platform we should proceed to the new chosen platform

                            //TODO when click on the platform that we are already on

                            oldSharedPreferences[index],
                            true
                        ) != checked
                    ) {
                        editor.putBoolean(oldSharedPreferences[index], checked!!)
                        editor.apply()
                        if (!checked) {// delete the data from database
                            gamesViewModel.deletePlatformFromDataBaseWhenSharedPreferenceChanges(
                                platformItem
                            )
                        }
                    }
                })
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        //return navController.navigateUp()
        return NavigationUI.navigateUp(navController, drawerLayout)

    }
}