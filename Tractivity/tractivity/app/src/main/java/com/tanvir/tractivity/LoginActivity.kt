package com.tanvir.tractivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

@Suppress("DEPRECATION")
class LoginActivity : BaseActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        //window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        auth = Firebase.auth

        bt_login.setOnClickListener{
            loginUser()
        }

        tv_signUP.setOnClickListener{
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    //logs in the existing user
    private fun loginUser (){
        val email:String = et_email_login.text.toString().trim{ it <= ' '}
        val password:String = et_password_login.text.toString()
        if(validateLogin(email,password)){
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("Login", "LoginInWithEmail:success")
                        val user = auth.currentUser
                        Toast.makeText(this,
                            "Welcome back to Tractivity",Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, TractivityMain::class.java)
                        startActivity(intent)
                        finish()


                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Login", "LogInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()

                    }
                }
        }
    }

    //validates the login form
    private fun validateLogin ( email: String ,
                                password: String) :Boolean{
        when {

            TextUtils.isEmpty(email) ->{
                showError("Please enter your email address")
                return false
            }
            TextUtils.isEmpty(password) ->{
                showError("Please enter your password")
                return false
            }
            else ->{
                return true

            }

        }
    }
}