package com.example.tree.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class User (
    var id: String,
    var username: String,
    var fullName: String,
    var password: String,
    var phoneNumber: String,
    var email: String,
    var avatar: String,
    var active: Boolean,
    var role: String,
    var storeId: String,
    var createdAt: Date = Date(),
    var updatedAt: Date = Date(),
    var announcedMessage: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readBoolean(),
        parcel.readString()!!,
        parcel.readString()!!,
        Date(parcel.readLong()),
        Date(parcel.readLong()),
        parcel.readString()!!,
    )

    constructor() : this("", "", "", "", "", "", "", false, "", "", Date(), Date(), "")

    companion object : Parceler<User> {

        override fun User.write(parcel: Parcel, flags: Int) {
            parcel.writeString(id)
            parcel.writeString(username)
            parcel.writeString(fullName)
            parcel.writeString(password)
            parcel.writeString(phoneNumber)
            parcel.writeString(email)
            parcel.writeString(avatar)
            parcel.writeBoolean(active)
            parcel.writeString(role)
            parcel.writeString(storeId)
            parcel.writeLong(createdAt.time)
            parcel.writeLong(updatedAt.time)
            parcel.writeString(announcedMessage)
        }

        override fun create(parcel: Parcel): User {
            return User(parcel)
        }

    }
}