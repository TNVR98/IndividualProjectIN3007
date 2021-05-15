package com.tanvir.tractivity.activities

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.tanvir.tractivity.*
import com.tanvir.tractivity.model.ActivityClass
import com.tanvir.tractivity.model.ActivityRecordClass
import com.tanvir.tractivity.model.FireStoreClass
import com.tanvir.tractivity.model.UserClass
import kotlinx.android.synthetic.main.activity_stopwatch.*
import kotlinx.android.synthetic.main.activity_trmain.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.dialog_save.view.*

/**
 * this class contains the  of the functionality the home screen, Tractivity
 * The stop watch, app navigation and storing the activity data in the database was implemented here.
 */
@Suppress("DEPRECATION")
class TractivityMain : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var pauseTime : Long = 0
    private var running : Boolean = false
    private var activitySubmited : Boolean = false
    val firestore = Firebase.firestore
    var progress : String ?= null
    var activityName : String =""
    var isActivityStarted : Boolean =  false
    var isActivitySaved : Boolean = false
    var isActivitySelected: Boolean = false

    val CHANNEL_ID = "channel_id"
    val CHANNEL_NAME = "channel_name"
    val NOTIFICATION_ID = 1



    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trmain)

        //HIDE STATUS BAR
        hideStatusBar()

        //CUSTOM ACTION BAR
        setupActionBar()

        //NAVIGATION LISTENER
        navigation.setNavigationItemSelectedListener(this)


        val notificationIntent = Intent(this, TractivityMain::class.java)
        notificationIntent.setAction(Intent.ACTION_MAIN)
        val pendingIntent : PendingIntent = PendingIntent.getActivity(this,0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT)


        createNotificationChannel()

        // to build the notification panel
        val buildNotification =  NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle("Tractivity")
            .setContentText("is running")
            .setNotificationSilent()
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT).build()


        // start and pause the activity
        btn_start.setOnClickListener {
            isActivityStarted = true
            NotificationManagerCompat.from(this).notify(NOTIFICATION_ID,buildNotification)
            startAndPauseActivity()
        }
        //STOP STOPWATCH and call Save Activity Dialog
        btn_stop.setOnClickListener{
            if( isActivityStarted){
                NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID)
                progress = c_chronometer.text.toString()
                pauseStopWatch()
                saveDialogFunction()

            }else{
                Toast.makeText(this, "Activity not started", Toast.LENGTH_LONG).show()
            }
        }

        // Reset stopwatch and confirmation dialog
        btn_reset.setOnClickListener {
            if(isActivityStarted){
                pauseStopWatch()
                confirmationDialog()
            }

        }

        populateNavUsername()

    }


    // start and pause the activity function
    private fun startAndPauseActivity(){
        if (!running) {
            startStopWatch()
        } else {
            pauseStopWatch()

        }

    }

    // START THE STOPWATCH
    private fun startStopWatch(){
        iArrow.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotating_arrow))
        c_chronometer.base = SystemClock.elapsedRealtime() + pauseTime
        c_chronometer.start()
        running = true
        btn_start.text = "Pause"
    }

    // PAUSE THE STOPWATCH
    private fun pauseStopWatch(){
        iArrow.clearAnimation()
        pauseTime = c_chronometer.base - SystemClock.elapsedRealtime()
        c_chronometer.stop()
        running = false
        btn_start.text= "Resume"
    }



    //reset the stopwatch
    private fun resetStopwatch(){

        c_chronometer.base= SystemClock.elapsedRealtime()
        iArrow.clearAnimation()
        pauseTime = 0
        running=false
        btn_start.text="Start"
    }

    private fun confirmationDialog(){
        val confBuilder = AlertDialog.Builder(this)
        confBuilder.setTitle("Reset stopwatch?")
        confBuilder.setMessage("resetting the stopwatch will cancel the progress")
        confBuilder.setPositiveButton("RESET") { dialog: DialogInterface, i: Int ->
            resetStopwatch()
            isActivityStarted=false
            dialog.cancel()
        }
        confBuilder.setNegativeButton("Cancel") { dialog: DialogInterface, i: Int ->
            dialog.cancel()
        }
        confBuilder.show()
    }


    //populate username on navigation
    fun populateNavUsername(){
        FirebaseFirestore.getInstance().collection(Constants.USERS)
                .document(FireStoreClass().getCurrentUserID()).get()
                .addOnSuccessListener {documentSnapshot ->
                    val user = documentSnapshot.toObject<UserClass>()!!
                    val navHeader = navigation.getHeaderView(0)
                    val navUsername = navHeader.findViewById<TextView>(R.id.tv_username)
                    navUsername.text = user.name

                }
    }
    //prevent accidental exit
    override fun onBackPressed() {
        doubleBackExit()
    }


    //CUSTOM ACTION BAR
    private fun setupActionBar() {
        setSupportActionBar(toolbar_trmain)
        toolbar_trmain.setNavigationIcon(R.drawable.ic_menu)
        toolbar_trmain.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    //TOGGLE NAVIGATION DRAWER
    private fun toggleDrawer() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }

    // Intend to the selected activity screen from the navigation menu
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.main_pg -> {
                drawer_layout.closeDrawer(GravityCompat.START)
                return true
            }
            R.id.activities ->{
                val intent = Intent(this, ActivitiesActivity::class.java)
                startActivity(intent)
            }
            R.id.charts ->{
                val intent = Intent(this, ChartsActivity::class.java)
                startActivity(intent)
            }
            R.id.profile ->{
                val intent = Intent(this, UserProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.logout ->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


    //creating a notification channel
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel1 = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                //behaviour of the notification
                setSound(null,null)
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel1)
        }
    }

    //SAVE ACTIVITY CUSTOM DIALOG
    @SuppressLint("SetTextI18n")
    private fun saveDialogFunction() {

        val saveDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_save,null)
        saveDialogView.tv_description.text= "Following $progress is spent on:"
        val saveDialogbuilder = AlertDialog.Builder(this)
                .setView(saveDialogView)
        val saveActivityDialog = saveDialogbuilder.show()
        saveActivityDialog.setCancelable(false) // prevent user to close the dialog by clicking outside the dialog
        saveDialogView.bt_selectActivity.setOnClickListener {
           // ACTIVITY NAMES DIALOG populated with the activity lists retrieved from the DB
            firestore.collection(Constants.USERS).document(FireStoreClass().getCurrentUserID())
                    .collection(Constants.ACTIVITIES)
                    .get()
                    .addOnSuccessListener { activities ->
                        Log.d("DB", "All documents received")
                        val activityNameList = ArrayList<String>()
                        for (activity in activities) {
                            val activityClass: ActivityClass? = activity.toObject(ActivityClass::class.java)
                            if (activityClass != null){
                                activityNameList.add(activityClass.name)

                            }

                        }
                        val items:Array<String> = activityNameList.toTypedArray()
                        val selectActivityBuilder = AlertDialog.Builder(this)
                        selectActivityBuilder.setTitle("Choose activity")
                        selectActivityBuilder.setSingleChoiceItems(items,-1){
                            dialogInterface: DialogInterface, i :Int ->

                            saveDialogView.et_activityName.setText(items[i])
                            isActivitySelected = true
                            dialogInterface.dismiss()

                        }
                        selectActivityBuilder.setNeutralButton("cancel"){ dialog:DialogInterface, which->
                            dialog.cancel()
                        }
                        val activitySelectDialog = selectActivityBuilder.create()
                        activitySelectDialog.show()
                    }
                    .addOnFailureListener { exception ->
                        Log.d("DB", "Error getting documents: ", exception)
                    }

        }
        // storing the data on the firestore once the submit button in clicked
        saveDialogView.bt_submit.setOnClickListener{
            activityName = saveDialogView.et_activityName.text.toString()
            if (TextUtils.isEmpty(activityName)){
                Toast.makeText(applicationContext, "Enter or select Activity Name", Toast.LENGTH_SHORT).show()
            } else {
                val activity = ActivityClass(activityName)
                val record = ActivityRecordClass(parseProgress(progress!!))
                if(isActivitySelected){
                    FireStoreClass().saveRecordOnDB(record,activity.name)

                } else{
                    FireStoreClass().saveActivityOnDB(activity)
                    FireStoreClass().saveRecordOnDB(record,activity.name)
                }
                isActivitySelected = false
                isActivitySelected = false
                resetStopwatch()
                saveActivityDialog.dismiss()
            }

        }
        saveDialogView.bt_cancel.setOnClickListener{
            Toast.makeText(applicationContext, "Activity not saved", Toast.LENGTH_SHORT).show()
            saveActivityDialog.dismiss()
        }


    }


    //PARSE STRING PROGRESS TO LONG (mm:ss or hh:mm:ss)
    fun parseProgress (progress:String) : Long{
        var progressInSeconds :Long = 0
        val strArray : Array<String> = progress.split(":").toTypedArray()
        if(strArray.size<3){
            progressInSeconds += strArray[0].toLong()*60
            progressInSeconds += strArray[1].toLong()
            return progressInSeconds
        }
        progressInSeconds += strArray[0].toLong()*3600
        progressInSeconds += strArray[1].toLong()*60
        progressInSeconds += strArray[2].toLong()
        return progressInSeconds
    }

}












