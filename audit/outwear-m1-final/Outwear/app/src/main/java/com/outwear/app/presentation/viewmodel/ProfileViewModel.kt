package com.outwear.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.outwear.app.model.repository.ReviewRepository
import com.outwear.app.model.repository.UserRepository
import com.outwear.app.model.repository.WishlistRepository
import com.outwear.app.model.repository.ClothingRepository
import com.outwear.app.model.repository.mappers.ClothingItem
import com.outwear.app.model.repository.mappers.Review
import com.outwear.app.model.repository.mappers.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProfileUiState {
    object Init : ProfileUiState()
    object Loading : ProfileUiState()
    data class Success(
        val user: User,
        val reviews: List<Review>,
        val wishlistItems: List<ClothingItem>,
        val isEditing: Boolean = false
    ) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val reviewRepository: ReviewRepository,
    private val wishlistRepository: WishlistRepository,
    private val clothingRepository: ClothingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Init)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    // Derived: total helpful votes received
    val totalHelpfulVotes: StateFlow<Int> = _uiState.map { state ->
        (state as? ProfileUiState.Success)?.reviews?.sumOf { it.helpfulCount } ?: 0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Derived: average rating given
    val averageRatingGiven: StateFlow<Float> = _uiState.map { state ->
        val reviews = (state as? ProfileUiState.Success)?.reviews ?: emptyList()
        if (reviews.isEmpty()) 0f else reviews.map { it.rating }.average().toFloat()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            userRepository.getCurrentUser()
                .filterNotNull()
                .flatMapLatest { user ->
                    combine(
                        reviewRepository.getReviewsByUser(user.id),
                        wishlistRepository.getWishlistIds(user.id)
                    ) { reviews, wishlistIds ->
                        Pair(reviews, wishlistIds)
                    }.flatMapLatest { (reviews, ids) ->
                        clothingRepository.getAllItems().map { allItems ->
                            val wishlistItems = allItems.filter { it.id in ids }
                            ProfileUiState.Success(
                                user = user,
                                reviews = reviews,
                                wishlistItems = wishlistItems
                            )
                        }
                    }.map { it as ProfileUiState }
                }.catch { e ->
                    _uiState.value = ProfileUiState.Error(e.message ?: "Failed to load profile")
                }.collect { state ->
                    _uiState.value = state
                }
        }
    }

    fun setEditing(editing: Boolean) {
        val current = _uiState.value as? ProfileUiState.Success ?: return
        _uiState.value = current.copy(isEditing = editing)
    }

    fun saveProfile(name: String, bio: String, location: String) {
        val current = _uiState.value as? ProfileUiState.Success ?: return
        if (name.isBlank()) return
        viewModelScope.launch {
            userRepository.updateProfile(current.user.id, name, bio, location)
            _uiState.value = current.copy(
                user = current.user.copy(displayName = name, bio = bio, location = location),
                isEditing = false
            )
        }
    }
}
