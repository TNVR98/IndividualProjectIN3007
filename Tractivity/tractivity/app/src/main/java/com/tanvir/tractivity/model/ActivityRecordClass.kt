package com.tanvir.tractivity.model

import android.os.Parcel
import android.os.Parcelable
import java.text.SimpleDateFormat
import java.util.*

data class ActivityRecordClass (
        val progress : Long = 0,
        val date : String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
//        val time: Timestamp = Timestamp(System.currentTimeMillis())
        ) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString()!!) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(progress)
        parcel.writeString(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ActivityRecordClass> {
        override fun createFromParcel(parcel: Parcel): ActivityRecordClass {
            return ActivityRecordClass(parcel)
        }

        override fun newArray(size: Int): Array<ActivityRecordClass?> {
            return arrayOfNulls(size)
        }
    }
}