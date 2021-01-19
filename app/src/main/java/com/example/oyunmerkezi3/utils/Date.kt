package com.example.oyunmerkezi3.utils

import android.os.Build
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.util.Date

@Parcelize//this class need to have restrictions to avoid wrong data
data class Date(val year: Int, val month: Int, val day: Int) : Parcelable {
    constructor() : this(1990, 1, 1)
}
