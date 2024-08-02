package com.example.tree.tips.models

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Tip(
    var approvalStatus: Int = 0,
    var content: String = "",
    @ServerTimestamp val createdAt: Date? = null,
    @ServerTimestamp var updatedAt: Date? = null,
    var imageList: MutableList<String> = mutableListOf(""),
    var shortDescription: String = "",
    var title: String = "",
    var userId: String = "",
    var checkContent: String = "",
    var vote_count: Int = 0,
    var id: String = "",
) : Parcelable {
    override fun toString(): String {
        return "Tip(approvalStatus=$approvalStatus, content='$content', createdAt=$createdAt, updatedAt=$updatedAt, imageList=$imageList, shortDescription='$shortDescription', title='$title', userId='$userId', vote=$vote_count, id='$id')"
    }
}

@Parcelize
data class Author(
    val userId: String = "",
    val fullName: String = "",
    val writerName: String = "",
    val writerAvatar: String = ""
) : Parcelable {
    override fun toString(): String {
        return "Author(userId='$userId', fullName='$fullName', writerName='$writerName')"
    }
}

data class Vote(
    val userId: String = "",
    val upvote: Boolean = false,
    @ServerTimestamp val updatedAt: Date? = null,
) {
    override fun toString(): String {
        val isUpvote = upvote
        return "Vote(userId='$userId', ${if (isUpvote !== null && isUpvote) "Upvote" else "Downvote"}"
    }
}

data class Comment(
    val userId: String = "",
    val content: String = "",
    @ServerTimestamp val createdAt: Date? = null,
) {
    @Exclude var fullName : String = ""
    @Exclude var avatar : String = ""
    override fun toString(): String {
        return "Comment(userId='$userId', content='$content', createdAt=$createdAt)"
    }
}