package com.example.oyunmerkezi3

import android.content.ContentValues.TAG
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.oyunmerkezi3.database.Game
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubeThumbnailLoader
import com.google.android.youtube.player.YouTubeThumbnailLoader.OnThumbnailLoadedListener
import com.google.android.youtube.player.YouTubeThumbnailView


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

@BindingAdapter("videoThumbnailImageView")
fun ImageView.setThumbnail(item: Game?) {

    val URL = "https://i4.ytimg.com/vi/${item!!.URL[0]}/mqdefault.jpg"
    item?.let {
        Glide.with(this.context).load(URL).into(this)

//        this.initialize(
//            "AIzaSyCbfkNTBYJp0JEp8hM4J0TCEm_EcnIvwig",
//            object : YouTubeThumbnailView.OnInitializedListener {
//                override fun onInitializationSuccess(
//                    youTubeThumbnailView: YouTubeThumbnailView,
//                    youTubeThumbnailLoader: YouTubeThumbnailLoader
//                ) {
//                    //when initialization is sucess, set the video id to thumbnail to load
//                    youTubeThumbnailLoader.setVideo(item.URL[0])
//                    youTubeThumbnailLoader.setOnThumbnailLoadedListener(object :
//                        OnThumbnailLoadedListener {
//                        override fun onThumbnailLoaded(
//                            youTubeThumbnailView: YouTubeThumbnailView,
//                            s: String
//                        ) {
//                            //when thumbnail loaded successfully release the thumbnail loader as we are showing thumbnail in adapter
//                            youTubeThumbnailLoader.release()
//                        }
//
//                        override fun onThumbnailError(
//                            youTubeThumbnailView: YouTubeThumbnailView,
//                            errorReason: YouTubeThumbnailLoader.ErrorReason
//                        ) {
//                            //print or show error when thumbnail load failed
//                            Log.e(TAG, "Youtube Thumbnail Error")
//                        }
//                    })
//                }
//
//                override fun onInitializationFailure(
//                    youTubeThumbnailView: YouTubeThumbnailView,
//                    youTubeInitializationResult: YouTubeInitializationResult
//                ) {
//                    //print or show error when initialization failed
//                    Log.e(TAG, "Youtube Initialization Failure")
//                }
//            })
    }
}

