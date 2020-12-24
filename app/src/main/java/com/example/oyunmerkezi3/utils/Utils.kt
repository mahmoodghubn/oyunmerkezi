package com.example.oyunmerkezi3.utils
//
import android.util.Log
import com.example.oyunmerkezi3.database.Game
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
//class Utils {
//
//    companion object {
//        private val firebaseDatabase: FirebaseDatabase = Firebase.database
//
//        init {
//            firebaseDatabase.setPersistenceEnabled(true)
//
//
//
//        }
//
//        fun getDatabase() : FirebaseDatabase {
//            return firebaseDatabase
//        }
//    }
//}

//

object Utils {
    private var mDatabaseRef: DatabaseReference? = null
    private var database: FirebaseDatabase? = null

    val databaseRef: DatabaseReference?
        get() {
            if (mDatabaseRef == null) {
                getDatabase()
                mDatabaseRef = database!!.reference
            }
            return mDatabaseRef
        }

    private fun getDatabase(): FirebaseDatabase? {
        if (database == null) {
            Firebase.database.setPersistenceEnabled(true)
            database = Firebase.database
        }
        return database
    }
}