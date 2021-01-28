package com.example.oyunmerkezi3.service

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import androidx.preference.PreferenceManager
import com.example.oyunmerkezi3.utils.CalendarUtil
import com.example.oyunmerkezi3.utils.NotificationTask
import com.google.gson.Gson


class NotificationService : JobIntentService() {
    lateinit var context: Context

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        val platformSharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this)
        val editor = platformSharedPreferences.edit()
        val lastDate =  CalendarUtil(null).getCurrentDate()
        val  gson =  Gson()
        val json = gson.toJson(lastDate)
        editor.putString("last_date", json)
        editor.apply()

    }

    override fun onHandleWork(intent: Intent) {


        val action = intent.action!!
        NotificationTask().executeTask(action, application)
    }

    companion object {
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, NotificationService::class.java, 0, intent)
        }
    }
}

