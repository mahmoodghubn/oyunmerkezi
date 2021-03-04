package com.example.oyunmerkezi3

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.oyunmerkezi3.bottomSheet.GamePriceAdapter
import com.example.oyunmerkezi3.database.GameDatabase
import com.example.oyunmerkezi3.database.GamesViewModel
import com.example.oyunmerkezi3.database.GamesViewModelFactory
import com.example.oyunmerkezi3.databinding.ActivityMainBinding
import com.example.oyunmerkezi3.databinding.PriceBottomSheetBinding
import com.example.oyunmerkezi3.shared_preferences.SharedPreferenceBooleanLiveData
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var gamesViewModel: GamesViewModel
    lateinit var platformSharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("UNUSED_VARIABLE")
        binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        setContentView(binding.root)

        platformSharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this)
        editor = platformSharedPreferences.edit()

        val application = requireNotNull(this).application
        val dataSource = GameDatabase.getInstance(application).gameDatabaseDao

        val viewModelFactory =
            GamesViewModelFactory(dataSource, application)
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
        var sharedPreferenceBooleanLiveData: SharedPreferenceBooleanLiveData

        for ((index, platformItem) in platformsArray.withIndex()) {
            sharedPreferenceBooleanLiveData =
                SharedPreferenceBooleanLiveData(platformSharedPreferences, platformItem, true)
            sharedPreferenceBooleanLiveData.getBooleanLiveData(platformItem, true)
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

    fun bottomSheetFunction(
        bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>,
        recyclerView: RecyclerView,
        priceBottomSheetBinding: PriceBottomSheetBinding,
        container: ViewGroup?
    ) {
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }

                    BottomSheetBehavior.STATE_EXPANDED -> {
                        //inflate the chosen games in the bottom sheet
                        val adapter1 = GamePriceAdapter(
                            gamesViewModel
                        )
                        val layoutManager = LinearLayoutManager(application)
                        layoutManager.orientation = LinearLayoutManager.VERTICAL
                        recyclerView.layoutManager = layoutManager
                        recyclerView.adapter = adapter1
                        adapter1.data = gamesViewModel.buyingCheckBoxArray
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {

                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {

                    }
                    BottomSheetBehavior.STATE_SETTLING -> {

                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })
        val gesture = GestureDetector(
            this,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onDown(e: MotionEvent): Boolean {
                    return true
                }

                override fun onFling(
                    e1: MotionEvent, e2: MotionEvent, velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    if (priceBottomSheetBinding.root.visibility == View.VISIBLE) {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                    return super.onFling(e1, e2, velocityX, velocityY)
                }
            })
        container!!.setOnTouchListener { v, event ->
            if (event!!.action == MotionEvent.ACTION_UP)
                v!!.performClick()

            gesture.onTouchEvent(event)
        }
    }

    fun sendWhatsAppMessage(actionButton: FloatingActionButton) {
        //whatsApp button
        val number = "+905465399410"
        val url = "https://api.whatsapp.com/send?phone=$number"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        //TODO need test by uninstalling whatsapp
        actionButton.setOnClickListener() {
            try {
                startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                //TODO show a message
            }
        }
    }

}