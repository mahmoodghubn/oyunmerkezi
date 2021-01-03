package com.example.oyunmerkezi3.recycling


import android.widget.TextView
import androidx.databinding.BindingAdapter


@BindingAdapter("videoUrl")
fun TextView.setVideoURL(item: String?) {
    item?.let {
        text = item
    }
}


//@BindingAdapter("videoThumbnailImageView")
//fun YouTubeThumbnailView.setThumbnail(item: Game?) {
//
//    item?.let {
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
//    }
//}