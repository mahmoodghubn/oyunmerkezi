package com.example.oyunmerkezi3.utils

import androidx.room.TypeConverter

object  DataConverter  {
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
            if(index != (strings.size-1)){
                result += ","
            }
        }
        return result
    }
}
