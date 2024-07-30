package com.example.tree.tips.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tree.tips.models.Comment
import com.example.tree.tips.models.Tip
import com.example.tree.users.models.User
import com.example.tree.utils.AuthHandler
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject

@Suppress("DEPRECATION")
class CommentViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val _commentList = MutableLiveData<List<Comment>>()
    val commentList: LiveData<List<Comment>> = _commentList

    fun castComment(tip: Tip, content: String) {
        val tipRef = firestore.collection("Tip")
        val commentRef = tipRef.document(tip.id).collection("comments")
        val comment = Comment(
            userId = AuthHandler.firebaseAuth.currentUser?.uid!!,
            content = content,
        )
        commentRef.add(comment)
            .addOnSuccessListener {
                Log.d("CommentViewModel", "[Comment] Comment added to database: $comment")
                queryComments(tip)
            }
            .addOnFailureListener { e ->
                Log.w("CommentViewModel", "[Comment] Error adding comment to database: $comment", e)
            }
    }

    fun queryComments(tip: Tip) {
        val tipsRef = firestore.collection("Tip")
        val commentRef = tipsRef.document(tip.id).collection("comments")
        val userRef = firestore.collection("users")

        commentRef
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(5)
            .get()
            .addOnSuccessListener { documents ->
                val queriedComments = documents.toObjects(Comment::class.java)
                var counter = 0
                for (i in queriedComments.indices) {
                    userRef.document(queriedComments[i].userId).get()
                        .addOnSuccessListener { document ->
                            counter++
                            queriedComments[i].fullName = document.toObject<User>()?.fullName ?: ""
                            queriedComments[i].avatar = document.toObject<User>()?.avatar ?: ""
                            Log.d("CommentViewModel", "Comment $i: ${queriedComments[i]}")
                            if (counter == queriedComments.size) {
                                Log.d("CommentViewModel", "Comment list updated")
                                _commentList.value = queriedComments
                            }
                        }
                        .addOnFailureListener {
                            Log.d("CommentViewModel", "Error getting author name and avatar: ", it)
                        }
                }
                Log.d("CommentViewModel", "Done getting comments")
            }
            .addOnFailureListener {
                Log.d("CommentViewModel", "Error getting comments: ", it)
            }
    }
}
