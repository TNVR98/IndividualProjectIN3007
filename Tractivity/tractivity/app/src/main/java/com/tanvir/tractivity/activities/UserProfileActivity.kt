package com.tanvir.tractivity.activities

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tanvir.tractivity.Constants
import com.tanvir.tractivity.R
import com.tanvir.tractivity.model.UserClass
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserProfileActivity : BaseActivity(){

     lateinit var gbloggedUser : UserClass


    val firestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        hideStatusBar()

        setupActionBar()

        populateProfileActivity()


    }


    //CUSTOM ACTION BAR (move to base activity)
    private fun setupActionBar() {
        setSupportActionBar(toolbar_profile)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back)
        actionBar.title = "Profile"

        toolbar_profile.setNavigationOnClickListener{
            onBackPressed() }
        }

    // POPULATE THE PROFILE PAGE WITH USER DATA FROM DB
    fun populateProfileActivity () {
        firestore.collection(Constants.USERS)
            .document(getCurrentUserID()).get()
            .addOnSuccessListener {documentSnapshot ->
                Log.d(ContentValues.TAG, "Document received")
                val loggedUser: UserClass? = documentSnapshot.toObject(UserClass::class.java)
                if (loggedUser != null) {
                    gbloggedUser = loggedUser
                    tv_username_profile.text = loggedUser.name
                    tv_profile_name.text=loggedUser.name
                    tv_profile_email.text = loggedUser.email
                    tv_score.text = loggedUser.score.toString()

                }

            }
                .addOnFailureListener{
                    exception ->
                    Log.d(ContentValues.TAG, "get failed with ", exception)
                }

        }
}






