package com.example.oyunmerkezi2.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface GameDatabaseDao {

    @Insert
    fun insert(game:Game)

    @Update
    fun update(game:Game)

    @Query("SELECT * from game_table WHERE gameId = :key")
    fun get(key: Long): Game?

    @Query("SELECT * FROM game_table ORDER BY gameId DESC")
    fun getAllGames(): LiveData<List<Game>>

//    @Query("SELECT * FROM game_table ORDER BY gameId DESC LIMIT 1")
//    fun getNewest(): Game?
}