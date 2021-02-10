package com.example.oyunmerkezi3.bottomSheet

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.oyunmerkezi3.database.MiniGame


@BindingAdapter("price")
fun TextView.setPrice(item: MiniGame?) {
    item?.let {
        text = item.price.toString()
    }

}


@BindingAdapter("gameName")
fun TextView.setGameName(item: MiniGame?) {
    item?.let {
        text = item.gameName
    }
}

@BindingAdapter("platform")
fun TextView.setPlatform(item: MiniGame?) {
    item?.let {
        text = item.platform.toString()
    }
}
@BindingAdapter("count")
fun TextView.setCount(item: MiniGame?) {
    item?.let {
        text = item.count.toString()
    }
}


@BindingAdapter("total")
fun TextView.setTotal(item: MiniGame?) {
    item?.let {
        text = item.total.toString()
    }
}


