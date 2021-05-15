package com.tanvir.tractivity.activities

import android.app.AlertDialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tanvir.tractivity.Constants
import com.tanvir.tractivity.R
import com.tanvir.tractivity.model.FireStoreClass
import com.tanvir.tractivity.model.UserClass
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.dialog_change_pass.*
import kotlinx.android.synthetic.main.dialog_change_pass.view.*

class UserProfileActivity : BaseActivity(){

     lateinit var gbloggedUser : UserClass


    val firestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        hideStatusBar()

        setupActionBar()

        populateProfileActivity()

        bt_change_pass.setOnClickListener{
            changePass()
        }


    }


    //CUSTOM ACTION BAR
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
                    tv_score.text = ""
                }
            }
                .addOnFailureListener{
                    exception ->
                    Log.d(ContentValues.TAG, "get failed with ", exception)
                }

        }

    private fun changePass(){
        val changePassDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_change_pass,null)
        val changePassDialogBuilder = AlertDialog.Builder(this).setView(changePassDialogView)
        val changePassDialog = changePassDialogBuilder.show()
        changePassDialogView.bt_submit.setOnClickListener{
            val password = changePassDialogView.et_pass.text.toString()
            val reTypedPassword = changePassDialogView.et_rePass.text.toString()
            if(isPasswordMatched(password,reTypedPassword)){
                FirebaseAuth.getInstance().currentUser!!.updatePassword(password).addOnCompleteListener{
                    task ->
                    if(task.isSuccessful){
                        Toast.makeText(this, "Password changed Successfully",
                            Toast.LENGTH_SHORT).show()
                        changePassDialog.dismiss()
                    }else{
                        Toast.makeText(this,task.exception!!.message.toString(),Toast.LENGTH_LONG).show()
                    }
                }
            }else{
                Toast.makeText(this, "Passwords do not match",
                    Toast.LENGTH_SHORT).show()
            }
        }
        changePassDialog.bt_cancel.setOnClickListener{
            changePassDialog.cancel()
        }
    }
    //checks if both the passwords matches
    private fun isPasswordMatched (password:String, reTypedPassword:String) :Boolean{
        return password == reTypedPassword
    }
}






