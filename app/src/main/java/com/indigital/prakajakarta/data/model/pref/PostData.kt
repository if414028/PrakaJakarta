package com.indigital.prakajakarta.data.model.pref

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

class PostData() : Parcelable {
    var houseOwner: String? = null
    var firstAnswer: String? = null
    var secondAnswer: String? = null
    var lat: Double = 0.0
    var lng: Double = 0.0
    var work: String? = null
    var address: String? = null
    var age: String? = null
    var sex: String? = null

    constructor(parcel: Parcel) : this() {
        houseOwner = parcel.readString()
        firstAnswer = parcel.readString()
        secondAnswer = parcel.readString()
        lat = parcel.readDouble()
        lng = parcel.readDouble()
        work = parcel.readString()
        address = parcel.readString()
        age = parcel.readString()
        sex = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(houseOwner)
        parcel.writeString(firstAnswer)
        parcel.writeString(secondAnswer)
        parcel.writeDouble(lat)
        parcel.writeDouble(lng)
        parcel.writeString(work)
        parcel.writeString(address)
        parcel.writeString(age)
        parcel.writeString(sex)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PostData> {
        override fun createFromParcel(parcel: Parcel): PostData {
            return PostData(parcel)
        }

        override fun newArray(size: Int): Array<PostData?> {
            return arrayOfNulls(size)
        }
    }
}