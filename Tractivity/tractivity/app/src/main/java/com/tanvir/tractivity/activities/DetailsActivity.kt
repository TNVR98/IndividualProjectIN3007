package com.tanvir.tractivity.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tanvir.tractivity.R
import com.tanvir.tractivity.constants.Constants
import com.tanvir.tractivity.adapters.RecordAdapter
import com.tanvir.tractivity.model.ActivityClass
import com.tanvir.tractivity.model.ActivityRecordClass
import com.tanvir.tractivity.model.FireStoreClass
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.app_bar2.iv_back
import kotlinx.android.synthetic.main.app_bar3.*
import kotlinx.android.synthetic.main.dialog_edit_activity.view.*

@Suppress("DEPRECATION")
/**
 * the activity details screen was implented in this class with the GUI designed in activity_details.xml file
 */
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
        displayProgress()

        iv_back.setOnClickListener{
            onBackPressed()
        }
        iv_delete.setOnClickListener{
            confirmationDialog()
        }
        iv_edit.setOnClickListener{
            displayActivityEditDialog()
        }

    }
    //query the progress records from the database and display in the recycleView
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

    //populate teh record RecycleView with retrieved data from the firestore
    private fun populateRecordsRv (recordList: ArrayList<ActivityRecordClass>) {
        if (recordList.size> 0){
            rv_recordList.layoutManager = LinearLayoutManager(this)
            rv_recordList.setHasFixedSize(true)

            val adapter =  RecordAdapter(recordList)
            rv_recordList.adapter = adapter

            }
    }

    //query the details of the activity clicked from the database
    private fun displayActivityDetailsFromDB (){
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

    // Populate the details sections
    @SuppressLint("SetTextI18n")
    fun populateActivityDetails(activity:ActivityClass){
        tv_activityName.text=activity.name
        tv_description.text= activity.description
        tv_startedDate.text= "Started Date: ${activity.date}"
        tv_dueDate.text= "Due date: ${activity.dueDate}"

    }

    // Delete an activity from the database
    private fun deleteActivity(){
        fireStore.collection(Constants.USERS).document(FireStoreClass().getCurrentUserID())
            .collection(Constants.ACTIVITIES).document(activityName).delete()
            .addOnSuccessListener {
                Log.d("DATA", "Activity has been deleted")
                Toast.makeText(this,
                    "Activity has been deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e -> Log.w("DATA", "Error deleting document", e) }
    }

    //display and confirmation dialog before deleting the activity
    private fun confirmationDialog(){
        val confBuilder = AlertDialog.Builder(this)
        confBuilder.setTitle("Delete Activity?")
        confBuilder.setPositiveButton("YES") { dialog: DialogInterface, i: Int ->
            deleteActivity()
            val intent = Intent(this, ActivitiesActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            dialog.cancel()
        }
        confBuilder.setNegativeButton("NO") { dialog: DialogInterface, i: Int ->
            dialog.cancel()
        }
        confBuilder.show()
    }

    //Display a custom dialog to let user edit the activity details
    // the dialog was designed in the dialog_edit_activity.xml
    private fun displayActivityEditDialog(){
        val editDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_activity,null)
        fireStore.collection(Constants.USERS).document(FireStoreClass().getCurrentUserID())
            .collection(Constants.ACTIVITIES).document(activityName).get()
            .addOnSuccessListener{ document ->
                Log.d("DB", "document received")
                val activity : ActivityClass? = document.toObject(ActivityClass::class.java)
                if (activity != null) {
                    editDialogView.et_description.setText(activity.description)
                    editDialogView.et_dueDate.setText(activity.dueDate)
                    val editDialogBuilder =AlertDialog.Builder(this).setView(editDialogView)
                    val editDialog = editDialogBuilder.show()
                    editDialogView.bt_submit.setOnClickListener {
                        val description = editDialogView.et_description.text.toString()
                        val dueDate = editDialogView.et_dueDate.text.toString()
                        fireStore.collection(Constants.USERS)
                            .document(FireStoreClass().getCurrentUserID())
                            .collection(Constants.ACTIVITIES).document(activityName).update(
                                mapOf(
                                    "description" to description,
                                    "dueDate" to dueDate
                                )
                            )
                        val intent = Intent(this, ActivitiesActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                        Toast.makeText(this, "Activity details changed", Toast.LENGTH_SHORT).show()
                        editDialog.dismiss()
                    }
                    editDialogView.bt_cancel.setOnClickListener {
                        editDialog.cancel()
                    }
                }
            }.addOnFailureListener { exception ->
                Log.d("DB", "Error getting the document: ", exception)
            }
    }

    //display the total time spent in a ctivity
    @SuppressLint("SetTextI18n")
    fun displayProgress(){
        fireStore.collection(Constants.USERS).document(FireStoreClass().getCurrentUserID())
            .collection(Constants.ACTIVITIES).document(activityName).collection(Constants.RECORDS).get()
            .addOnSuccessListener{ documents ->
                Log.d("DB", "collection received")
                var totalTime = 0L
                for (document in documents){
                    val record : ActivityRecordClass = document.toObject(ActivityRecordClass::class.java)
                    totalTime += record.progress
                }
                tv_progress.text= "Total time spent: ${formatProgress(totalTime)}"
            }.addOnFailureListener { exception ->
                Log.d("DB", "Error getting the document: ", exception)
            }
    }

    //format the total progress from seconds to string in hh mm ss format
    private fun formatProgress(progressInSec : Long) : String{
        val hours = progressInSec / 3600
        val minutes = (progressInSec % 3600) / 60
        val seconds = progressInSec % 60
        return when {
            hours >0 -> {
                String.format("%02dh %02dm %02ds", hours, minutes, seconds)
            }
            minutes>0 -> {
                String.format("%02dm %02ds",minutes, seconds)
            }
            else -> {
                String.format("%02ds", seconds)
            }
        }
    }

}