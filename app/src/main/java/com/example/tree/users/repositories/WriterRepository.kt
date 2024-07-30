package com.example.tree.users.repositories

import com.example.tree.users.models.Writer
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class WriterRepository {
    private val db = FirebaseFirestore.getInstance()
    private val writerCollection = db.collection("writers")

    suspend fun createNewWriter(writer: Writer, userId: String) {
        val documentReference = writerCollection.add(writer).await()
        val writerId = documentReference.id
        writer.id = writerId

        val userDocumentReference = db.collection("users").document(userId)
        userDocumentReference.update("writerId", writerId).await()
    }
}