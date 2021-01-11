package com.example.oyunmerkezi3.database

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.oyunmerkezi3.utils.GameFilter

/**
 * This is pretty much boiler plate code for a ViewModel Factory.
 *
 * Provides the SleepDatabaseDao and context to the ViewModel.
 */
class GamesViewModelFactory(
    private val dataSource: GameDatabaseDao,
    private val application: Application,
    private val x:Int,
    private val filterGame:GameFilter?) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GamesViewModel::class.java)) {
            return GamesViewModel(dataSource, application,x,filterGame) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}