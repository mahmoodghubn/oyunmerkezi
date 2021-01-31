package com.example.oyunmerkezi3.database

import android.app.Application
import android.os.Build
import androidx.lifecycle.*
import com.example.oyunmerkezi3.utils.CalendarUtil
import com.example.oyunmerkezi3.utils.GameFilter
import kotlinx.coroutines.*
import java.time.LocalDate

class GamesViewModel(
    val database: GameDatabaseDao,
    application: Application,
    platform: String
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var selectedPlatform = platform

    //games variable is observable by games fragment and contain the list of games from the database
    private var games: LiveData<List<Game>?> =
        Transformations.map(database.getPlatform(platform)) { list ->
            list?.sortedBy { it.gameId }
        }
    var games2: MediatorLiveData<List<Game>?> = MediatorLiveData<List<Game>?>()
    val sellingCheckBoxArray = arrayListOf<MiniGame>()
    val buyingCheckBoxArray = arrayListOf<MiniGame>()
    var totalPriceLiveData: MutableLiveData<Int> = MutableLiveData(0)

    init {
        games2.addSource(database.getPlatform(platform)) { it ->
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

    private fun orderBy(x: Int) {
        games = when (x) {
            0 -> {
                Transformations.map(games) { list ->
                    list?.sortedBy { it.gameName }
                }
            }
            1 -> {
                Transformations.map(games) { list ->
                    list?.sortedByDescending { it.gameName }
                }
            }
            2 -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Transformations.map(games) { list ->
                        list?.sortedBy {
                            LocalDate.of(
                                it.publishedDate.year,
                                it.publishedDate.month,
                                it.publishedDate.day
                            )
                        }
                    }
                } else {
                    Transformations.map(games) { list ->
                        list?.sortedBy {
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
                    Transformations.map(games) { list ->
                        list?.sortedByDescending {
                            LocalDate.of(
                                it.publishedDate.year,
                                it.publishedDate.month,
                                it.publishedDate.day
                            )
                        }
                    }
                } else {
                    Transformations.map(games) { list ->
                        list?.sortedByDescending {
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
                Transformations.map(games) { list ->
                    list?.sortedBy { it.sellingPrice }
                }
            }
            5 -> {
                Transformations.map(games) { list ->
                    list?.sortedByDescending { it.sellingPrice }
                }
            }
            6 -> {
                Transformations.map(games) { list ->
                    list?.sortedBy { it.hours }
                }
            }
            7 -> {
                Transformations.map(games) { list ->
                    list?.sortedByDescending { it.hours }
                }
            }
            else -> {
                Transformations.map(games) { list ->
                    list?.sortedBy { it.gameRating }
                }
            }
        }
    }

    fun filter(gameFilter: GameFilter) {
        games = Transformations.map(database.getPlatform(selectedPlatform)) { list ->
            list?.sortedBy { it.gameId }
        }
        gameFilter.minPrice?.let {
            games = Transformations.map(games)
            { list ->
                list?.filter { it.sellingPrice >= gameFilter.minPrice }
            }
        }
        gameFilter.maxPrice?.let {
            games = Transformations.map(games)
            { list ->
                list?.filter { it.sellingPrice <= gameFilter.maxPrice }
            }
        }
        gameFilter.minHours?.let {
            games = Transformations.map(games)
            { list ->
                list?.filter { it.hours >= gameFilter.minHours }
            }
        }
        gameFilter.maxHours?.let {
            games = Transformations.map(games)
            { list ->
                list?.filter { it.hours <= gameFilter.maxHours }
            }
        }
        gameFilter.age?.let {
            games = Transformations.map(games)
            { list ->
                list?.filter { it.age <= gameFilter.age }
            }
        }
        gameFilter.playersNo?.let {
            games = Transformations.map(games)
            { list ->
                list?.filter { gameFilter.playersNo in it.playerNo }
            }
        }
        gameFilter.inStock?.let {
            games = Transformations.map(games)
            { list ->
                list?.filter { it.stock == gameFilter.inStock }
            }
        }
        gameFilter.online?.let {
            games = Transformations.map(games)
            { list ->
                list?.filter {
                    it.online == Online.Offline || it.online == Online.Both
                }
            }
        }
        gameFilter.language?.let {
            games = Transformations.map(games)
            { list ->
                list?.filter { gameFilter.language in it.language }
            }
        }
        gameFilter.category?.let {
            games = Transformations.map(games)
            { list ->
                list?.filter { it.category == gameFilter.category }
            }
        }
        gameFilter.gameRate?.let {
            games = Transformations.map(games)
            { list ->
                list?.filter { it.gameRating >= gameFilter.gameRate }
            }
        }
        gameFilter.publishDate?.let {
            val cal = CalendarUtil(gameFilter.publishDate)
            games = Transformations.map(games)
            { list ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    list?.filter {
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
                    list?.filter {
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
        games2.addSource(games) { List ->
            games2.setValue(List)
        }
    }

    fun onSelectedPlatformChange(filter: String) {
        selectedPlatform = filter
        games2.addSource(database.getPlatform(filter)) { it ->
            games2.setValue(it)
        }
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

    fun setFavorite(gameId: Long) {
        uiScope.launch {
            val game = getGame(gameId)!!
            game.showNotification = !game.showNotification
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
