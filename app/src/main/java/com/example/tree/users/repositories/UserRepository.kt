package com.example.tree.users.repositories

import android.net.Uri
import android.util.Log
import com.example.tree.users.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepository(private val db: FirebaseFirestore) {
    private val usersCollection = db.collection("users")

    fun getUserWithCallback(userId: String, callback: (User?) -> Unit) {
        Log.d("UserRepository", "Getting user with ID: $userId")
        usersCollection.document(userId).get()
            .addOnSuccessListener { snapshot ->
                callback(snapshot.toObject(User::class.java))
            }
            .addOnFailureListener { exception ->
                callback(null)
            }
    }


    suspend fun updateAvatar(userId: String, avatarUrl: String) {
        usersCollection.document(userId).update("avatar", avatarUrl).await()
    }


    private suspend fun saveAvatar(userId: String, avatarUrl: String) {
        usersCollection.document(userId).update("avatar", avatarUrl).await()
    }

    suspend fun updateToStore(userId: String, writerId: String) {
        usersCollection.document(userId).update(
            mapOf(
                "writerId" to writerId,
                "role" to "writer"
            )
        ).await()
    }

}