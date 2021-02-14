package com.example.oyunmerkezi3.activity
/*
* this activity gets the details of a game and show it on the screen*/

import android.os.Bundle
import android.text.Editable
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Scroller
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.oyunmerkezi3.R
import com.example.oyunmerkezi3.database.*
import com.example.oyunmerkezi3.databinding.ActivityDetailBinding
import com.example.oyunmerkezi3.model.Comment
import com.example.oyunmerkezi3.recycling.*
import com.example.oyunmerkezi3.utils.ConnectionBroadcastReceiver
import com.example.oyunmerkezi3.utils.Utils
import com.example.oyunmerkezi3.utils.toText
import com.google.android.material.snackbar.Snackbar
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerFragment
import com.google.firebase.database.*
import kotlin.math.roundToLong

private var youTubePlayer: YouTubePlayer? = null
private lateinit var game: Game

class DetailActivity : AppCompatActivity() {
    var comments: MutableMap<String, Comment> = mutableMapOf()
    var commentList = arrayListOf<Comment>()
    private lateinit var mPlaceRef: DatabaseReference

    private lateinit var binding: ActivityDetailBinding
    var youTubePlayerFragment: YouTubePlayerFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
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
        binding.gameProperties.notificationOnPrice.setOnClickListener {
            gamesViewModel.setShowNotification(game.gameId)

        }

        game = detailActivityArgs.game
        binding.gameProperties.date.text = game.publishedDate.toText()
        binding.gameProperties.online.text = game.online.toText()
        binding.gameProperties.playersValue.text = game.playerNo.toText()
        binding.gameProperties.language.text = game.language.toText()
        binding.gameProperties.caption.text = game.caption.toText()
        downloadCommentsOnThisGame()

        title = game.gameName
        binding.gameProperties.game = game

        if (comments.isNullOrEmpty()) {
            binding.gameProperties.commentView.commentEditText.addTextChangedListener {
                if (binding.gameProperties.commentView.commentEditText.text.isNullOrBlank()) {
                    binding.gameProperties.commentView.sendImageButton.visibility = View.GONE
                } else {
                    binding.gameProperties.commentView.sendImageButton.visibility = View.VISIBLE
                    binding.gameProperties.commentView.sendImageButton.setOnClickListener {
                        sendMessage(binding.gameProperties.commentView.commentEditText.text)
                        binding.gameProperties.commentView.sendImageButton.visibility = View.GONE

                    }
                }
            }
        }
//        for (item in comments) {
//            if (commentList.none { it.userId == item.value.userId})
//                commentList.add(item.component2())
//        }
        initializeYoutubePlayer()
        val adapter = VideoAdapter(VideoListener { url ->
            youTubePlayer?.loadVideo(url)
        })
        binding.gameProperties.videoList.adapter = adapter
        adapter.data = game.URL
    }

    private fun inflateCommentsOnBottomSheet(
    ) {
        //TODO make a log in
        //TODO update the comment by add the name and if the same user add can edit his comment or delete
        binding.commentsBottomSheet.root.visibility = View.VISIBLE
        binding.gameProperties.root.visibility = View.GONE
        val adapter1 = CommentAdapter(
            CommentListener { }
        )
        val layoutManager = LinearLayoutManager(application)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.commentsBottomSheet.commentList.layoutManager = layoutManager
        binding.commentsBottomSheet.commentList.adapter = adapter1


        adapter1.submitList(commentList)
        binding.commentsBottomSheet.commentEditText.setScroller(Scroller(requireNotNull(this).application))
        binding.commentsBottomSheet.commentEditText.maxLines = 1
        binding.commentsBottomSheet.commentEditText.isVerticalScrollBarEnabled = true
        binding.commentsBottomSheet.commentEditText.movementMethod = ScrollingMovementMethod()
        binding.commentsBottomSheet.hide.setOnClickListener {
            binding.commentsBottomSheet.root.visibility = View.GONE
            binding.gameProperties.root.visibility = View.VISIBLE

        }
        binding.commentsBottomSheet.commentEditText.addTextChangedListener {
            if (binding.commentsBottomSheet.commentEditText.text.isNullOrBlank()) {
                binding.commentsBottomSheet.sendImageButton.visibility = View.GONE
            } else {
                binding.commentsBottomSheet.sendImageButton.visibility = View.VISIBLE
                binding.commentsBottomSheet.sendImageButton.setOnClickListener {
                    sendMessage(binding.commentsBottomSheet.commentEditText.text)
                    commentList.add(0,Comment((Math.random() * 100).roundToLong().toString(),binding.commentsBottomSheet.commentEditText.text.toString() ))
                    adapter1.submitList(commentList)
                    adapter1.notifyItemInserted(0)
                    layoutManager.scrollToPosition(0)
                    binding.commentsBottomSheet.commentEditText.text?.clear()
                    binding.commentsBottomSheet.profileImage.visibility = View.GONE
                    binding.commentsBottomSheet.commentEditText.visibility = View.GONE
                    binding.commentsBottomSheet.sendImageButton.visibility = View.GONE

                }
            }
        }
    }

    private fun downloadCommentsOnThisGame() {
        mPlaceRef = Utils.databaseRef?.child("comments")!!.child(game.gameId.toString())
        mPlaceRef.addValueEventListener(postListener)
        mPlaceRef.keepSynced(true)
    }

    private fun sendMessage(text: Editable?) {
        //TODO we have to look on it after making security so that it may give na error can not edit other comments
        //TODO make sure that no body will make a comment on the same key
        val uniqueKey: String = (Math.random() * 100).roundToLong().toString()//TODO change the do userID
        comments[uniqueKey] = Comment(uniqueKey, text.toString())
        mPlaceRef.updateChildren(comments as MutableMap<String, Any>)
    }

    private val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI
            for (item in dataSnapshot.children.withIndex()) {
                comments[item.value.key!!] = item.value.getValue(Comment::class.java)!!
                if (commentList.none { it.userId == item.value.getValue(Comment::class.java)!!.userId })
                    commentList.add(item.value.getValue(Comment::class.java)!!)
            }
            if (dataSnapshot.exists())
                showCommentLastComment()
        }

        override fun onCancelled(databaseError: DatabaseError) {
        }
    }

    private fun showCommentLastComment() {
        binding.gameProperties.commentView.commentEditText.visibility = View.GONE
        binding.gameProperties.commentView.commentTextView.visibility = View.VISIBLE
        binding.gameProperties.commentView.commentsLayout.setOnClickListener {
            inflateCommentsOnBottomSheet()
        }
        binding.gameProperties.commentView.commentTextView.text =
            comments.maxByOrNull { it.value.date }?.component2()?.message
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
            for (item in dataSnapshot.children.withIndex()) {
                comments.put(item.value.key!!, item.value.getValue(Comment::class.java)!!)
                commentList.add(item.value.getValue(Comment::class.java)!!)

            }
            if (dataSnapshot.exists())
                showCommentLastComment()
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
        override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
        override fun onCancelled(databaseError: DatabaseError) {}
    }
}