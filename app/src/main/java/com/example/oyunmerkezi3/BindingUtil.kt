package com.example.oyunmerkezi3

import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.oyunmerkezi3.database.Game
import java.net.MalformedURLException

@BindingAdapter("sellingPrice")
fun TextView.setGamePrice(item: Game?) {
    item?.let {
        text = item.sellingPrice.toString()
    }
}

@BindingAdapter("gameName")
fun TextView.setGameName(item: Game?) {
    item?.let {
        text = item.gameName
    }
}

@BindingAdapter("imgThumbnail")
fun ImageView.setThumbnail(item: Game?) {
    try {
        val imgUrl =
            "http://img.youtube.com/vi/" + item?.URL + "/0.jpg";

        imgUrl.let {
            val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
            Glide.with(this.context)
                .load(imgUri)
                .into(this)
        }

    } catch (e: MalformedURLException) {
        e.printStackTrace();
    }
}
