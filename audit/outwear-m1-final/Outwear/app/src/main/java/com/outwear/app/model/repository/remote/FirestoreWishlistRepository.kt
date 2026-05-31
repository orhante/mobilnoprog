package com.outwear.app.model.repository.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Firestore collection: "wishlists"
 * Document ID = uid of the signed-in user
 * Fields:
 *   itemIds: List<String>   (clothing item IDs stored as strings)
 *   updatedAt: Long
 */
interface FirestoreWishlistRepository {
    /** Realtime stream of the current user's wishlisted item IDs */
    fun watchWishlist(): Flow<List<String>>

    suspend fun addToCloudWishlist(itemId: String): NetworkResult<Unit>
    suspend fun removeFromCloudWishlist(itemId: String): NetworkResult<Unit>
}

class FirestoreWishlistRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : FirestoreWishlistRepository {

    private val collection = firestore.collection("wishlists")

    private val uid: String? get() = auth.currentUser?.uid

    override fun watchWishlist(): Flow<List<String>> {
        val currentUid = uid ?: return flowOf(emptyList())
        return callbackFlow {
            val listener = collection.document(currentUid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        // Emit empty list on error instead of crashing
                        trySend(emptyList())
                        return@addSnapshotListener
                    }
                    @Suppress("UNCHECKED_CAST")
                    val ids = snapshot?.get("itemIds") as? List<String> ?: emptyList()
                    trySend(ids)
                }
            awaitClose { listener.remove() }
        }
    }

    override suspend fun addToCloudWishlist(itemId: String): NetworkResult<Unit> {
        val currentUid = uid ?: return NetworkResult.Error("Not authenticated")
        return try {
            val ref = collection.document(currentUid)
            firestore.runTransaction { tx ->
                val snapshot = tx.get(ref)
                @Suppress("UNCHECKED_CAST")
                val existing = (snapshot.get("itemIds") as? List<String>)?.toMutableList() ?: mutableListOf()
                if (!existing.contains(itemId)) {
                    existing.add(itemId)
                    tx.set(ref, mapOf("itemIds" to existing, "updatedAt" to System.currentTimeMillis()), SetOptions.merge())
                }
            }.await()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Failed to add to cloud wishlist")
        }
    }

    override suspend fun removeFromCloudWishlist(itemId: String): NetworkResult<Unit> {
        val currentUid = uid ?: return NetworkResult.Error("Not authenticated")
        return try {
            val ref = collection.document(currentUid)
            firestore.runTransaction { tx ->
                val snapshot = tx.get(ref)
                @Suppress("UNCHECKED_CAST")
                val existing = (snapshot.get("itemIds") as? List<String>)?.toMutableList() ?: mutableListOf()
                existing.remove(itemId)
                tx.set(ref, mapOf("itemIds" to existing, "updatedAt" to System.currentTimeMillis()), SetOptions.merge())
            }.await()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Failed to remove from cloud wishlist")
        }
    }
}
