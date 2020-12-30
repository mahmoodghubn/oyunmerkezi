package com.example.oyunmerkezi3

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.oyunmerkezi3.database.Game

class DetailViewModelFactory(private val game: Game) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(this.game) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}