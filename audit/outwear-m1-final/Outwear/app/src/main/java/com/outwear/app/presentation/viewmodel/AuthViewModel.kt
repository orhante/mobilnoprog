package com.outwear.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.outwear.app.model.repository.remote.AuthRepository
import com.outwear.app.model.repository.remote.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiState {
    /** Initial state — Firebase auth listener not yet fired. Show a loading splash. */
    object Idle : AuthUiState()
    /** A sign-in or sign-up operation is in progress. */
    object Loading : AuthUiState()
    /** User is signed in with a valid Firebase session (persists across restarts). */
    data class Authenticated(val user: FirebaseUser) : AuthUiState()
    /** No active session — show Sign In / Sign Up screen. */
    object Unauthenticated : AuthUiState()
    /** An error occurred during sign-in or sign-up. */
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authState: StateFlow<AuthUiState> = _authState.asStateFlow()

    init {
        // Firebase AuthStateListener fires immediately with the current user (null if not signed in).
        // This is what provides persistent login across app restarts.
        viewModelScope.launch {
            authRepository.authStateFlow().collect { user ->
                _authState.value = if (user != null) {
                    AuthUiState.Authenticated(user)
                } else {
                    AuthUiState.Unauthenticated
                }
            }
        }
    }

    fun signUp(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthUiState.Error("Email and password cannot be empty")
            return
        }
        if (password.length < 6) {
            _authState.value = AuthUiState.Error("Password must be at least 6 characters")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthUiState.Loading
            when (val result = authRepository.signUp(email.trim(), password)) {
                is NetworkResult.Success -> _authState.value = AuthUiState.Authenticated(result.data)
                is NetworkResult.Error   -> _authState.value = AuthUiState.Error(result.message)
                else -> Unit
            }
        }
    }

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthUiState.Error("Email and password cannot be empty")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthUiState.Loading
            when (val result = authRepository.signIn(email.trim(), password)) {
                is NetworkResult.Success -> _authState.value = AuthUiState.Authenticated(result.data)
                is NetworkResult.Error   -> _authState.value = AuthUiState.Error(result.message)
                else -> Unit
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
        // authStateFlow emits null → _authState becomes Unauthenticated automatically
    }

    fun clearError() {
        if (_authState.value is AuthUiState.Error) {
            _authState.value = AuthUiState.Unauthenticated
        }
    }
}
