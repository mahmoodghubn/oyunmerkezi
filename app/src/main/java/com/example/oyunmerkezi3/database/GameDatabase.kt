package com.example.oyunmerkezi3.database

import androidx.room.Database
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.Room
import androidx.room.TypeConverters
import com.example.oyunmerkezi3.utils.DataConverter

@Database(entities = [Game::class], version = 1, exportSchema = false)
@TypeConverters(DataConverter::class)
abstract class GameDatabase : RoomDatabase() {

    abstract val gameDatabaseDao: GameDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE: GameDatabase? = null

        fun getInstance(context: Context): GameDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        GameDatabase::class.java,
                        "sleep_history_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}