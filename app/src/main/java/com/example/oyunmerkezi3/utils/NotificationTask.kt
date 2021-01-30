package com.example.oyunmerkezi3.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.example.oyunmerkezi3.R
import com.example.oyunmerkezi3.database.DownloadedGame
import com.example.oyunmerkezi3.database.Game
import com.example.oyunmerkezi3.database.GameDatabase
import com.example.oyunmerkezi3.database.GameDatabaseDao
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson
import kotlinx.coroutines.*

class NotificationTask {
    val newGameNotificationId = "NEW_GAME_NOTIFICATION"
    private val gameChannel = "GAME_CHANNEL"
    lateinit var context: Context
    private var job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)
    lateinit var database: GameDatabaseDao
    val actionShowNotification = "show-notification"

    fun executeTask(action: String, context: Context) {
        if (this.actionShowNotification == action) {
            showNotification(context)
        }
    }

    private fun showNotification(context: Context) {

        val platformsArray: Array<String> = context.resources.getStringArray(R.array.platforms)
        val platformSharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context)
        val activePlatforms = arrayListOf<String>()
        platformSharedPreferences.let { it1 ->
            for (item in platformsArray) {
                if (it1.getBoolean(item, true))
                    activePlatforms.add(item)
            }
        }

        val mPlaceRef = arrayListOf<DatabaseReference>()
        for ((index, item) in activePlatforms.withIndex()) {
            mPlaceRef.add(
                Utils.databaseRef?.child("platforms")!!.child(item)
            )
            mPlaceRef[index].addChildEventListener(mChildEventListener)
            mPlaceRef[index].keepSynced(true)
            mPlaceRef[index].keepSynced(true)
        }

        database = GameDatabase.getInstance(context).gameDatabaseDao
        this.context = context
        createChannel(newGameNotificationId, gameChannel, context)

    }

    private val mChildEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val platformSharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context)
            val gson = Gson()
            val json: String? = platformSharedPreferences.getString("last_date", null)
            var lastDate: Date = CalendarUtil(null).getCurrentDate()
            json?.let { lastDate = gson.fromJson(json, Date::class.java) }
            val downloadedGame = dataSnapshot.getValue(DownloadedGame::class.java)!!

            uiScope.launch {
                if (getGame(downloadedGame.gameId) == null) {
                    insertGame(Game(downloadedGame))
                    if (downloadedGame.publishedDate.isBigger(lastDate)) {
                        val notificationManager = ContextCompat.getSystemService(
                            context,
                            NotificationManager::class.java
                        ) as NotificationManager
                        notificationManager.sendNotification(
                            "${downloadedGame.gameName} is available now in the market",
                            context,
                            Game(downloadedGame)
                        )
                    }

                }
            }
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
            val game = dataSnapshot.getValue(DownloadedGame::class.java)!!
            uiScope.launch {
                val game2 = getGame(game.gameId)!!
                updateGame(Game(game,game2.favorite,game2.showNotification))

                //TODO make sure the changes is only on the price or stock
                if (game2.showNotification) {
                    val notificationManager = ContextCompat.getSystemService(
                        context,
                        NotificationManager::class.java
                    ) as NotificationManager
                    notificationManager.sendNotification(
                        "The price of ${game.gameName} has been changed ",
                        context,
                        Game(game)
                    )
                }
            }
        }

        private fun insertGame(game: Game) {
            uiScope.launch {
                insert(game)
            }
        }

        private suspend fun insert(game: Game) {
            withContext(Dispatchers.IO) {
                database.insert(game)
            }
        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {
            val game = dataSnapshot.getValue(DownloadedGame::class.java)
            deleteGame(Game(game!!))
        }

        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}

        override fun onCancelled(databaseError: DatabaseError) {}
    }

    private suspend fun getGame(gameId: Long): Game? {
        return database.get(gameId)
    }

    private fun updateGame(game: Game) {
        uiScope.launch {
            update(game)
        }
    }

    private suspend fun update(game: Game) {
        withContext(Dispatchers.IO) {
            database.update(game)
        }
    }


    private fun deleteGame(game: Game) {
        uiScope.launch {
            delete(game)
        }
    }

    private suspend fun delete(game: Game) {
        withContext(Dispatchers.IO) {
            database.delete(game)
        }
    }

    private fun createChannel(channelId: String, channelName: String, context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Changes On Games"
            val notificationManager = context.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}