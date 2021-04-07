package com.tanvir.tractivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
       var atg = AnimationUtils.loadAnimation(this,R.anim.atg)
        iv_picstatictic.startAnimation(atg)
        tv_welcome.startAnimation(AnimationUtils.loadAnimation(this,R.anim.bgone))
        tv_description.startAnimation(AnimationUtils.loadAnimation(this,R.anim.bgone))
        l_buttons.startAnimation(AnimationUtils.loadAnimation(this,R.anim.bgtwo))

        var currentUserID = FireStoreClass().getCurrentUserID()

        //STAY LOGGED IN
        //intent to TractivityMain activity if the user is logged in
        if(currentUserID.isNotEmpty()){
            startActivity(Intent(this,TractivityMain::class.java))
            finish()
        }

        bt_login.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()

        }

        bt_signUp.setOnClickListener{
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }



    }
}