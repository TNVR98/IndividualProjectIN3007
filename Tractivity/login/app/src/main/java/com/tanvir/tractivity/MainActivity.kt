package com.tanvir.tractivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
       var atg = AnimationUtils.loadAnimation(this,R.anim.atg)
        iv_picstatictic.startAnimation(atg)
        tv_welcome.startAnimation(AnimationUtils.loadAnimation(this,R.anim.bgone))
        tv_description.startAnimation(AnimationUtils.loadAnimation(this,R.anim.bgone))
        l_buttons.startAnimation(AnimationUtils.loadAnimation(this,R.anim.bgtwo))


        bt_login.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }

        bt_signUp.setOnClickListener{
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }



    }
}