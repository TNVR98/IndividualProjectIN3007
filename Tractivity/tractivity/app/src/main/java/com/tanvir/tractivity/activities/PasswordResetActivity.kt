package com.tanvir.tractivity.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.tanvir.tractivity.R
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_password_reset.*

/**
 *  the user can reset their password if forgotten.
 *  adapted from https://firebase.google.com/docs/auth/web/manage-users
 *  and https://www.youtube.com/watch?v=nVhPqPpgndM
 */
@Suppress("DEPRECATION")
class PasswordResetActivity : AppCompatActivity() {

    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_reset)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        // reset password
        btn_resetPass.setOnClickListener{
            val email:String = et_email_passReset.text.toString().trim{ it <= ' '}
            // check if the email was entered
            if (TextUtils.isEmpty(email)){
                Toast.makeText(this, "Please enter your email Address",
                    Toast.LENGTH_SHORT).show()
            } else{
                //send the password reset email
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener{
                    task ->
                    if(task.isSuccessful){
                        Toast.makeText(this, "Email sent, check your inbox",
                            Toast.LENGTH_SHORT).show()
                        finish()
                        onBackPressed()
                    }else{
                        Toast.makeText(this,task.exception!!.message.toString(),Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}