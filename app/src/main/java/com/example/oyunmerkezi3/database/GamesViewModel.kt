package com.example.oyunmerkezi3.database

import android.app.Application
import androidx.lifecycle.*
import com.example.oyunmerkezi3.utils.CalendarUtil
import com.example.oyunmerkezi3.utils.GameFilter
import com.example.oyunmerkezi3.utils.Utils
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.*

class GamesViewModel(
    val database: GameDatabaseDao,
    application: Application,
    platform: String,
    filterGame: GameFilter?
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var plat = platform

    //games variable is observable by games fragment and contain the list of games from the database
    var games: LiveData<List<Game>?> = Transformations.map(database.getPlatform(plat)) {
        it?.sortedBy { it.gameId }
    }
    var games2: MediatorLiveData<List<Game>?> = MediatorLiveData<List<Game>?>()

    //childEvenListener is listening to changes in firebase database
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

    private var mPlaceRef: DatabaseReference =
        Utils.databaseRef?.child("platforms")!!.child(platform)

    init {
        filterGame?.let { filter(it) }
        //TODO fix the bug this block of code get called every time we filter games and that load extra data
        mPlaceRef.addChildEventListener(mChildEventListener)
        mPlaceRef.keepSynced(true)
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

    private fun orderBy(x: Int) {
        games = when (x) {
            0 -> {
                Transformations.map(games) {
                    it?.sortedBy { it.gameName }
                }
            }
            1 -> {
                Transformations.map(games) {
                    it?.sortedByDescending { it.gameName }
                }
            }
            2 -> {
                Transformations.map(games) {
                    it?.sortedBy { it.publishedDate }
                }
            }
            3 -> {
                Transformations.map(games)
                {
                    it?.sortedByDescending { it.publishedDate }
                }
            }
            4 -> {
                Transformations.map(games) {
                    it?.sortedBy { it.sellingPrice }
                }
            }
            5 -> {
                Transformations.map(games) {
                    it?.sortedByDescending { it.sellingPrice }
                }
            }
            6 -> {
                Transformations.map(games) {
                    it?.sortedBy { it.hours }
                }
            }
            7 -> {
                Transformations.map(games) {
                    it?.sortedByDescending { it.hours }
                }
            }
            else -> {
                Transformations.map(games) {
                    it?.sortedBy { it.gameRating }
                }
            }
        }
    }

    fun filter(gameFilter: GameFilter) {
//        games = Transformations.map(games){
//            it?.filter { it.platform.name == plat }
//        }
        gameFilter.minPrice?.let {
            games = Transformations.map(games)
            {
                it?.filter { it.sellingPrice >= gameFilter.minPrice }
            }
        }
        gameFilter.maxPrice?.let {
            games = Transformations.map(games)
            {
                it?.filter { it.sellingPrice <= gameFilter.maxPrice }
            }
        }
        gameFilter.minHours?.let {
            games = Transformations.map(games)
            {
                it?.filter { it.hours >= gameFilter.minHours }
            }
        }
        gameFilter.maxHours?.let {
            games = Transformations.map(games)
            {
                it?.filter { it.hours <= gameFilter.maxHours }
            }
        }
        gameFilter.age?.let {
            games = Transformations.map(games)
            {
                it?.filter { it.age <= gameFilter.age }
            }
        }
        gameFilter.playersNo?.let {
            games = Transformations.map(games)
            {
                it?.filter { gameFilter.playersNo in it.playerNo }
            }
        }
        gameFilter.inStock?.let {
            games = Transformations.map(games)
            {
                it?.filter { it.stock == gameFilter.inStock }
            }
        }
        gameFilter.online?.let {
            games = Transformations.map(games)
            {
                it?.filter {
                    it.online == Online.Offline || it.online == Online.Both
                }
            }
        }
        gameFilter.language?.let {
            games = Transformations.map(games)
            {
                it?.filter { gameFilter.language in it.language }
            }
        }
        gameFilter.category?.let {
            games = Transformations.map(games)
            {
                it?.filter { it.category == gameFilter.category }
            }
        }
        gameFilter.gameRate?.let {
            games = Transformations.map(games)
            {
                it?.filter { it.gameRating >= gameFilter.gameRate }
            }
        }
        gameFilter.publishDate?.let {
            val cal = CalendarUtil(gameFilter.publishDate)
            games = Transformations.map(games)
            {
                it?.filter { it.publishedDate >= cal.dateBefore() }
            }
        }
        gameFilter.orderBy?.let {
            orderBy(it)
        }

    }

    fun onFilterChanged(filter: String) {
//        mPlaceRef = Utils.databaseRef?.child("platforms")!!.child(filter)
//        mPlaceRef?.addChildEventListener(mChildEventListener)
//        mPlaceRef!!.keepSynced(true)
        games = database.getPlatform(filter)
        games2.addSource(games) { exerciseList ->
            games2.removeSource(games)
            games2.setValue(exerciseList)
        }
        //TODO understanding exerciseList variable
        plat = filter
    }
}
