package com.example.tree.users.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class User(
    var id: String = "",
    var email: String = "",
    var username: String = "",
    var fullName: String = "",
    var password: String = "",
    var avatar: String = "",
    var phoneNumber: String = "",
    val role: String = "",
    val writerId: String = "",
    var active: Boolean = true,
    val createdAt: Date = Date(),
    var updatedAt: Date = Date()
) : Parcelable {
    override fun toString(): String {
        return "User(id=$id, email='$email', name='${fullName}')"
    }
}