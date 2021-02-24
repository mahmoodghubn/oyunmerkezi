package com.example.oyunmerkezi3.utils

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize//this class need to have restrictions to avoid wrong data
data class Date(val year: Int, val month: Int, val day: Int) : Parcelable,Comparable<Date> {
    constructor() : this(1990, 1, 1)
    override operator fun compareTo(other: Date): Int {
        if (this.year > other.year) return 1
        if (this.year < other.year) return -1
        if (this.month > other.month) return 1
        if (this.month < other.month) return -1
        if (this.day > other.day) return 1
        if (this.day < other.day) return -1
        return 0
    }

}
fun Date.isBigger(date: Date):Boolean{

    if (this.year> date.year){
        return true
    }
    else if(this.year == date.year) {
        if (this.month > date.month) {
            return true
        } else if (this.day > date.day && this.month == date.month) {
            return true
        }
    }
    return false
}
fun Date.toText():String{
    return "${this.day}-${this.month}-${this.year}"
}