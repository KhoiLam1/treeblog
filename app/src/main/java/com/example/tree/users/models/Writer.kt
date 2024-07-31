package com.example.tree.users.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Writer(
    var id: String = "",
    val writerName: String = "",
    var writerAvatar: String = "",
    val writerEmail: String = "",
    var active: Boolean = true,
    val createdAt: Date = Date(),
    var updatedAt: Date = Date()
) : Parcelable {
    override fun toString(): String {
        return "Writer(id=$id, writerName=$writerName)"
    }
}
