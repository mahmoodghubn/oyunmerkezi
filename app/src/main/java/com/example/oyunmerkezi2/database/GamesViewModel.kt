package com.example.oyunmerkezi2.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

class GamesViewModel(
    val database: GameDatabaseDao,
    application: Application
) : AndroidViewModel(application) {
    val arrayList = MutableLiveData<List<Game>?>()
    init {
        val game:Game = Game(gameName = "batman",sellingPrice = 33,buyingPrice = 90)
        val game2:Game = Game(gameName = "super",sellingPrice = 33,buyingPrice = 90)
        arrayList.value = mutableListOf(game,game2)
    }
    private var viewModelJob = Job()
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
    private val uiScope = CoroutineScope(Dispatchers.Main +  viewModelJob)
    //private var games = MutableLiveData<List<Game>?>()
    //private var games : LiveData<List<Game>?>? = null
     var games : LiveData<List<Game>?>? = database.getAllGames()
    //private var game = MutableLiveData<Game?>()



    var bool:Boolean = true

    init {
        if (bool)
        for (item in arrayList.value!!){
            insertGame(item)
        }
        bool = false
    }
    private fun insertGame(game:Game) {
        uiScope.launch {
            insert(game)
        }
    }

    private suspend fun insert(game: Game) {
        withContext(Dispatchers.IO){
            database.insert(game)
        }
    }



}