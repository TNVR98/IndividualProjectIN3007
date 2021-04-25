package com.tanvir.tractivity.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tanvir.tractivity.Constants
import com.tanvir.tractivity.R
import com.tanvir.tractivity.RecordAdapter
import com.tanvir.tractivity.model.ActivityClass
import com.tanvir.tractivity.model.ActivityRecordClass
import com.tanvir.tractivity.model.FireStoreClass
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.app_bar2.iv_back
import kotlinx.android.synthetic.main.app_bar3.*

@Suppress("DEPRECATION")
class DetailsActivity : AppCompatActivity() {
    private val fireStore = Firebase.firestore
    private lateinit var activityName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        if(intent.hasExtra(Constants.ACTIVITYNAME)){
            activityName = intent.getStringExtra(Constants.ACTIVITYNAME)!!
        }

        displayActivityDetailsFromDB()
        displayRecordListFromDB()

        iv_back.setOnClickListener{
            onBackPressed()
        }
        iv_delete.setOnClickListener{

        }

    }
    fun displayRecordListFromDB(){
        fireStore.collection(Constants.USERS).document(FireStoreClass().getCurrentUserID())
            .collection(Constants.ACTIVITIES).document(activityName).collection(Constants.RECORDS)
            .get()
            .addOnSuccessListener { records ->
                Log.d("DB", "All documents received")
                val recordList = ArrayList<ActivityRecordClass>()
                for (record in records) {
                    val record: ActivityRecordClass? = record.toObject(ActivityRecordClass::class.java)
                    if (record != null) {
                        recordList.add(record)
                    }
                }
                populateRecordsRv(recordList)
            }.addOnFailureListener { exception ->
                Log.d("DB", "Error getting documents: ", exception)
            }

    }

    fun populateRecordsRv (recordList: ArrayList<ActivityRecordClass>) {
        if (recordList.size> 0){
            rv_recordList.layoutManager = LinearLayoutManager(this)
            rv_recordList.setHasFixedSize(true)

            val adapter =  RecordAdapter(recordList)
            rv_recordList.adapter = adapter

            }
    }


    fun displayActivityDetailsFromDB (){
        fireStore.collection(Constants.USERS).document(FireStoreClass().getCurrentUserID())
            .collection(Constants.ACTIVITIES).document(activityName).get()
            .addOnSuccessListener{ document ->
                Log.d("DB", "document received")
                val activity : ActivityClass? = document.toObject(ActivityClass::class.java)
                populateActivityDetails(activity!!)

            }.addOnFailureListener { exception ->
                Log.d("DB", "Error getting the document: ", exception)
            }
    }

    @SuppressLint("SetTextI18n")
    fun populateActivityDetails(activity:ActivityClass){
        tv_activityName.text=activity.name
        tv_description.text= activity.description
        tv_startedDate.text= "Started Date: ${activity.date}"
        tv_dueDate.text= "Due date: ${activity.dueDate}"

    }

}