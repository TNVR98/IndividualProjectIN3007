package com.tanvir.tractivity

import android.app.Activity
import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.util.*

class FireStoreClass {
     private val fireStore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()


    fun registerUserOnDB (user:User){
        fireStore.collection(Constants.USERS).document(getCurrentUserID())
            .set(user, SetOptions.merge()).addOnSuccessListener {
                Log.d("DDDBB", "Document saved")

            }
            .addOnFailureListener{ e->
                Log.w("DDDBB", "Error adding document")
            }

    }

    fun getCurrentUserID() : String  {
        var currentUser = auth.currentUser
        var currentUserID= ""
        if(currentUser != null){
            currentUserID =currentUser.uid
        }
        return currentUserID
    }

    fun saveActivityOnDB(activity:ActivityClass, record: ActivityRecordClass) {
        fireStore.collection(Constants.USERS)
                .document(getCurrentUserID())
                .collection(Constants.ACTIVITIES).document(activity.name).set(activity, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("DDDBB", "Activity saved")
                    saveRecordOnDB(record,activity.name)


                }
                .addOnFailureListener{ e ->
                    Log.e("DDDBB", "activity not saved")
                }
    }

    fun saveRecordOnDB(record:ActivityRecordClass,activityName :String){
        fireStore.collection(Constants.USERS)
                .document(getCurrentUserID())
                .collection(Constants.ACTIVITIES)
                .document(activityName).collection(Constants.RECORDS)
                .add(record).addOnSuccessListener {
                    Log.d("DDDBB", "record saved")
                }
                .addOnFailureListener{ e ->
                    Log.e("DDDBB", "record not saved")
                }


    }



//    fun getUserFromDB(activity:UserProfileActivity){
//
//        fireStore.collection(Constants.USERS).document(getCurrentUserID()).get().addOnSuccessListener {document ->
//            Log.d(TAG, "Document received")
//            val loggedUser: User? = document.toObject(User::class.java)
//            if (loggedUser != null) {
//                activity.setUserData(loggedUser)
//                }
//
//            }
//            .addOnFailureListener{
//                    exception ->
//                Log.d(TAG, "get failed with ", exception)
//            }
////
//    }

//
//    fun getUserFromDB() {
//        //lateinit var loggedUser:User
//        fireStore.collection(Constants.USERS)
//            .document(getCurrentUserID()).get()
//            .addOnSuccessListener {documentSnapshot ->
//                val user = documentSnapshot.toObject<User>()!!
//        }
//    }

//    fun getUserFromDB() :User {
//        lateinit var loggedUser: User
//        fireStore.collection(Constants.USERS)
//            .document(getCurrentUserID()).get()
//            .addOnSuccessListener { documentSnapshot ->
//                loggedUser = documentSnapshot.toObject<User>()!!
//
//            }
//        return loggedUser
//    }

//        fun getUserFromDB(activity:UserProfileActivity){
//
//        fireStore.collection(Constants.USERS).document(getCurrentUserID()).get().addOnSuccessListener {document ->
//            Log.d(TAG, "Document received")
//            val loggedUser: User? = document.toObject(User::class.java)
//            if (loggedUser != null) {
//                activity.loggedUser = loggedUser
//                }
//
//            }
//            .addOnFailureListener{
//                    exception ->
//                Log.d(TAG, "get failed with ", exception)
//            }
////
//    }

}