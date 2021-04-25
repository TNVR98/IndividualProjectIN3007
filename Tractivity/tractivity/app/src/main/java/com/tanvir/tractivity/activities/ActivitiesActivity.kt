package com.tanvir.tractivity.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tanvir.tractivity.*
import com.tanvir.tractivity.ActivityAdapter
import com.tanvir.tractivity.model.ActivityClass
import com.tanvir.tractivity.model.FireStoreClass
import kotlinx.android.synthetic.main.activity_activities.*
import kotlinx.android.synthetic.main.app_bar2.*

@Suppress("DEPRECATION")
class ActivitiesActivity : AppCompatActivity() {
    private val firestore = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activities)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        displayActivityListFromDB()
        iv_back.setOnClickListener{
            onBackPressed()
        }

        iv_add.setOnClickListener{
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
            finish()
        }


    }

    // POPULATE RECYCLEVIEW
    fun populateActivityListRv (activityList: ArrayList<ActivityClass>){
        if (activityList.size> 0){
            rv_activitiesList.layoutManager = LinearLayoutManager(this)
            rv_activitiesList.setHasFixedSize(true)

            val adapter =  ActivityAdapter(activityList)
            rv_activitiesList.adapter = adapter

            adapter.setOnclickListener(object: ActivityAdapter.OnItemClickListener {
                override fun onItemClick(position: Int, item: ActivityClass) {
                    val intent = Intent(this@ActivitiesActivity, DetailsActivity::class.java)
                    intent.putExtra(Constants.ACTIVITYNAME,item.name)
                    startActivity(intent)
                }

            })
        }else{
            Toast.makeText(this, "no activity stored", Toast.LENGTH_SHORT).show()
        }
    }

    fun displayActivityListFromDB(){
        firestore.collection(Constants.USERS).document(FireStoreClass().getCurrentUserID())
            .collection(Constants.ACTIVITIES)
            .get()
            .addOnSuccessListener { results ->
                Log.d("DB", "All documents received")
                val activityList = ArrayList<ActivityClass>()
                for (result in results) {
                    val activity: ActivityClass? = result.toObject(ActivityClass::class.java)
                    if (activity != null) {
                        activityList.add(activity)

                    }
                }
                populateActivityListRv(activityList)
            }.addOnFailureListener { exception ->
                Log.d("DB", "Error getting documents: ", exception)
            }

    }
}