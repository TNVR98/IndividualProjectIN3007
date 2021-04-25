package com.tanvir.tractivity.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.tanvir.tractivity.model.ActivityClass
import com.tanvir.tractivity.model.FireStoreClass
import com.tanvir.tractivity.R
import kotlinx.android.synthetic.main.activity_add_activity.*
import java.text.SimpleDateFormat
import java.util.*

class AddActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_activity)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setupActionBar()

        bt_add.setOnClickListener{
            val activityName = et_activityName.text.toString()
            val description = et_description.text.toString()
            val dueDate =  et_dueDate.text.toString()
            if (TextUtils.isEmpty(activityName)) {
                Toast.makeText(
                    applicationContext,
                    "Please Enter Activity Name",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val activity = ActivityClass(activityName,
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                    ,description, dueDate)
                FireStoreClass().saveActivityOnDB(activity)
                val intent = Intent(this, ActivitiesActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_addActivity)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back)
        actionBar.title = "Add Activity"

        toolbar_addActivity.setNavigationOnClickListener{
            val intent = Intent(this, ActivitiesActivity::class.java)
            startActivity(intent)
            finish()

        }
    }
}