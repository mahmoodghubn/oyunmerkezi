package com.example.oyunmerkezi3.database

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.lifecycle.*
import com.example.oyunmerkezi3.utils.CalendarUtil
import com.example.oyunmerkezi3.utils.GameFilter
import com.example.oyunmerkezi3.utils.Utils
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.*
import java.time.LocalDate

class GamesViewModel(
    val database: GameDatabaseDao,
    application: Application,
    platform: String,
    downloaded: Boolean
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
    val sellingCheckBox = arrayListOf<MiniGame>()
    val buyingCheckBox = arrayListOf<MiniGame>()
    var total: MutableLiveData<Int> = MutableLiveData(0)

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
        Utils.databaseRef?.child("platforms")!!.child("PS3")

    init {
        if (!downloaded) {
            mPlaceRef.addChildEventListener(mChildEventListener)
            mPlaceRef.keepSynced(true)
        }
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Transformations.map(games) {
                        it?.sortedBy {
                            LocalDate.of(
                                it.publishedDate.year,
                                it.publishedDate.month,
                                it.publishedDate.day
                            )
                        }
                    }
                } else {
                    Transformations.map(games) {
                        it?.sortedBy {
                            java.util.Date(
                                it.publishedDate.year,
                                it.publishedDate.month,
                                it.publishedDate.day
                            )
                        }
                    }
                }
            }
            3 -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Transformations.map(games) {
                        it?.sortedByDescending {
                            LocalDate.of(
                                it.publishedDate.year,
                                it.publishedDate.month,
                                it.publishedDate.day
                            )
                        }
                    }
                } else {
                    Transformations.map(games) {
                        it?.sortedByDescending {
                            java.util.Date(
                                it.publishedDate.year,
                                it.publishedDate.month,
                                it.publishedDate.day
                            )
                        }
                    }
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
            { it1 ->
                it1?.filter { it.gameRating >= gameFilter.gameRate }
            }
        }
        gameFilter.publishDate?.let {
            val cal = CalendarUtil(gameFilter.publishDate)
            games = Transformations.map(games)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    it?.filter {
                        LocalDate.of(
                            it.publishedDate.year,
                            it.publishedDate.month,
                            it.publishedDate.day
                        ) >= LocalDate.of(
                            cal.dateBefore().year,
                            cal.dateBefore().month,
                            cal.dateBefore().day
                        )
                    }
                else
                    it?.filter {
                        java.util.Date(
                            it.publishedDate.year,
                            it.publishedDate.month,
                            it.publishedDate.day
                        ) >= java.util.Date(
                            cal.dateBefore().year,
                            cal.dateBefore().month,
                            cal.dateBefore().day
                        )
                    }
            }
        }
        gameFilter.orderBy?.let {
            orderBy(it)
        }

    }

    fun onFilterChanged(filter: String) {
        games = database.getPlatform(filter)
        games2.addSource(games) { exerciseList ->
            games2.removeSource(games)
            games2.setValue(exerciseList)
        }
        //TODO understanding exerciseList variable
        plat = filter
    }

    fun downloadDataFromFireBaseWhenSharedPreferenceChange(platform: String) {
        mPlaceRef = Utils.databaseRef?.child("platforms")!!.child(platform)
        mPlaceRef.addChildEventListener(mChildEventListener)
        mPlaceRef.keepSynced(true)
    }

    fun deletePlatformFromDataBaseWhenSharedPreferenceChanges(platform: String) {
        uiScope.launch {
            delete(platform)
        }
    }

    private suspend fun delete(platform: String) {
        withContext(Dispatchers.IO) {
            database.deletePlatform(platform)
        }
    }

    fun addSoledGame(game: MiniGame) {
        if (sellingCheckBox.filter { it.gameId == game.gameId }.size == 1) {
            sellingCheckBox.remove(sellingCheckBox.first { it.gameId == game.gameId })
            total.value = total.value!!.minus(game.price)
        } else {
            sellingCheckBox.add(game)
            total.value = total.value!!.plus(game.price)
        }
    }

    fun addBoughtGame(game: MiniGame) {
        if (buyingCheckBox.filter { it.gameId == game.gameId }.size == 1) {
            buyingCheckBox.remove(buyingCheckBox.first { it.gameId == game.gameId })
            total.value = total.value!!.plus(game.price)
        } else {
            buyingCheckBox.add(game)
            total.value = total.value!!.minus(game.price)
        }
    }
    fun clear(){
        sellingCheckBox.clear()
        buyingCheckBox.clear()
        total.value = 0
    }
}
