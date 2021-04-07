package com.tanvir.tractivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

@Suppress("DEPRECATION")
open class BaseActivity : AppCompatActivity() {
    private var backPressedOnce = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

    }


    fun hideStatusBar(){
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }


    fun getCurrentUserID():String{
        return FirebaseAuth.getInstance().currentUser!!.uid

    }
    //prevent user from accidentally exiting the app

    fun doubleBackExit() {
        if(backPressedOnce){
            super.onBackPressed()
            return
        }else{
            this.backPressedOnce = true
            Toast.makeText(this, "press back again to exit",Toast.LENGTH_SHORT).show()
            Handler().postDelayed({backPressedOnce = false},2000 )
        }
    }


    //display error massage as snack bar

    fun showError(errorMessage:String){
        val snackBar= Snackbar.make(findViewById(android.R.id.content),errorMessage,Snackbar.LENGTH_LONG)
        snackBar.show()
    }
}