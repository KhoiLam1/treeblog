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

    suspend fun getUser(userId: String): User? {
        val snapshot = usersCollection.document(userId).get().await()
        return snapshot.toObject(User::class.java)
    }

    suspend fun getWriterId(userId: String): String {
        val snapshot = usersCollection.document(userId).get().await()
        return snapshot.getString("writerId") ?:""
    }

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

    suspend fun updateUser(userId: String, user: User) {
        usersCollection.document(userId).set(user).await()
    }

    suspend fun updateAvatar(userId: String, avatarUrl: String) {
        usersCollection.document(userId).update("avatar", avatarUrl).await()
    }

    suspend fun uploadAvatar(userId: String, avatarUri: Uri): String? {
        return try {
            val storageRef = FirebaseStorage.getInstance().reference.child("avatars/$userId.jpg")
            val uploadTask = storageRef.putFile(avatarUri)
            val snapshot = uploadTask.await()
            val downloadUrl = snapshot.metadata?.reference?.downloadUrl?.await()
            downloadUrl?.toString()?.also { avatarUrl ->
                saveAvatar(userId, avatarUrl)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
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

    suspend fun getUserByWriterId(writerId: String): User? {
        return withContext(Dispatchers.IO) {
            val snapshot = usersCollection
                .whereEqualTo("writerId", writerId)
                .limit(1)
                .get()
                .await()

            if (snapshot.documents.isNotEmpty()) {
                snapshot.documents.first().toObject(User::class.java)
            } else {
                null
            }
        }
    }
}