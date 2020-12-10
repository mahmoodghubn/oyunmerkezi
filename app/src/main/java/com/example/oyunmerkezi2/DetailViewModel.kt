package com.example.oyunmerkezi2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

public class DetailViewModel(gameId: Long):ViewModel(){
    private val _gameId = MutableLiveData<Long>()
    val gameId: LiveData<Long>
        get() = _gameId

    init {
        _gameId.value = gameId
    }
}