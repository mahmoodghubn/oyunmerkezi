package com.example.oyunmerkezi2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.oyunmerkezi2.database.Game

public class DetailViewModel(game: Game):ViewModel(){
    private val _gameName = MutableLiveData<Game>()
    val score: LiveData<Game>
        get() = _gameName

    init {
        _gameName.value = game
    }
}