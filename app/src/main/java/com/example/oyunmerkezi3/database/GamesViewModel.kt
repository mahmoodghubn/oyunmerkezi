package com.example.oyunmerkezi3.database

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
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
    private var game = MutableLiveData<Game?>()

    val arrayList = MutableLiveData<ArrayList<Game>>()

    //private var games = MutableLiveData<List<Game>?>()
    //private var games : LiveData<List<Game>?>? = null
    private val mChildEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val downloadedGame = dataSnapshot.getValue(Game::class.java)
            viewModelScope.launch {
                if( getGame(downloadedGame!!.gameId)==null) {
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
        val myRef = Firebase.database.getReference("game")
        myRef.addChildEventListener(mChildEventListener)
//        myRef.push().setValue(Game(gameId = 1, "gta 5", 99, 70))
//        myRef.push().setValue(Game(gameId = 2, "spider man", 109, 80))
//        myRef.push().setValue(Game(gameId = 3, "batman", 119, 90))
//        myRef.push().setValue(Game(gameId = 4, "assassins", 199, 50))
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


    private val _navigateToDetails = MutableLiveData<Long>()
    val navigateToDetails
        get() = _navigateToDetails

    fun onGameClicked(id: Long) {
        _navigateToDetails.value = id
    }

    fun onGameDetailsNavigated() {
        _navigateToDetails.value = null
    }


}

//    private fun getGame(gameId: Long) {
////        uiScope.launch {
////            game.value = get(gameId)
////        }
//
//        viewModelScope.launch {
//            game.value = get(gameId)
//        }
//    }
//         return withContext(Dispatchers.IO) {
//            database.get(gameId)
//        }
//return database.get(gameId)
