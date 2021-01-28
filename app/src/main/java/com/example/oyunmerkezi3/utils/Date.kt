package com.example.oyunmerkezi3.utils

import android.os.Parcelable
import android.util.Log
import kotlinx.parcelize.Parcelize

@Parcelize//this class need to have restrictions to avoid wrong data
data class Date(val year: Int, val month: Int, val day: Int) : Parcelable {
    constructor() : this(1990, 1, 1)


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
