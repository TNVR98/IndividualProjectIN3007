package com.tanvir.tractivity

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
import kotlinx.android.synthetic.main.activity_stopwatch.*
import kotlinx.android.synthetic.main.activity_trmain.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.dialog_save.view.*

@Suppress("DEPRECATION")
class TractivityMain : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var pauseTime : Long = 0
    private var running : Boolean = false
    private var activitySubmited : Boolean = false
    val firestore = Firebase.firestore
    var progress : String ?= null
    var activityName : String =""
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


//        notificationIntent.setAction(Intent.ACTION_MAIN);
//        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


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



        button.setOnClickListener {
           val intentP = Intent(this, TaskActivity::class.java)
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
            startActivity(intentP)
            }



        // start and pause the activity
        btn_start.setOnClickListener {
            NotificationManagerCompat.from(this).notify(NOTIFICATION_ID,buildNotification)
            startAndPauseActivity()


        }
        //STOP AND RESET STOPWATCH
        btn_stop.setOnClickListener{
            NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID)
            progress = c_chronometer.text.toString()
            c_chronometer.stop()
            saveDialogFunction()
            resetStopwatch()

        }


        populateNavUsername()


    }

    //pupulate username on navigation
    fun populateNavUsername(){
        FirebaseFirestore.getInstance().collection(Constants.USERS)
                .document(FireStoreClass().getCurrentUserID()).get()
                .addOnSuccessListener {documentSnapshot ->
                    val user = documentSnapshot.toObject<User>()!!
                    val nav_header = navigation.getHeaderView(0)
                    val nav_username = nav_header.findViewById<TextView>(R.id.tv_username)
                    nav_username.text = user.name

                }
    }
    //prevent accidental exit
    override fun onBackPressed() {
        doubleBackExit()
    }


    //ACTION BAR
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


    // start and pause the activity function
   private fun startAndPauseActivity(){
        if (!running) {
            iArrow.startAnimation(AnimationUtils.loadAnimation(this,R.anim.rotating_arrow))
            c_chronometer.base = SystemClock.elapsedRealtime() + pauseTime
            c_chronometer.start()
            running = true
            btn_start.text = "Pause"
            // btn_start.setBackground(R.drawable.pause_button)
        } else {
            iArrow.clearAnimation()
            pauseTime = c_chronometer.base - SystemClock.elapsedRealtime()
            c_chronometer.stop()
            running = false
            btn_start.text= "Resume"
            //btn_start.setBackground(R.drawable.button_start)

        }

    }



    //reset the stopwatch
    private fun resetStopwatch(){

        c_chronometer.base= SystemClock.elapsedRealtime()
        iArrow.clearAnimation()
        pauseTime = 0
        running=false
        btn_start.text="Start"
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
           //SELECT ACTIVITY DIALOG
            //val listItems = arrayOf("item 1","item2", "item3")
            firestore.collection(Constants.USERS).document(FireStoreClass().getCurrentUserID())
                    .collection(Constants.ACTIVITIES)
                    .get()
                    .addOnSuccessListener { result ->
                        Log.d("DB", "All documents received")
                        val activityNameList = ArrayList<String>()
                        for (document in result) {
                            val activity: ActivityClass? = document.toObject(ActivityClass::class.java)
                            if (activity != null){
                                activityNameList.add(activity.name)

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
        saveDialogView.bt_submit.setOnClickListener{
            activityName = saveDialogView.et_activityName.text.toString()
            if (TextUtils.isEmpty(activityName)){
                Toast.makeText(applicationContext, "Enter or select Activity Name", Toast.LENGTH_SHORT).show()
            } else {
                val activity = ActivityClass(activityName)
                val record = ActivityRecordClass(parseProgress(progress!!))
                FireStoreClass().saveActivityOnDB(activity, record)
                saveActivityDialog.dismiss()
                isActivitySelected = false
            }

        }
        saveDialogView.bt_cancel.setOnClickListener{
            Toast.makeText(applicationContext, "Please save the Activity", Toast.LENGTH_LONG).show()
        }


    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.main_pg -> {
                drawer_layout.closeDrawer(GravityCompat.START)
                return true
            }
            R.id.activities ->{
                val intent = Intent(this, TaskActivity::class.java)
                startActivity(intent)
            }
            R.id.charts ->{
                Toast.makeText(this,"charts clicked",Toast.LENGTH_SHORT).show()
            }
            R.id.profile ->{
                val intent = Intent(this, UserProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.logout ->{
//                Toast.makeText(this,"logout clicked",Toast.LENGTH_SHORT).show()
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this,IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    //PARSE STRING PROGRESS TO INTEGER (mm:ss or hh:mm:ss)
    fun parseProgress (progress:String) : Int{
        var progressInSeconds :Int = 0
        val strArray : Array<String> = progress.split(":").toTypedArray()
        if(strArray.size<3){
            progressInSeconds += strArray[0].toInt()*60
            progressInSeconds += strArray[1].toInt()
            return progressInSeconds
        }
        progressInSeconds += strArray[0].toInt()*3600
        progressInSeconds += strArray[1].toInt()*60
        progressInSeconds += strArray[2].toInt()
        return progressInSeconds
    }








}














//        val saveDialog = Dialog(this)
//        /*Set the screen content from a layout resource.
//    The resource will be inflated, adding all top-level views to the screen.*/
//        saveDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        saveDialog.setContentView(R.layout.dialog_save)
//        tv_submit.setOnClickListener(View.OnClickListener {
//            var activityN : String = et_activityName.toString()
//           // activityName= et_activityName.text.toString()
//            tv_progress.setText(String.format(activityN))
//            saveDialog.dismiss() // Dialog will be dismissed
//        })
//        saveDialog.tv_cancel.setOnClickListener(View.OnClickListener {
//            Toast.makeText(applicationContext, "clicked cancel", Toast.LENGTH_LONG).show()
//            saveDialog.dismiss()
//        })
//        //Start the dialog and display it on screen.
//        saveDialog .show()

//*************************************
//        val builder = AlertDialog.Builder(this)
//        val inflater = layoutInflater
//        val dialogLayout = inflater.inflate(R.layout.dialog_save,null)
//
//        with(builder){
//            setPositiveButton("Submit"){dialog,which ->
//                tv_progress.text=et_activityName.text.toString()
//            }
//            setNegativeButton("cancel") { dialog, which ->
//                Toast.makeText(applicationContext, "clicked cancel", Toast.LENGTH_LONG).show()
//            }
//            setView(dialogLayout)
//            show()
//        }