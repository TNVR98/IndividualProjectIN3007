package com.tanvir.tractivity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_up.*

@Suppress("DEPRECATION")
class SignUpActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        auth = Firebase.auth

        bt_SignUp.setOnClickListener {
            createUser()
        }

        tv_login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            }
    }

    // user is created and stored on firestore
    private fun createUser(){
        val name: String = et_name.text.toString().trim{ it <= ' '}
        val email: String = et_email.text.toString().trim{ it <= ' '}
        val password : String = et_password.text.toString()
        val reTypedPassword : String = et_reTypePassword.text.toString()

        if(validateSignUp(name,email,password,reTypedPassword)){
            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this){
                task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
//                    val user: FirebaseUser = task.result!!.user!!
//                    val userEmail = user.email!!
                    val user = auth.currentUser
                    Toast.makeText(this,
                        "Welcome $name to Tractivity",Toast.LENGTH_SHORT).show()
                    FirebaseAuth.getInstance().signOut()
                    finish()
                }else {
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    //function to validate the sighUp form
    private fun validateSignUp (name:String , email: String ,
                                password: String, reTypedPassword : String) :Boolean{
        when {
            TextUtils.isEmpty(name) ->{
                showError("Please enter your name")
                return false
            }
            TextUtils.isEmpty(email) ->{
                showError("Please enter your email address")
                return false
            }
            TextUtils.isEmpty(password) ->{
                showError("Please enter a password")
                return false
            }
            TextUtils.isEmpty(reTypedPassword) ->{
                showError("Please re-Type your password")
                return false
            }
            !isPasswordMatched(password,reTypedPassword) ->{
                return false
            }
            else ->{
                return true

            }

        }
    }

    //checks if both the passwords matches
    private fun isPasswordMatched (password:String, reTypedPassword:String) :Boolean{
        return if(password.equals(reTypedPassword,false)){
            true
        }else{
            showError("Password does not match, please retype")
            false
        }

    }

}