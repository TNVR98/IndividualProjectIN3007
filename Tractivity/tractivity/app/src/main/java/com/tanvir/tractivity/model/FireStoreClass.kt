package com.tanvir.tractivity.model

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tanvir.tractivity.constants.Constants

/**
 * All the methods of storing the data on the firestore were implemented in this class
 * note: retrieving data methods were not implemented in this file
 * resources used: https://firebase.google.com/docs/auth/android/password-auth
 *                 https://firebase.google.com/docs/firestore/quickstart#android
 *                 https://firebase.google.com/docs/auth/android/manage-users
 *                 https://www.youtube.com/watch?v=dRYnm_k3w1w&t=269s
 *                 https://firebase.google.com/docs/firestore/manage-data/add-data
 */
class FireStoreClass {
    val fireStore = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    //store the user info on the firestore
    fun registerUserOnDB (user: UserClass){
        fireStore.collection(Constants.USERS).document(getCurrentUserID())
            .set(user, SetOptions.merge()).addOnSuccessListener {
                Log.d("DDDBB", "Document saved")

            }
            .addOnFailureListener{ e->
                Log.w("DDDBB", "Error adding document")
            }
    }

    // get the ID of currently logged in user from Authentication server
    fun getCurrentUserID() : String  {
        var currentUser = auth.currentUser
        var currentUserID= ""
        if(currentUser != null){
            currentUserID =currentUser.uid
        }
        return currentUserID
    }

    // this function takes a Activity object class and store it in the Firetore as a document in the Activity collection
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

    // store the activity record on the firestore
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