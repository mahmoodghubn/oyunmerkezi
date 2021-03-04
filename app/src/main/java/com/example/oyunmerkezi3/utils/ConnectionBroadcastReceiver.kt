package com.example.oyunmerkezi3.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.oyunmerkezi3.database.Game

abstract class ConnectionBroadcastReceiver : BroadcastReceiver() {
    companion object {
        @JvmStatic
        fun registerWithoutAutoUnregister(context: Context, connectionBroadcastReceiver: ConnectionBroadcastReceiver) {
            context.registerReceiver(connectionBroadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        }

        @JvmStatic
        fun registerToFragmentAndAutoUnregister(context: Context, fragment: Fragment, games: MediatorLiveData<List<Game>?>, connectionBroadcastReceiver: ConnectionBroadcastReceiver) {
            var isRegistered = false
            games.observe(fragment, { it ->
                it?.let {
                    val applicationContext = context.applicationContext
                    if (it.isEmpty()){
                        isRegistered = true
                        registerWithoutAutoUnregister(applicationContext, connectionBroadcastReceiver)
                        fragment.lifecycle.addObserver(object : LifecycleObserver {
                            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
                            fun onPause() {
                                try {
                                    applicationContext.unregisterReceiver(connectionBroadcastReceiver)
                                } catch (e: IllegalArgumentException) {
                                    // Check wether we are in debug mode
                                    e.printStackTrace()
                                }
                            }
                        })
                    }else if (isRegistered){
                        isRegistered = false
                        try {
                            applicationContext.unregisterReceiver(connectionBroadcastReceiver)
                        } catch (e: IllegalArgumentException) {
                            // Check wether we are in debug mode
                            e.printStackTrace()
                        }                    }
                }
            })


        }

        @JvmStatic
        fun registerToActivityAndAutoUnregister(activity: AppCompatActivity, connectionBroadcastReceiver: ConnectionBroadcastReceiver) {
            registerWithoutAutoUnregister(activity, connectionBroadcastReceiver)
            activity.lifecycle.addObserver(object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
                fun onPause() {
                    try {
                        activity.unregisterReceiver(connectionBroadcastReceiver)
                    } catch (e: IllegalArgumentException) {
                        // Check wether we are in debug mode
                            e.printStackTrace()
                    }
//                    activity.unregisterReceiver(connectionBroadcastReceiver)
                }
            })
        }

        @JvmStatic
        fun hasInternetConnection(context: Context): Boolean {
            val info = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
            return !(info == null || !info.isConnectedOrConnecting)
        }

    }

    override fun onReceive(context: Context, intent: Intent) {
        val hasConnection = !intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)
        onConnectionChanged(hasConnection)
    }

    abstract fun onConnectionChanged(hasConnection: Boolean)
}