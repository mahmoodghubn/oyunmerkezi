package com.example.oyunmerkezi3

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.oyunmerkezi3.database.Game

public class DetailViewModel(game: Game):ViewModel(){
    private val _game = MutableLiveData<Game>()
    val game: LiveData<Game>
        get() = _game

    init {
        _game.value = game
    }
}