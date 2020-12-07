package com.example.oyunmerkezi2.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.net.URL
@Parcelize
@Entity(tableName = "game_table")
data class Game(
    @PrimaryKey(autoGenerate = true)
    var gameId: Long = 0L,

    @ColumnInfo
    var gameName:String = "",

    @ColumnInfo
    var sellingPrice:Int = 0,

    @ColumnInfo
    var buyingPrice:Int = 0

//    @ColumnInfo
//    var favorite:Boolean = false,

//    @ColumnInfo
//    var about:String = "",

//    @ColumnInfo
//    var age:Int = 3,

//    @ColumnInfo
//    var playNo:Int = 1

//    @ColumnInfo(name = "rating")
//    var gameRating: Int = 0,

//    @ColumnInfo
//    var inStock:Boolean = true

//    @ColumnInfo
//    var hours:Int = 1

//    @ColumnInfo
//    var date:Int = 1

//    @ColumnInfo
//    var language:String = "turkish",

//    @ColumnInfo
//    var comments:String = ""

//    @ColumnInfo
//    var category: String,

//    @ColumnInfo
//    var videoUrl:String = ""

//    @ColumnInfo
//    var region:String
):Parcelable