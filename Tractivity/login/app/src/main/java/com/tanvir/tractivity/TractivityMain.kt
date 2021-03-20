package com.tanvir.tractivity

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.android.synthetic.main.activity_trmain.*
import kotlinx.android.synthetic.main.dialog_save.view.*

@Suppress("DEPRECATION")
class TractivityMain : AppCompatActivity() {
    private var pauseTime : Long = 0
    private var running : Boolean = false
    private var activitySubmited : Boolean = false
    var progress : String ?= null

    var activityName : String ?= null

    val activityList = ArrayList<String>()




    val CHANNEL_ID = "channel_id"
    val CHANNEL_NAME = "channel_name"
    val NOTIFICATION_ID = 1

//    fun openDialog() {
//       //val save_dialog: Save_Dialog  = Save_Dialog()
//        //save_dialog.show(supportFragmentManager,"Save Dialog")
//    }

// full screen by hiding status bar navigation bar
//    override fun onWindowFocusChanged(hasFocus: Boolean) {
//        super.onWindowFocusChanged(hasFocus)
//        if (hasFocus) hideSystemUI()
//    }
//
//    private fun hideSystemUI() {
//        // Enables regular immersive mode.
//        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
//        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                // Set the content to appear under the system bars so that the
//                // content doesn't resize when the system bars hide and show.
//                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                // Hide the nav bar and status bar
//                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_FULLSCREEN)
//    }
//
//    // Shows the system bars by removing all the flags
//// except for the ones that make the content appear under the system bars.
//    private fun showSystemUI() {
//        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
//    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //HIDE STATUS BAR
        // If the Android version is lower than Jellybean, use this call to hide
        // the status bar.
        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
            setContentView(R.layout.activity_trmain)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        activityList.add("Activity One")
        activityList.add("Project")
        activityList.add("Task three")


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
           val intentP = Intent(this, ProjectActivity::class.java)
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
            startActivity(intentP)
            }
        







        // start and pause the activity
        btn_start.setOnClickListener {
            NotificationManagerCompat.from(this).notify(NOTIFICATION_ID,buildNotification)
            startAndPauseActivity()


        }

        btn_stop.setOnClickListener{
            NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID)
            progress = c_chronometer.text.toString()
            c_chronometer.stop()
            saveDialogFunction()
            resetStopwatch()
            //tv_progress.text =  activityName
            //openDialog()
           // tv_progress.text=progress + activityName


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

    //Save activity dialog
    @SuppressLint("SetTextI18n")
    private fun saveDialogFunction() {

        val saveDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_save,null)
        saveDialogView.tv_description.text= "Following $progress is spent on:"
        val saveDialogbuilder = AlertDialog.Builder(this)
                .setView(saveDialogView)
        val saveActivityDialog = saveDialogbuilder.show()
        saveActivityDialog.setCancelable(false) // prevent user to close the dialog by clicking outside the dialog
        saveDialogView.bt_selectActivity.setOnClickListener {
            //val listItems = arrayOf("item 1","item2", "item3")
            val listItems:Array<String> = activityList.toTypedArray()

            val activitySelectBuilder = AlertDialog.Builder(this)
            activitySelectBuilder.setTitle("Choose activity")
            activitySelectBuilder.setSingleChoiceItems(listItems,-1){
                    dialogInterface: DialogInterface, i :Int ->
               // dialogView.tv_selectedActivity.text= listItems[i]
                //dialogView.et_activityName.hide()
                saveDialogView.et_activityName.setText(listItems[i])
                dialogInterface.dismiss()

            }

            activitySelectBuilder.setNeutralButton("cancel"){dialog:DialogInterface,which->
                dialog.cancel()
            }

            val activitySelectDialog = activitySelectBuilder.create()
            activitySelectDialog.show()

        }
        saveDialogView.bt_submit.setOnClickListener{
            saveActivityDialog.dismiss()
            activityName = saveDialogView.et_activityName.text.toString()
            tv_progress.text =  activityName
        }
        saveDialogView.bt_cancel.setOnClickListener{
            Toast.makeText(applicationContext, "Please save the Activity", Toast.LENGTH_LONG).show()
        }


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