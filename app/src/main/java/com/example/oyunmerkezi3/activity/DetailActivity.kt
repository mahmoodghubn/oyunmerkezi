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
import com.example.oyunmerkezi3.model.Comment
import com.example.oyunmerkezi3.recycling.VideoAdapter
import com.example.oyunmerkezi3.recycling.VideoListener
import com.example.oyunmerkezi3.utils.ConnectionBroadcastReceiver
import com.example.oyunmerkezi3.utils.Utils
import com.example.oyunmerkezi3.utils.toText
import com.google.android.material.snackbar.Snackbar
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerFragment
import com.google.firebase.database.*

private var youTubePlayer: YouTubePlayer? = null
private lateinit var game: Game

class DetailActivity : AppCompatActivity() {
    var comments: ArrayList<Comment> = arrayListOf<Comment>()
    //var comments: MutableMap<String, Comment> = mutableMapOf<String, Comment>()
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

        if (comments.isNullOrEmpty()) {
            binding.commentView.commentEditText.addTextChangedListener {
                if (binding.commentView.commentEditText.text.isBlank()) {
                    binding.commentView.sendImageButton.visibility = View.GONE
                } else {
                    binding.commentView.sendImageButton.visibility = View.VISIBLE
                    binding.commentView.sendImageButton.setOnClickListener {
                        sendMessage(binding.commentView.commentEditText.text)
                        binding.commentView.sendImageButton.visibility = View.GONE

                    }
                }
            }
        }
        initializeYoutubePlayer()
        val adapter = VideoAdapter(VideoListener { url ->
            youTubePlayer?.loadVideo(url)
        })
        binding.videoList.adapter = adapter
        adapter.data = game.URL
    }

    private fun downloadCommentsOnThisGame() {
        mPlaceRef = Utils.databaseRef?.child("comments")!!.child(game.gameId.toString())
        mPlaceRef.addChildEventListener(mChildEventListener)
        mPlaceRef.keepSynced(true)
    }

    private fun sendMessage(text: Editable?) {
        //TODO the following key could lead to error should look for a solution
        //TODO we have to look on it after making security so that it may give na error can not edit other comments
//        comments[{ comments.size + 1 }.toString()] = Comment(text.toString())
        //TODO make sure that no body will make a comment on the same key
//        mPlaceRef.updateChildren(comments as Map<String, Any>)
        comments.add(Comment(text.toString()))
        mPlaceRef.push().setValue(comments)
    }

    private val mChildEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
//            val genericTypeIndicator: GenericTypeIndicator<Map<String, Comment>> =
//                object : GenericTypeIndicator<Map<String, Comment>>() {}
            val genericTypeIndicator: GenericTypeIndicator<ArrayList<Comment>> =
                object : GenericTypeIndicator<ArrayList<Comment>>() {}
            comments = dataSnapshot.getValue(genericTypeIndicator)!!
            Log.i("mahmoditecomments",comments.toString())
//            comments = dataSnapshot.getValue(genericTypeIndicator) as MutableMap<String, Comment>

            showCommentLastComment()
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
        override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
        override fun onCancelled(databaseError: DatabaseError) {}
    }

    private fun showCommentLastComment() {
        binding.commentView.commentEditText.visibility = View.GONE
        binding.commentView.commentTextView.visibility = View.VISIBLE
        binding.commentView.commentTextView.setOnClickListener {
            inflateCommentsOnBottomSheet()
        }
//           binding.commentView.commentTextView.text = comments["0"]?.message ?: ""
        binding.commentView.commentTextView.text = comments.maxByOrNull { it.date }!!.message ?: ""
    }

    private fun inflateCommentsOnBottomSheet(
    ) {
        //TODO we may wanna change the arrayList to map
        //TODO show the bottom sheet
        //TODO add a comment
        //TODO make a log in
        //TODO update the comment by add the name and if the same user add can edit his comment or delete
        for (item in comments)
            Log.i("mahmoodite", item?.message ?:"")
//            Log.i("mahmoodite", item.component2().message)
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
}