package com.example.tree.users.repositories

import com.example.tree.users.models.Store
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class StoreRepository {
    private val db = FirebaseFirestore.getInstance()
    private val storeCollection = db.collection("stores")

    suspend fun createNewStore(store: Store, userId: String) {
        val documentReference = storeCollection.add(store).await()
        val storeId = documentReference.id
        store.id = storeId

        val userDocumentReference = db.collection("users").document(userId)
        userDocumentReference.update("storeId", storeId).await()
    }
}