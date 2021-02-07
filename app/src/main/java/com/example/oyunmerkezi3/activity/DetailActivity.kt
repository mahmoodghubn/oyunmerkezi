package com.example.oyunmerkezi3.activity
/*
* this activity gets the details of a game and show it on the screen*/
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navArgs
import com.example.oyunmerkezi3.R
import com.example.oyunmerkezi3.database.*
import com.example.oyunmerkezi3.databinding.ActivityDetailBinding
import com.example.oyunmerkezi3.recycling.VideoAdapter
import com.example.oyunmerkezi3.recycling.VideoListener
import com.example.oyunmerkezi3.utils.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.youtube.player.*
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference

private var youTubePlayer: YouTubePlayer? = null
private lateinit var game: Game
var comments: ArrayList<String> = ArrayList<String>()

class DetailActivity : AppCompatActivity() {
    private lateinit var mPlaceRef: DatabaseReference

    private lateinit var binding: ActivityDetailBinding
    var youTubePlayerFragment: YouTubePlayerFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView<ActivityDetailBinding>(
            this,
            R.layout.activity_detail
        )

        var firstCreation = true
        ConnectionBroadcastReceiver.registerToActivityAndAutoUnregister(
            this,
            object : ConnectionBroadcastReceiver() {
                override fun onConnectionChanged(hasConnection: Boolean) {
                    if (!hasConnection || !firstCreation) {
                        firstCreation = false
                        showInternetStatus(hasConnection)
                    }
                }
            })
        val detailActivityArgs by navArgs<DetailActivityArgs>()
        val application = requireNotNull(this).application
        val dataSource = GameDatabase.getInstance(application).gameDatabaseDao

        val viewModelFactory =
            GamesViewModelFactory(dataSource, application)
        val gamesViewModel =
            ViewModelProvider(this, viewModelFactory).get(GamesViewModel::class.java)
        binding.notificationOnPrice.setOnClickListener {
            gamesViewModel.setShowNotification(game.gameId)

        }

        game = detailActivityArgs.game

        binding.date.text = game.publishedDate.toText()
        binding.online.text = game.online.toText()
        binding.playersValue.text = game.playerNo.toText()
        binding.language.text = game.language.toText()
        binding.caption.text = game.caption.toText()
        downloadCommentsOnThisGame()

        title = game.gameName
        binding.game = game

        initializeYoutubePlayer()
        val adapter = VideoAdapter(VideoListener { url ->
            youTubePlayer?.loadVideo(url)
        })
        binding.videoList.adapter = adapter
        adapter.submitList(game.URL)
    }

    private fun downloadCommentsOnThisGame() {
        val mPlaceRef = Utils.databaseRef?.child("platforms")!!.child(game.gameId.toString())
        mPlaceRef.addChildEventListener(mChildEventListener)
        mPlaceRef.keepSynced(true)
    }

    private fun sendMessage(text: Editable?) {
        mPlaceRef.push().setValue(text.toString())
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

    fun showInternetStatus(connected: Boolean) {
        if (connected) {
            val snackBar = Snackbar
                .make(binding.root, "connected", Snackbar.LENGTH_LONG)
            snackBar.show()
        } else {
            val snackBar = Snackbar
                .make(binding.root, "disconnected", Snackbar.LENGTH_LONG)
            snackBar.show()
        }
    }

    private val mChildEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val comment = dataSnapshot.getValue(String::class.java)
            comment?.let {
                comments.add(comment)
                showComments()
            }

        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
        override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
        override fun onCancelled(databaseError: DatabaseError) {}
    }

    private fun showComments() {
        if (comments.isEmpty()) {
            binding.commentView.commentEditText.addTextChangedListener {
                if (binding.commentView.commentEditText.text.isBlank()) {
                    binding.commentView.sendImageButton.visibility = View.GONE

                } else {
                    binding.commentView.sendImageButton.visibility = View.VISIBLE
                    binding.commentView.sendImageButton.setOnClickListener {
                        sendMessage(binding.commentView.commentEditText.text)
                    }
                }
            }
        } else {
            binding.commentView.commentEditText.visibility = View.GONE
            binding.commentView.commentTextView.visibility = View.VISIBLE
            binding.commentView.commentTextView.text = comments.first()
        }
    }
}