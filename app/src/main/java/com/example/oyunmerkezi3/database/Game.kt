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
    var favorite: Boolean = false,

    @ColumnInfo
    var about: String = "",

    @ColumnInfo
    var age: Int = 3,

    @ColumnInfo
    var gameRating: Float = 0F,
//
    @ColumnInfo
    var stock: Boolean = false,
//
    @ColumnInfo
    var hours: Int = 1,

    @ColumnInfo
    var publishedDate: Date = Date(1990, 1, 1),

    @ColumnInfo
    var online: Online,

    @ColumnInfo
    var category: Category,

//    @ColumnInfo
//    var region: Region,

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
        false,
        "",
        3,
        0F,
        false,
        1,
        Date(1990, 1, 1),
        Online.Online,
        Category.Race,
//        Region.R0
        emptyList(),
        listOf(Language.Turkish,Language.English),
        listOf(Language.Turkish,Language.English)
    )
}

//enum class Region {
//    R0,R1,R2,R3,R4,R5,R6
//}
//
enum class Online {
    Online, Offline, Both
}

enum class Language {
    Arabic, Dutch, English, French, German, Greek, Hebrew, Hindi, Irish, Italian,
    Norwegian, Persian, Polish, Portuguese, Romanian, Russian,
    Serbian, Spanish, Swedish, Turkish
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