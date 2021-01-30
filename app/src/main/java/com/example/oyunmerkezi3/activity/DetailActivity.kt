package com.example.oyunmerkezi3.activity
/*
* this activity gets the details of a game and show it on the screen*/
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navArgs
import androidx.preference.PreferenceManager
import com.example.oyunmerkezi3.R
import com.example.oyunmerkezi3.database.Game
import com.example.oyunmerkezi3.database.GameDatabase
import com.example.oyunmerkezi3.database.GamesViewModel
import com.example.oyunmerkezi3.database.GamesViewModelFactory
import com.example.oyunmerkezi3.databinding.ActivityDetailBinding
import com.example.oyunmerkezi3.recycling.VideoAdapter
import com.example.oyunmerkezi3.recycling.VideoListener
import com.google.android.youtube.player.*

private var youTubePlayer: YouTubePlayer? = null
private lateinit var game: Game

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    var youTubePlayerFragment: YouTubePlayerFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         binding = DataBindingUtil.setContentView<ActivityDetailBinding>(
             this,
             R.layout.activity_detail
         )
        val detailActivityArgs by navArgs<DetailActivityArgs>()

        val platformSharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this)
        val application = requireNotNull(this).application
        val dataSource = GameDatabase.getInstance(application).gameDatabaseDao
        val currentPlatform = platformSharedPreferences.getString("current", "PS4")

        val viewModelFactory =
            GamesViewModelFactory(dataSource, application, currentPlatform!!)
        val gamesViewModel =
            ViewModelProvider(this, viewModelFactory).get(GamesViewModel::class.java)
        binding.notificationOnPrice.setOnClickListener{
            gamesViewModel.setFavorite(game.gameId)

        }

        game = detailActivityArgs.game
        title = game.gameName
        binding.game = game

        initializeYoutubePlayer()
        val adapter = VideoAdapter(VideoListener { url ->
            youTubePlayer?.loadVideo(url)
        })
        binding.videoList.adapter = adapter
        adapter.submitList(game.URL)
    }

    private fun initializeYoutubePlayer() {
        youTubePlayerFragment =
            fragmentManager.findFragmentById(R.id.youtube_player_fragment) as YouTubePlayerFragment
        if (youTubePlayerFragment == null) return
        youTubePlayerFragment!!.initialize(
            "AIzaSyCbfkNTBYJp0JEp8hM4J0TCEm_EcnIvwig",
            object : YouTubePlayer.OnInitializedListener {
                override fun onInitializationSuccess(
                    provider: YouTubePlayer.Provider, player: YouTubePlayer,
                    wasRestored: Boolean
                ) {
                    if (!wasRestored) {
                        youTubePlayer = player
                        //set the player style default
                        youTubePlayer!!.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT)
                        youTubePlayer!!.loadVideos(game.URL)
                    }
                }

                override fun onInitializationFailure(
                    arg0: YouTubePlayer.Provider,
                    arg1: YouTubeInitializationResult
                ) {
                    //print or show error if initialization failed
                    Log.e("Detail Activity", "Youtube Player View initialization failed")
                }
            })
    }

}