package com.example.oyunmerkezi3.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.oyunmerkezi3.utils.Utils
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.coroutines.*


class GamesViewModel(
    val database: GameDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    var games: LiveData<List<Game>?>? = database.getAllGames()
    public val mChildEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val downloadedGame = dataSnapshot.getValue(Game::class.java)
            viewModelScope.launch {
                if (getGame(downloadedGame!!.gameId) == null) {
                    insertGame(downloadedGame)
                }
            }
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
            val game = dataSnapshot.getValue(Game::class.java)
            updateGame(game!!)
        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {
            val game = dataSnapshot.getValue(Game::class.java)
            deleteGame(game!!)
        }

        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}

        override fun onCancelled(databaseError: DatabaseError) {}
    }

    init {
        val mPlaceRef = Utils.databaseRef?.child("game")
        mPlaceRef?.addChildEventListener(mChildEventListener)
        mPlaceRef!!.keepSynced(true)
    }

    private suspend fun getGame(gameId: Long): Game? {
        return database.get(gameId)
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

    private val _navigateToDetails = MutableLiveData<Game>()
    val navigateToDetails
        get() = _navigateToDetails

    fun onGameClicked(game: Game) {
        _navigateToDetails.value = game
    }

    fun onGameDetailsNavigated() {
        _navigateToDetails.value = null
    }
}
