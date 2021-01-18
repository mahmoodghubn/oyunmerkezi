package com.example.oyunmerkezi3.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "game_table")
data class Game(
    @PrimaryKey(autoGenerate = true)
    var gameId: Long = 0L,

    @ColumnInfo
    var gameName: String = "",

    @ColumnInfo
    var sellingPrice: Int = 0,

    @ColumnInfo
    var buyingPrice: Int = 0,

    @ColumnInfo
    var URL: List<String>,

    @ColumnInfo
    var about: String = "",

    @ColumnInfo
    var age: Int = 3,

    @ColumnInfo
    var gameRating: Float = 0F,

    @ColumnInfo
    var stock: Boolean = false,

    @ColumnInfo
    var hours: Int = 1,

    @ColumnInfo
    var publishedDate: Date = Date(1990, 1, 1),

    @ColumnInfo
    var online: Online,

    @ColumnInfo
    var category: Category,

    @ColumnInfo
    var platform: Platform,

    @ColumnInfo
    var playerNo: List<Int>,

    @ColumnInfo
    var language: List<Language>,

    @ColumnInfo
    var caption: List<Language>
) : Parcelable {
    constructor() : this(
        0,
        "",
        0,
        0,
        emptyList(),
        "",
        3,
        0F,
        false,
        1,
        Date(1990, 1, 1),
        Online.Online,
        Category.Race,
        Platform.PS4,
        emptyList(),
        listOf(Language.Turkish, Language.English),
        listOf(Language.Turkish, Language.English)
    )
}

enum class Online {
    Online, Offline, Both
}

enum class Language {
    Turkish, Arabic, English, French, German, Italian, Russian, Spanish, Japanese
}

enum class Category {
    ActionWar,
    Sport,
    Race,
    FightingBoxing,
    Platform,
    Strategy,
    FearThriller,
    RPG,
    VR
}

enum class Platform {
    PS3, PS4, PS5, XBoxOne, XBoxX
}

object SingletonPlatform {
    private var platformBool = arrayListOf(
        Platform.PS3,
        Platform.PS4,
        Platform.PS5,
        Platform.XBoxOne,
        Platform.XBoxX
    )
//    init {
//        currentPlatform(0)
//    }
//    fun currentPlatform(index:Int):Pair<Platform,Boolean> {
//        return platformBool[index]
//    }
//    fun setCurrentPlat(boolArrayList :ArrayList<Pair<Platform,Boolean>>){
//        for ((index, item) in boolArrayList.withIndex()){
//            platformBool[index] = item
//        }
//    }
}


