package com.example.oyunmerkezi3.fragments

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.oyunmerkezi3.model.Comment

@BindingAdapter("message")
fun TextView.setMessage(item: Comment?) {
    item?.let {
        text = item.message
    }
}