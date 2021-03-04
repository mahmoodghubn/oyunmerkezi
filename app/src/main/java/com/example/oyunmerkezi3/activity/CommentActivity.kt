package com.example.oyunmerkezi3.activity

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.RatingBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import com.example.oyunmerkezi3.GamesFragment
import com.example.oyunmerkezi3.R
import com.example.oyunmerkezi3.model.Comment
import com.example.oyunmerkezi3.utils.Utils
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlin.properties.Delegates

class CommentActivity : AppCompatActivity() {
    private lateinit var menuItem: MenuItem
    private var rating by Delegates.notNull<Float>()
    private var text by Delegates.notNull<String>()
    var user = Firebase.auth.currentUser

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
        setSupportActionBar(findViewById(R.id.toolbar))
        val mActionBar: Toolbar = findViewById(R.id.toolbar)
        mActionBar.navigationIcon = ResourcesCompat.getDrawable(
            resources,
            R.drawable.ic_baseline_arrow_back_24,
            null
        )
        mActionBar.setNavigationOnClickListener {
            finish()
        }
        mActionBar.title = game.gameName

        val bundle = intent.extras?.getBundle("bundle")
        bundle?.let {
            rating = bundle.getFloat("RATING")
            text = bundle.getString("MESSAGE")!!

        }

        findViewById<RatingBar>(R.id.rate_bar).rating = rating
        findViewById<EditText>(R.id.comment_edit_text).setText(text)
        findViewById<me.zhanghai.android.materialratingbar.MaterialRatingBar>(R.id.rate_bar).setOnRatingBarChangeListener { _, rating2, _ ->
            menuItem.isEnabled = rating2 > 0f
            rating = rating2
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menuItem = menu?.getItem(0)!!
        menuItem.isEnabled = rating > 0
        return true;
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.comment_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.send) {
            if (user == null) {
                launchSignInFlow()
            } else {
                sendComment()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sendComment() {
        user?.let {
            val text = findViewById<EditText>(R.id.comment_edit_text).editableText.toString()
            val userComment = Comment(
                it.uid,
                rating.toInt(),
                it.displayName,
                text,
                it.photoUrl.toString()
            )
            mPlaceRef.child(it.uid).setValue(userComment)
            val platform = game.platform.toString()
            rateList[(rating - 1).toInt()] = rateList[(rating - 1).toInt()].plus(1)
            Utils.databaseRef?.child("platforms")!!
                .child(platform).child(game.gameId.toString()).child("gameRating")
                .setValue(rateList)
            finish()
        }

    }

    private fun launchSignInFlow() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            GamesFragment.SIGN_IN_REQUEST_CODE
        )
    }

    companion object {
        const val SIGN_IN_REQUEST_CODE = 1001
        const val TAG = "Comment Activity"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // User successfully signed in
                user = Firebase.auth.currentUser
                Log.i(
                    TAG,
                    "Successfully signed in user ${FirebaseAuth.getInstance().currentUser?.displayName}!"
                )
                val dataSnapshot = mPlaceRef.child(user?.uid!!).get()
                dataSnapshot.addOnCompleteListener { it ->
                    if (it.result?.exists()!!) {
                        showAlertDialog()
                    } else {
                        sendComment()
                    }
                }
            } else {
                Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
            }
        }
    }

    private fun showAlertDialog() {
        val alertDialog2 = AlertDialog.Builder(
            this@CommentActivity
        )

        // Setting Dialog Title
        alertDialog2.setTitle("Previous Comment Exist...")

        // Setting Dialog Message
        alertDialog2.setMessage("Do want to override the previous comment?")

        // Setting Icon to Dialog
        alertDialog2.setIcon(R.drawable.ic_baseline_warning_24)

        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton(
            "YES"
        ) { _, _ ->
            sendComment()
        }
        // Setting Negative "NO" Btn
        alertDialog2.setNegativeButton(
            "NO"
        ) { dialog, _ ->
            dialog.cancel()
            finish()
        }

        // Showing Alert Dialog
        alertDialog2.show()
    }
}
