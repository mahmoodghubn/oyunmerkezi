package com.example.oyunmerkezi3.recycling

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("videoUrl")
fun ImageView.setVideoURL(item: String?) {

    val url = "https://i4.ytimg.com/vi/${item!!}/mqdefault.jpg"
    item.let {
        Glide.with(this.context).load(url).into(this)

    }
}