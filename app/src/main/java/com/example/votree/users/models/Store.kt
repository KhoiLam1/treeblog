package com.example.votree.users.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Store(
    var id: String = "",
    val storeName: String = "",
    var storeAvatar: String = "",
    val storeEmail: String = "",
    val storePhoneNumber: String = "",
    var active: Boolean = true,
    val createdAt: Date = Date(),
    var updatedAt: Date = Date()
) : Parcelable {
    override fun toString(): String {
        return "Writer(id=$id, storeName=$storeName)"
    }
}
