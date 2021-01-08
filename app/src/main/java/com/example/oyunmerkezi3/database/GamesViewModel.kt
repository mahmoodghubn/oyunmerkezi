package com.example.oyunmerkezi3.database

import android.app.Application
import androidx.lifecycle.*
import com.example.oyunmerkezi3.utils.Utils
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.coroutines.*

class GamesViewModel(
    val database: GameDatabaseDao,
    application: Application,
    x: Int//this value comes from the orderBy fragment to determine which order chose
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //games variable is observable by games fragment and contain the list of games from the database
    var games: LiveData<List<Game>?> = Transformations.map(database.getAllGames()) {
        it?.sortedBy { it.gameId }
    }

    //chilEvenListener is listening to changes in firebase database
    private val mChildEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val downloadedGame = dataSnapshot.getValue(Game::class.java)
            uiScope.launch {
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

        games = when (x) {
            1 -> {
                Transformations.map(games) {
                    it?.sortedBy { it.gameName }
                }
            }
            2 -> {
                Transformations.map(games) {
                    it?.sortedByDescending { it.gameName }
                }
            }
            3 -> {
                Transformations.map(games) {
                    it?.sortedBy { it.publishedDate }
                }
            }
            4 -> {
                Transformations.map(games)
                {
                    it?.sortedByDescending { it.publishedDate }
                }
            }
            5 -> {
                Transformations.map(games) {
                    it?.sortedBy { it.sellingPrice }
                }
            }
            6 -> {
                Transformations.map(games) {
                    it?.sortedByDescending { it.sellingPrice }
                }
            }
            7 -> {
                Transformations.map(games) {
                    it?.sortedBy { it.hours }
                }
            }
            8 -> {
                Transformations.map(games) {
                    it?.sortedByDescending { it.hours }
                }
            }
            9 -> {
                Transformations.map(games) {
                    it?.sortedBy { it.gameRating }
                }
            }
            else -> {
                Transformations.map(games) {
                    it?.sortedByDescending { it.hours }
                }
            }
        }

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

    //when finish navigating call this function
    fun onGameDetailsNavigated() {
        _navigateToDetails.value = null
    }
}
