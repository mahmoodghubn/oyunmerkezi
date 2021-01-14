package com.example.oyunmerkezi3.utils

import android.os.Parcelable
import com.example.oyunmerkezi3.database.Category
import com.example.oyunmerkezi3.database.Language
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameFilter(
    val minPrice: Int?,
    val maxPrice: Int?,
    val minHours: Int?,
    val maxHours: Int?,
    val age: Int?,
    val playersNo: Int?,
    val online: Boolean?,
    val favorite: Boolean?,
    val inStock: Boolean?,
    val gameRate: Float?,
    val category: Category?,
    val language: Language?,
    val publishDate: Int?,
    val orderBy:Int?
) :
    Parcelable {

}