package com.example.tree.tips.view_models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tree.tips.models.Author
import com.example.tree.tips.models.Tip
import com.example.tree.tips.models.Vote
import com.example.tree.users.models.Writer
import com.example.tree.users.models.User
import com.example.tree.utils.AuthHandler
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class TipsViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    val tipList = MutableLiveData<List<Tip>>()
    val topTipList = MutableLiveData<List<Tip>>()
    private var tipListDocuments: QuerySnapshot? = null
    private val collectionRef = firestore.collection("Tip")
    val sortDirection = MutableLiveData(SORT_BY_NEWEST)
    val user = MutableLiveData<User?>()

    init {
        loadUser()
    }

    companion object {
        const val SORT_BY_NEWEST = 1
        const val SORT_BY_VOTE = 0
        const val SORT_BY_OLDEST = -1
    }

    private fun loadUser() {
        val currentUser = AuthHandler.firebaseAuth.currentUser
        currentUser?.let {
            firestore.collection("users").document(it.uid)
                .get()
                .addOnSuccessListener { document ->
                    user.value = document.toObject(User::class.java)
                }
                .addOnFailureListener {
                    Log.e("TipsViewModel", "Error fetching user", it)
                }
        }
    }

    fun queryAllTips(direction: Int) {
        Log.d("TipsViewModel", "Getting tips, direction: $direction")
        val collectionApprovedRef = collectionRef.whereEqualTo("approvalStatus", 1)
        fun querySuccessListener(documents: QuerySnapshot) {
            tipListDocuments = documents
            tipList.value = documents.toObjects(Tip::class.java)
            Log.d("TipsViewModel", "Done getting tips")
        }
        fun queryFailListener(err: Exception) {
            Log.d("TipsViewModel", "Error getting documents: ", err)
        }
        when (direction) {
            SORT_BY_NEWEST -> collectionApprovedRef
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    querySuccessListener(documents)
                }
                .addOnFailureListener {
                    queryFailListener(it)
                }
            SORT_BY_VOTE -> collectionApprovedRef
                .orderBy("vote_count", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    querySuccessListener(documents)
                }
                .addOnFailureListener {
                    queryFailListener(it)
                }
            SORT_BY_OLDEST -> collectionApprovedRef
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    querySuccessListener(documents)
                }
                .addOnFailureListener {
                    queryFailListener(it)
                }
        }
    }

    fun queryTopTips() {
        collectionRef
            .whereEqualTo("approvalStatus", 1)
            .orderBy("vote_count", Query.Direction.DESCENDING)
            .limit(5)
            .get()
            .addOnSuccessListener { documents ->
                topTipList.value = documents.toObjects(Tip::class.java)
                Log.d("TipsViewModel", "Done getting top tips: " + topTipList.value?.size)
            }
            .addOnFailureListener {
                Log.d("TipsViewModel", "Error getting documents: ", it)
            }
    }

    val authorLiveData = MutableLiveData<Author?>()
    fun getAuthor(userId: String) {
        val userRef = Firebase.firestore.collection("users").document(userId)

        userRef.get()
            .addOnSuccessListener { document ->
                val user = document.toObject<User>()
                val fullname = user?.fullName ?: ""
                val writerId = user?.writerId

                if (!writerId.isNullOrEmpty()) {
                    val writerRef = Firebase.firestore.collection("writers").document(writerId)
                    writerRef.get()
                        .addOnSuccessListener { writerDocument ->
                            val writer = writerDocument.toObject<Writer>()
                            val writerName = writer?.writerName ?: ""
                            val writerAvatar = writer?.writerAvatar ?: ""
                            // Update authorLiveData with full user and writer details
                            authorLiveData.value = Author(userId, fullname, writerName, writerAvatar)
                        }
                        .addOnFailureListener { e ->
                            Log.w("TipsViewModel", "Error getting writer data", e)
                            // Set authorLiveData with only user details if writer data fails
                            authorLiveData.value = Author(userId, fullname, "", "")
                        }
                } else {
                    // Set authorLiveData with only user details if no writerId
                    authorLiveData.value = Author(userId, fullname, "", "")
                }
            }
            .addOnFailureListener { e ->
                Log.w("TipsViewModel", "Error getting user data", e)
                authorLiveData.value = null
            }
    }

    fun castVote(tip: Tip, isUpvote: Boolean) {
        val voteRef = collectionRef.document(tip.id).collection("votes")
        val docRef = collectionRef.document(tip.id)

        // Check if the current user is authenticated and tip.id is not null
        val currentUser = AuthHandler.firebaseAuth.currentUser
        if (currentUser == null || tip.id.isNullOrEmpty()) {
            Log.w("TipsViewModel", "User not authenticated or tip ID is null")
            return
        }

        docRef.update("vote_count", FieldValue.increment(if (isUpvote) 1 else -1))
            .addOnSuccessListener {
                Log.d("TipsViewModel", "[Vote] vote_count updated:" + if (isUpvote) "+1" else "-1")
            }
            .addOnFailureListener { e ->
                Log.w("TipsViewModel", "[Vote] Error updating vote_count ", e)
            }

        val vote = Vote(
            userId = currentUser.uid,
            upvote = isUpvote,
        )
        voteRef.document(currentUser.uid + "_vote").set(vote)
            .addOnSuccessListener {
                Log.d("TipsViewModel", "[Vote] Vote added to database: $vote")
            }
            .addOnFailureListener { e ->
                Log.w("TipsViewModel", "[Vote] Error adding vote to database: $vote", e)
            }
    }

    fun unVoteTip(tip: Tip, upvote: Boolean) {
        val currentUser = AuthHandler.firebaseAuth.currentUser
        if (currentUser == null) {
            Log.w("TipsViewModel", "User not authenticated")
            return
        }

        val docRef = collectionRef.document(tip.id)
        val voteRef = docRef.collection("votes").document(currentUser.uid + '_' + upvote.toString())

        voteRef.get().addOnSuccessListener {
            voteRef.delete()
                .addOnSuccessListener {
                    Log.d("TipsViewModel", "[Unvote] Vote removed from database")
                }
                .addOnFailureListener { e ->
                    Log.w("TipsViewModel", "[Unvote] Error removing vote from database", e)
                }
            if (upvote) {
                docRef.update("vote_count", FieldValue.increment(-1))
                    .addOnSuccessListener {
                        Log.d("TipsViewModel", "[Unvote] vote_count updated")
                    }
                    .addOnFailureListener { e ->
                        Log.w("TipsViewModel", "[Unvote] Error updating vote_count ", e)
                    }
            } else {
                docRef.update("vote_count", FieldValue.increment(1))
                    .addOnSuccessListener {
                        Log.d("TipsViewModel", "[Unvote] vote_count updated")
                    }
                    .addOnFailureListener { e ->
                        Log.w("TipsViewModel", "[Unvote] Error updating vote_count ", e)
                    }
            }
        }.addOnFailureListener { e ->
            Log.w("TipsViewModel", "[Unvote] Error getting vote", e)
        }
    }

    fun getIsUpvoted(tipId: String): MutableLiveData<Boolean?> {
        val isUpvoted = MutableLiveData<Boolean?>()
        val voteRef = collectionRef.document(tipId).collection("votes")
        val currentUser = AuthHandler.firebaseAuth.currentUser
        if (currentUser == null) {
            isUpvoted.value = null
            return isUpvoted
        }
        voteRef
            .whereEqualTo("userId", currentUser.uid)
            .limit(1)
            .get()
            .addOnSuccessListener { document ->
                if (document.isEmpty) {
                    isUpvoted.value = null
                    return@addOnSuccessListener
                }
                isUpvoted.value = document.toObjects(Vote::class.java)[0].upvote
            }
            .addOnFailureListener {
                isUpvoted.value = null
            }
        return isUpvoted
    }
}