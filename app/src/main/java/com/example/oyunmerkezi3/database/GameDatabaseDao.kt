package com.example.oyunmerkezi3.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface GameDatabaseDao {

    @Insert
    fun insert(game:Game)

    @Update
    fun update(game:Game)

    @Delete
    fun delete(game: Game)

    @Query("SELECT * from game_table WHERE gameId = :key")
    suspend fun get(key: Long):Game?

    @Query("SELECT * FROM game_table ORDER BY gameId DESC")
    fun getAllGames(): LiveData<List<Game>?>

    @Query("SELECT * from game_table WHERE platform = :platform")
    fun getPlatform(platform: String):LiveData<List<Game>?>

}