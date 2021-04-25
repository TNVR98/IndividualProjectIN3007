package com.tanvir.tractivity.model

import android.os.Parcel
import android.os.Parcelable

data class UserClass (
    val id: String = "",
    val name : String="",
    val email :String ="",
    val score : Long = 0

        ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeLong(score)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserClass> {
        override fun createFromParcel(parcel: Parcel): UserClass {
            return UserClass(parcel)
        }

        override fun newArray(size: Int): Array<UserClass?> {
            return arrayOfNulls(size)
        }
    }
}