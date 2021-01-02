package com.example.oyunmerkezi3.activity

import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.navArgs
import com.example.oyunmerkezi3.DetailViewModel
import com.example.oyunmerkezi3.DetailViewModelFactory
import com.example.oyunmerkezi3.R
import com.example.oyunmerkezi3.database.Game
import com.example.oyunmerkezi3.databinding.ActivityDetailBinding
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayer.PlayerStyle
import com.google.android.youtube.player.YouTubePlayerView


class DetailActivity : YouTubeBaseActivity() , YouTubePlayer.OnInitializedListener{
    private lateinit var binding: ActivityDetailBinding
    private var youTubeView: YouTubePlayerView? = null
    private lateinit var viewModel: DetailViewModel
    private lateinit var viewModelFactory: DetailViewModelFactory
    private lateinit var game: Game
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         binding = DataBindingUtil.setContentView<ActivityDetailBinding>(
             this,
             R.layout.activity_detail
         )
        val detailActivityArgs by navArgs<DetailActivityArgs>()
        game = detailActivityArgs.game
        youTubeView = binding.youtubeView

        // Initializing video player with developer key
        youTubeView?.initialize("AIzaSyCbfkNTBYJp0JEp8hM4J0TCEm_EcnIvwig", this);


    }
    override fun onInitializationFailure(
        provider: YouTubePlayer.Provider?,
        errorReason: YouTubeInitializationResult
    ) {
        if (errorReason.isUserRecoverableError) {
            errorReason.getErrorDialog(this, 5).show()
        } else {
            val errorMessage = String.format(
                "getString(xsdfsdf)", errorReason.toString()
            )
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    override fun onInitializationSuccess(
        provider: YouTubePlayer.Provider?,
        player: YouTubePlayer, wasRestored: Boolean
    ) {
        if (!wasRestored) {

            // loadVideo() will auto play video
            // Use cueVideo() method, if you don't want2 to play it automatically
            player.loadVideos(game.URL)

            // Hiding player controls
//            player.setPlayerStyle(PlayerStyle.MINIMAL)
        }
    }
}