package com.example.oyunmerkezi3.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.oyunmerkezi3.utils.Date
import kotlinx.parcelize.Parcelize

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
    var gameRating: Float = 0f,

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
    var caption: List<Language>,

    @ColumnInfo
    var favorite: Boolean,

    @ColumnInfo
    var showNotification: Boolean
) : Parcelable {
    constructor() : this(
        0,
        "",
        0,
        0,
        emptyList(),
        "",
        3,
        0f,
        false,
        1,
        Date(1990, 1, 1),
        Online.Online,
        Category.Race,
        Platform.PS4,
        emptyList(),
        listOf(Language.Turkish, Language.English),
        listOf(Language.Turkish, Language.English),
        false,
        false
    )
    constructor(game:DownloadedGame) : this(
        game.gameId,
        game.gameName,
        game.sellingPrice,
        game.buyingPrice,
        game.URL,
        game.about,
        game.age,
        getRating(game.gameRating),
        game.stock,
        game.hours,
        game.publishedDate,
        game.online,
        game.category,
        game.platform,
        game.playerNo,
        game.language,
        game.caption,
        false,
        false
    )
    constructor(game:DownloadedGame,showNotification: Boolean,favorite: Boolean) : this(
        game.gameId,
        game.gameName,
        game.sellingPrice,
        game.buyingPrice,
        game.URL,
        game.about,
        game.age,
        getRating(game.gameRating),
        game.stock,
        game.hours,
        game.publishedDate,
        game.online,
        game.category,
        game.platform,
        game.playerNo,
        game.language,
        game.caption,
        favorite,
        showNotification
    )
}

enum class Online {
    Online, Offline, Both
}

fun Online.toText():String{
    return when(this){
        Online.Online -> "Only Online"
        Online.Offline -> "Offline Mode"
        Online.Both ->"Online and Offline"
    }
}

fun List<Int>.toText():String{
    var string = ""
    for (item in this){
        if ( item != this.last())
            string = "$string$item-"
    }
    string = "$string${this.last()}"
    return string
}
@JvmName("toTextString")
fun List<Language>.toText():String{
    var string = ""
    for (item in this){
        if ( item != this.last())
            string = "$string${item.name}-"
    }
    string = "$string${this.last().name}"
    return string
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

data class MiniGame(var gameId: Long,var gameName: String , var price: Int,var platform: Platform,var count:Int,var total:Int){
    constructor() : this(
        0,
        "",
        0,
        Platform.PS4,
        0,
        0
    )
}

private fun getRating(rateList: List<Int>): Float {
    val numberOfRater: Int = rateList.sum()
    var totalRating: Long = 0
    for ((index, item) in rateList.withIndex())
        totalRating += item * (index + 1)
    return if (numberOfRater != 0)
        totalRating.toFloat().div(numberOfRater)
    else
        0f

}