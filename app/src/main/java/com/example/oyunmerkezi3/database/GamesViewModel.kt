package com.example.oyunmerkezi3.database

import android.app.Application
import android.os.Build
import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import com.example.oyunmerkezi3.shared_preferences.SharedPreferenceStringLiveData
import com.example.oyunmerkezi3.utils.CalendarUtil
import com.example.oyunmerkezi3.utils.GameFilter
import kotlinx.coroutines.*
import java.time.LocalDate

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

    var games: LiveData<List<Game>?> = database.getAll()

    var favoriteGames: LiveData<List<Game>?> = database.getAllFavorite(true)

    var games2: MediatorLiveData<List<Game>?> = MediatorLiveData<List<Game>?>()
    val buyingCheckBoxArray = arrayListOf<MiniGame>()
    var totalPriceLiveData: MutableLiveData<Int> = MutableLiveData(0)

    init {
        val platformSharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(application)
        //observing the changes in shared preferences and delete data accordingly
        val sharedPreferenceStringLiveData: SharedPreferenceStringLiveData =
            SharedPreferenceStringLiveData(platformSharedPreferences, "current", "PS4")
        val currentSharedPreferences = sharedPreferenceStringLiveData.getStringLiveData(
            "current",
            "PS4"
        )

        games = Transformations.switchMap(currentSharedPreferences) { it ->
            database.getPlatform(it!!)
        }

        games2.addSource(games) { it ->
            games2.setValue(it)
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

    private fun orderBy(x: Int, games: List<Game>?): List<Game>? {
        return when (x) {
            0 -> {
                games?.sortedBy { it.gameName }
            }
            1 -> {
                games?.sortedByDescending { it.gameName }
            }
            2 -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    games?.sortedBy {
                        LocalDate.of(
                            it.publishedDate.year,
                            it.publishedDate.month,
                            it.publishedDate.day
                        )
                    }
                } else {
                    games?.sortedBy {
                        java.util.Date(
                            it.publishedDate.year,
                            it.publishedDate.month,
                            it.publishedDate.day
                        )
                    }
                }
            }
            3 -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    games?.sortedByDescending {
                        LocalDate.of(
                            it.publishedDate.year,
                            it.publishedDate.month,
                            it.publishedDate.day
                        )
                    }
                } else {
                    games?.sortedByDescending {
                        java.util.Date(
                            it.publishedDate.year,
                            it.publishedDate.month,
                            it.publishedDate.day
                        )
                    }
                }
            }
            4 -> {
                games?.sortedBy { it.sellingPrice }
            }
            5 -> {
                games?.sortedByDescending { it.sellingPrice }
            }
            6 -> {
                games?.sortedBy { it.hours }
            }
            7 -> {
                games?.sortedByDescending { it.hours }
            }
            else -> {
                games?.sortedByDescending { it.gameRating }
            }
        }
    }

    fun filter(gameFilter: GameFilter?) {
        games2.removeSource(games)
        games2.addSource(games) { it ->
            games2.setValue(filter2(gameFilter, it!!))
        }
    }

    private fun filter2(gameFilter: GameFilter?, games: List<Game>): List<Game>? {

        var localListGame: List<Game>? = games
        gameFilter?.let { item ->
            item.minPrice?.let { pr ->
                localListGame = games.filter { it.sellingPrice >= pr }
            }

            item.maxPrice?.let { pr ->
                localListGame = localListGame?.filter { it.sellingPrice <= pr }
            }

            item.minHours?.let { pr ->
                localListGame = localListGame?.filter { it.hours >= pr }
            }

            item.maxHours?.let { pr ->
                localListGame = localListGame?.filter { it.hours <= pr }
            }

            item.age?.let { pr ->
                localListGame = localListGame?.filter { it.age <= pr }
            }

            item.playersNo?.let { pr ->
                localListGame = localListGame?.filter { pr in it.playerNo }
            }

            item.inStock?.let { pr ->
                localListGame = localListGame?.filter { it.stock == pr }
            }

            item.online?.let {
                localListGame = localListGame?.filter {
                    it.online == Online.Offline || it.online == Online.Both
                }
            }

            item.language?.let { pr ->
                localListGame = localListGame?.filter { pr in it.language }
            }

            item.category?.let { pr ->
                localListGame = localListGame?.filter { it.category == pr }
            }

            item.gameRate?.let { pr ->
                localListGame = localListGame?.filter { it.gameRating >= pr }
            }

            item.publishDate?.let { pr ->
                val cal = CalendarUtil(pr)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    localListGame = localListGame?.filter {
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
                    localListGame = localListGame?.filter {
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

            item.orderBy?.let {
                localListGame = orderBy(it, localListGame)
            }

        }



        return localListGame
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

    fun addMiniGame(game: MiniGame) {
        if (buyingCheckBoxArray.any { it.gameId == game.gameId }) {
            buyingCheckBoxArray.remove(buyingCheckBoxArray.first { it.gameId == game.gameId })
            totalPriceLiveData.value = totalPriceLiveData.value!!.minus(game.total)

        } else {
            buyingCheckBoxArray.add(game)
            totalPriceLiveData.value = totalPriceLiveData.value!!.plus(game.price)
        }
    }

    fun increaseCount(game: MiniGame) {
        totalPriceLiveData.value = totalPriceLiveData.value!!.plus(game.price)
    }

    fun decreasingCount(game: MiniGame) {
        totalPriceLiveData.value = totalPriceLiveData.value!!.minus(game.price)
    }

    fun clearTheListOfSelectedGame() {
        buyingCheckBoxArray.clear()
        totalPriceLiveData.value = 0
    }

    fun setShowNotification(gameId: Long) {
        uiScope.launch {
            val game = getGame(gameId)!!
            game.showNotification = !game.showNotification
            updateGame(game)
        }
    }

    fun setFavorite(gameId: Long) {
        uiScope.launch {
            val game = getGame(gameId)!!
            game.favorite = !game.favorite
            updateGame(game)
        }
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
}
