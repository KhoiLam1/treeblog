package com.example.tree.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Writer(
    var id: String,
    var writerName: String,
    var writerEmail: String,
    var writerAvatar: String = "",
    var createdAt: Date = Date(),
    var updatedAt: Date = Date(),
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        Date(parcel.readLong()),
        Date(parcel.readLong()),
    )

    companion object : Parceler<Writer> {
        override fun Writer.write(parcel: Parcel, flags: Int) {
            parcel.writeString(id)
            parcel.writeString(writerName)
            parcel.writeString(writerEmail)
            parcel.writeString(writerAvatar)
            parcel.writeLong(createdAt.time)
            parcel.writeLong(updatedAt.time)
        }

        override fun create(parcel: Parcel): Writer {
            return Writer(
                id = parcel.readString()!!,
                writerName = parcel.readString()!!,
                writerEmail = parcel.readString()!!,
                writerAvatar = parcel.readString()!!,
                createdAt = Date(parcel.readLong()),
                updatedAt = Date(parcel.readLong())
            )
        }
    }
}
