package com.example.tree.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Store (
    var id: String,
    var storeName: String,
    var storeLocation: String,
    var storeEmail: String,
    var storePhoneNumber: String,
    var storeAvatar: String = "",
    var createdAt: Date = Date(),
    var updatedAt: Date = Date(),
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        Date(parcel.readLong()),
        Date(parcel.readLong()),
    )

    constructor() : this("", "", "", "", "", "", Date(), Date())

    companion object : Parceler<Store> {

        override fun Store.write(parcel: Parcel, flags: Int) {
            parcel.writeString(id)
            parcel.writeString(storeName)
            parcel.writeString(storeLocation)
            parcel.writeString(storeEmail)
            parcel.writeString(storePhoneNumber)
            parcel.writeString(storeAvatar)
            parcel.writeLong(createdAt.time)
            parcel.writeLong(updatedAt.time)
        }

        override fun create(parcel: Parcel): Store {
            return Store(parcel)
        }
    }
}