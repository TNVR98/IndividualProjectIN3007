package com.tanvir.tractivity.model

import android.os.Parcel
import android.os.Parcelable
import java.text.SimpleDateFormat
import java.util.*

/**
 * A data class to hold the Activity details
 */
data class ActivityClass(
        val name: String = "",
        val date : String = SimpleDateFormat("dd/MM/yyyy",Locale.getDefault()).format(Date()),
        val description: String  = "",
        val dueDate : String = ""


        ) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(date)
        parcel.writeString(description)
        parcel.writeString(dueDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ActivityClass> {
        override fun createFromParcel(parcel: Parcel): ActivityClass {
            return ActivityClass(parcel)
        }

        override fun newArray(size: Int): Array<ActivityClass?> {
            return arrayOfNulls(size)
        }
    }

}