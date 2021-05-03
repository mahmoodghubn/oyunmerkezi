package com.example.oyunmerkezi3.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.navigation.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.oyunmerkezi3.R
import com.example.oyunmerkezi3.database.*
import com.example.oyunmerkezi3.databinding.ActivityDetailBinding
import com.example.oyunmerkezi3.model.Comment
import com.example.oyunmerkezi3.recycling.*
import com.example.oyunmerkezi3.utils.Utils
import com.example.oyunmerkezi3.utils.toText
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerFragment
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


lateinit var game: Game
lateinit var mPlaceRef: DatabaseReference
var rateList = arrayListOf(0, 0, 0, 0, 0)

class DetailActivity : AppCompatActivity() {
    private var youTubePlayer: YouTubePlayer? = null
    var youTubePlayerFragment: YouTubePlayerFragment? = null
    var outerRating: Float = 0F
    var user: FirebaseUser? = null
    private var userComment: Comment? = null
    var comments: MutableMap<String, Comment> = mutableMapOf()
    var commentList = arrayListOf<Comment>()
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_detail
        )

        val detailActivityArgs by navArgs<DetailActivityArgs>()
        game = detailActivityArgs.game

        binding.gameProperties.online.text = game.online.toText()
        binding.gameProperties.caption.text = game.caption.toText()
        binding.gameProperties.language.text = game.language.toText()
        binding.gameProperties.date.text = game.publishedDate.toText()
        binding.gameProperties.playersValue.text = game.playerNo.toText()

        downloadCommentsOnThisGame()

        title = game.gameName
        binding.gameProperties.game = game

        initializeYoutubePlayer()
        val adapter = VideoAdapter(VideoListener { url ->
            youTubePlayer?.loadVideo(url)
        })
        binding.gameProperties.videoList.adapter = adapter
        adapter.data = game.URL
    }

    private fun downloadCommentsOnThisGame() {
        rateList = arrayListOf(0, 0, 0, 0, 0)
        mPlaceRef = Utils.databaseRef?.child("comments")!!.child(game.gameId.toString())
        mPlaceRef.addValueEventListener(postListener)
        mPlaceRef.keepSynced(true)
    }

    //TODO this listener is not necessary and can removed
    private val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI
            commentList = arrayListOf<Comment>()
            rateList = arrayListOf(0, 0, 0, 0, 0)
            user = Firebase.auth.currentUser
            for (item in dataSnapshot.children.withIndex()) {
                val downloadedComment = item.value.getValue(Comment::class.java)!!
                rateList[downloadedComment.gameRate - 1]++

                if (downloadedComment.userId == user?.uid) {
                    userComment = downloadedComment

                } else if (downloadedComment.message != "") {
                    comments[item.value.key!!] = downloadedComment
                    commentList.add(item.value.getValue(Comment::class.java)!!)
                }

            }
            if (rateList.sum() > 0)
                binding.gameProperties.totalRaters.text = "${rateList.sum()} rater(s)"
            showUserComment()
        }

        override fun onCancelled(databaseError: DatabaseError) {
        }
    }

    private fun showUserComment() {
        binding.gameProperties.userCommentView.apply {
            commentsCount.visibility = View.GONE
            expandImageButton.visibility = View.GONE
            sendText.visibility = View.VISIBLE
            commentsTitle.textSize = 20f
        }

        if (userComment != null) {
            binding.gameProperties.userCommentView.apply {
                deleteMenu.visibility = View.VISIBLE
                rateBar.visibility = View.GONE
                commentsTitle.text = "your comment"
                sendText.text = "change this comment"
                deleteMenu.setOnClickListener {
                    showPopup(it)
                }
                sendText.setOnClickListener {
                    val baseBundle = Bundle()
                    baseBundle.putFloat("RATING", userComment!!.gameRate.toFloat())
                    baseBundle.putString("MESSAGE", userComment!!.message)
                    val intent = Intent(this@DetailActivity, CommentActivity::class.java).apply {
                        putExtra("bundle", baseBundle)
                    }
                    startActivity(intent)
                }
                commentView.apply {
                    divider.visibility = View.GONE
                    root.visibility = View.VISIBLE
                    usernameTextView.text = userComment?.userName
                    gameRatingBar.rating = userComment!!.gameRate.toFloat()
                    dateTextView.text = userComment!!.date.toText()
                    if (userComment!!.message.isNullOrBlank()) {
                        commentTextView.visibility = View.GONE
                    } else {
                        commentTextView.visibility = View.VISIBLE
                        commentTextView.text = userComment!!.message ?: ""
                    }
                }
            }
            val profile = binding.gameProperties.userCommentView.commentView.profileImage
            user?.photoUrl.let {
                Glide.with(applicationContext).load(it).error(R.drawable.ic_baseline_face_24)
                    .apply(RequestOptions.circleCropTransform()).into(profile)
            }
        } else {
            binding.gameProperties.userCommentView.apply {
                commentView.root.visibility = View.GONE
                deleteMenu.visibility = View.GONE
                rateBar.visibility = View.VISIBLE
                commentsTitle.text = "rate this game"
                sendText.text = "send comment"
                rateBar.setOnRatingBarChangeListener { _, rating, _ ->
                    if (rating > 0) {
                        outerRating = rating
                        sendComment()
                    }
                }
                sendText.setOnClickListener {
                    sendComment()
                }
            }
        }

        comments.remove(userComment?.userId)

        val lastComment = comments.maxByOrNull { it.component2().date }?.component2()
        lastComment?.let {
            showLastComment(it)
        }
        if (lastComment == null) {
            binding.gameProperties.commentViewFrameLayout.visibility = View.GONE
        }
    }

    private fun sendComment() {
        val baseBundle = Bundle()
        baseBundle.putFloat("RATING", outerRating)
        baseBundle.putString("MESSAGE", "")
        val intent = Intent(this@DetailActivity, CommentActivity::class.java).apply {
            putExtra("bundle", baseBundle)
        }
        startActivity(intent)
    }

    private fun showLastComment(lastComment: Comment) {
        binding.gameProperties.commentView.apply {
            commentView.root.visibility = View.VISIBLE
            commentsCount.visibility = View.VISIBLE
            expandImageButton.visibility = View.VISIBLE
            rateBar.visibility = View.GONE
            sendText.visibility = View.GONE
            deleteMenu.visibility = View.GONE
            commentView.divider.visibility = View.GONE
            commentsCount.text = commentList.size.toString()
            commentsTitle.text = "comments"
            commentView.usernameTextView.text = lastComment.userName
            commentView.gameRatingBar.rating = lastComment.gameRate.toFloat()
            commentView.dateTextView.text = lastComment.date.toText()
            commentView.commentTextView.text = lastComment.message
            expandImageButton.setOnClickListener {
                inflateCommentsOnBottomSheet()
            }
        }
        val userprofile = binding.gameProperties.commentView.commentView.profileImage
        lastComment.photoUri?.let {
            Glide.with(applicationContext).load(it).error(R.drawable.ic_baseline_face_24)
                .apply(RequestOptions.circleCropTransform()).into(userprofile)
        }
    }

    private fun inflateCommentsOnBottomSheet() {
        val adapter1 = CommentAdapter(CommentListener { })
        val layoutManager = LinearLayoutManager(application)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.gameProperties.root.visibility = View.GONE
        binding.commentsBottomSheet.apply {
            root.visibility = View.VISIBLE
            commentList.layoutManager = layoutManager
            commentList.adapter = adapter1
            hide.setOnClickListener {
                binding.commentsBottomSheet.root.visibility = View.GONE
                binding.gameProperties.root.visibility = View.VISIBLE
            }
        }
        adapter1.submitList(commentList)

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
                        youTubePlayer?.let {
                            it.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT)
                            it.loadVideos(game.URL)
                            it.setShowFullscreenButton(false)
                        }
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

    private fun showPopup(v: View) {
        val popup = PopupMenu(this, v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.delete_comment_menu, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.delete) {
                mPlaceRef.child(user?.uid!!).removeValue()
                val platform = game.platform.toString()
                val userRating = userComment?.gameRate!!
                rateList[userRating - 1] = rateList[userRating - 1].minus(1)
                Utils.databaseRef?.child("platforms")!!
                    .child(platform).child(game.gameId.toString()).child("gameRating")
                    .setValue(rateList)
                userComment = null
            }
            true
        }
        popup.show()
    }
}