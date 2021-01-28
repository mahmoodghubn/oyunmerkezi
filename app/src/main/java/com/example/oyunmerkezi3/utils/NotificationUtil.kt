package com.example.oyunmerkezi3.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.example.oyunmerkezi3.R
import com.example.oyunmerkezi3.activity.DetailActivity
import com.example.oyunmerkezi3.database.Game


fun NotificationManager.sendNotification(messageBody: String, application: Context, game: Game) {

    // Create an Intent for the activity you want to start
    val resultIntent = Intent(application, DetailActivity::class.java)
    resultIntent.putExtra("game", game)
    // Create the TaskStackBuilder
    val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(application).run {
        // Add the intent, which inflates the back stack
        addNextIntentWithParentStack(resultIntent)
        // Get the PendingIntent containing the entire back stack
        getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    //TODO set the style of the notification to messaging style
    val builder = NotificationCompat.Builder(
        application, NotificationTask().newGameNotificationId
    ).setSmallIcon(R.drawable.ic_outline_search_24)
        .setContentTitle("OYUNMERKEZI")
        .setContentText(messageBody)
        .setContentIntent(resultPendingIntent)
        .setAutoCancel(true)
    notify(0, builder.build())
}


