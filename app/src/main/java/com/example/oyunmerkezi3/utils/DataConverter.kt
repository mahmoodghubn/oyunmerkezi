package com.example.oyunmerkezi3.utils

import androidx.room.TypeConverter
import com.example.oyunmerkezi3.database.Category
import com.example.oyunmerkezi3.database.Language
import com.example.oyunmerkezi3.database.Online
import java.util.*

object DataConverter {
    @TypeConverter
    @JvmStatic
    fun toList(strings: String): List<String> {
        val list = mutableListOf<String>()
        val array = strings.split(",")
        for (s in array) {
            list.add(s)
        }
        return list
    }

    @TypeConverter
    @JvmStatic
    fun toString(strings: List<String>): String {
        var result = ""
        strings.forEachIndexed { index, element ->
            result += element
            if (index != (strings.size - 1)) {
                result += ","
            }
        }
        return result
    }

    @TypeConverter
    @JvmStatic
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    @JvmStatic
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

    @TypeConverter
    @JvmStatic
    fun onlineToString(date: Online?): String {
        return date!!.name
    }

    @TypeConverter
    @JvmStatic
    fun fromStringToOnline(value: String): Online? {
        return Online.values().find { it.name == value }
    }

    @TypeConverter
    @JvmStatic
    fun categoryToString(date: Category?): String {
        return date!!.name
    }

    @TypeConverter
    @JvmStatic
    fun fromStringToCategory(value: String): Category? {
        return Category.values().find { it.name == value }
    }

    @TypeConverter
    @JvmStatic
    fun languageToString(languages: List<Language>?): String {
        var result = ""
        languages!!.forEachIndexed { index, element ->
            result += element.name
            if (index != (languages.size - 1)) {
                result += ","
            }
        }
        return result
    }

    @TypeConverter
    @JvmStatic
    fun fromStringToLanguage(strings: String): List<Language>? {
        val list = mutableListOf<Language>()
        val array = strings.split(",")
        for (s in array) {
            list.add(Language.values().find { it.name == s }!!)
        }
        return list
    }

    @TypeConverter
    @JvmStatic
    fun toIntList(strings: String): List<Int> {
        val list = mutableListOf<Int>()
        val array = strings.split(",")
        for (item in array) {
            list.add(item.toInt())
        }
        return list
    }

    @TypeConverter
    @JvmStatic
    fun fromIntListToString(strings: List<Int>): String {
        var result = ""
        strings.forEachIndexed { index, element ->
            result += element
            if (index != (strings.size - 1)) {
                result += ","
            }
        }
        return result
    }
}
