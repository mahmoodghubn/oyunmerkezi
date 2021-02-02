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
    val sellingCheckBoxArray = arrayListOf<MiniGame>()
    val buyingCheckBoxArray = arrayListOf<MiniGame>()
    var totalPriceLiveData: MutableLiveData<Int> = MutableLiveData(0)

    init {
        val platformSharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(application)
        //observing the changes in shared preferences and delete data accordingly
        val sharedPreferenceStringLiveData: SharedPreferenceStringLiveData
        sharedPreferenceStringLiveData =
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

        gameFilter?.minPrice?.let {
            localListGame = games.filter { it.sellingPrice >= gameFilter.minPrice }
        }

        gameFilter?.maxPrice?.let {
            localListGame = localListGame?.filter { it.sellingPrice <= gameFilter.maxPrice }
        }

        gameFilter?.minHours?.let {
            localListGame = localListGame?.filter { it.hours >= gameFilter.minHours }
        }

        gameFilter?.maxHours?.let {
            localListGame = localListGame?.filter { it.hours <= gameFilter.maxHours }
        }

        gameFilter?.age?.let {
            localListGame = localListGame?.filter { it.age <= gameFilter.age }
        }

        gameFilter?.playersNo?.let {
            localListGame = localListGame?.filter { gameFilter.playersNo in it.playerNo }
        }

        gameFilter?.inStock?.let {
            localListGame = localListGame?.filter { it.stock == gameFilter.inStock }
        }

        gameFilter?.online?.let {
            localListGame = localListGame?.filter {
                it.online == Online.Offline || it.online == Online.Both
            }
        }

        gameFilter?.language?.let {
            localListGame = localListGame?.filter { gameFilter.language in it.language }
        }

        gameFilter?.category?.let {
            localListGame = localListGame?.filter { it.category == gameFilter.category }
        }

        gameFilter?.gameRate?.let {
            localListGame = localListGame?.filter { it.gameRating >= gameFilter.gameRate }
        }

        gameFilter?.publishDate?.let {
            val cal = CalendarUtil(gameFilter.publishDate)
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

        gameFilter?.orderBy?.let {
            localListGame = orderBy(it, localListGame)
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

    fun addSoledGame(game: MiniGame) {
        if (sellingCheckBoxArray.filter { it.gameId == game.gameId }.size == 1) {
            sellingCheckBoxArray.remove(sellingCheckBoxArray.first { it.gameId == game.gameId })
            totalPriceLiveData.value = totalPriceLiveData.value!!.minus(game.total)

        } else {
            sellingCheckBoxArray.add(game)
            totalPriceLiveData.value = totalPriceLiveData.value!!.plus(game.price)
        }
    }

    fun addBoughtGame(game: MiniGame) {
        if (buyingCheckBoxArray.filter { it.gameId == game.gameId }.size == 1) {
            buyingCheckBoxArray.remove(buyingCheckBoxArray.first { it.gameId == game.gameId })
            totalPriceLiveData.value = totalPriceLiveData.value!!.plus(game.total)

        } else {
            buyingCheckBoxArray.add(game)
            totalPriceLiveData.value = totalPriceLiveData.value!!.minus(game.price)
        }
    }

    fun increaseCount(game: MiniGame, isSelling: Boolean) {
        if (isSelling) {
            totalPriceLiveData.value = totalPriceLiveData.value!!.plus(game.price)
        } else {
            totalPriceLiveData.value = totalPriceLiveData.value!!.minus(game.price)
        }
    }

    fun decreasingCount(game: MiniGame, isSelling: Boolean) {
        if (isSelling) {
            totalPriceLiveData.value = totalPriceLiveData.value!!.minus(game.price)
        } else {
            totalPriceLiveData.value = totalPriceLiveData.value!!.plus(game.price)
        }
    }

    fun clearTheListOfSelectedGame() {
        sellingCheckBoxArray.clear()
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
