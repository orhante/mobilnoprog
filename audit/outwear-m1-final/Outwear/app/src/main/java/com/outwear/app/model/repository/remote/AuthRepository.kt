package com.outwear.app.model.repository.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface AuthRepository {
    val currentUser: FirebaseUser?
    fun authStateFlow(): Flow<FirebaseUser?>
    suspend fun signUp(email: String, password: String): NetworkResult<FirebaseUser>
    suspend fun signIn(email: String, password: String): NetworkResult<FirebaseUser>
    fun signOut()
}

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    // Emits the current user and every subsequent auth-state change.
    // The UI observes this to decide which screen to show.
    override fun authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun signUp(email: String, password: String): NetworkResult<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: return NetworkResult.Error("Sign-up succeeded but user is null")
            NetworkResult.Success(user)
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Sign-up failed")
        }
    }

    override suspend fun signIn(email: String, password: String): NetworkResult<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: return NetworkResult.Error("Sign-in succeeded but user is null")
            NetworkResult.Success(user)
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Sign-in failed")
        }
    }

    override fun signOut() {
        auth.signOut()
    }
}
