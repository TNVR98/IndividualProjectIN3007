package com.tanvir.tractivity.model

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tanvir.tractivity.Constants

class FireStoreClass {
    val fireStore = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    fun registerUserOnDB (user: UserClass){
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

    fun saveActivityOnDB(activity: ActivityClass) {
        fireStore.collection(Constants.USERS)
                .document(getCurrentUserID())
                .collection(Constants.ACTIVITIES).document(activity.name).set(activity, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("DDDBB", "Activity saved")
                }
                .addOnFailureListener{ e ->
                    Log.e("DDDBB", "activity not saved")
                }
    }

    fun saveRecordOnDB(record: ActivityRecordClass, activityName :String){
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
}