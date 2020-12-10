package com.example.oyunmerkezi2

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.oyunmerkezi2.database.Game

@BindingAdapter("sellingPrice")
fun TextView.setSleepDurationFormatted(item: Game?) {
    item?.let {
        text = item.sellingPrice.toString()
    }
}

@BindingAdapter("gameName")
fun TextView.setSleepQualityString(item: Game?) {
    item?.let {
        text = item.gameName
    }
}