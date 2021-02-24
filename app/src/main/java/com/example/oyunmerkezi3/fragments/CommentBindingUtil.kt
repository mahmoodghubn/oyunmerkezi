package com.example.oyunmerkezi3.fragments

import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.oyunmerkezi3.R
import com.example.oyunmerkezi3.database.Game
import com.example.oyunmerkezi3.model.Comment
import com.example.oyunmerkezi3.utils.toText

@BindingAdapter("message")
fun TextView.setMessage(item: Comment?) {
    item?.let {
        text = item.message
    }
}

@BindingAdapter("userName")
fun TextView.setUserName(item: Comment?) {
    item?.let {
        text = item.userName
    }
}

@BindingAdapter("date")
fun TextView.setDate(item: Comment?) {
    item?.let {
        text = item.date.toText()
    }
}


@BindingAdapter("photo")
fun ImageView.setPhoto(item: Comment?) {

    item?.photoUri?.let {
        Glide.with(this.context).load(item.photoUri).error(R.drawable.ic_baseline_face_24).apply(RequestOptions.circleCropTransform()).into(this)

    }
}

@BindingAdapter("rate")
fun RatingBar.setRate(item: Comment?) {
    item?.let {
        rating = item.gameRate.toFloat()
    }
}