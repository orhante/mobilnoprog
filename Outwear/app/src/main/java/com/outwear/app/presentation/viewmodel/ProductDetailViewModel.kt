package com.outwear.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.outwear.app.model.repository.ClothingRepository
import com.outwear.app.model.repository.ReviewRepository
import com.outwear.app.model.repository.WishlistRepository
import com.outwear.app.model.repository.mappers.ClothingItem
import com.outwear.app.model.repository.mappers.Review
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProductDetailUiState {
    object Init : ProductDetailUiState()
    object Loading : ProductDetailUiState()
    data class Success(
        val item: ClothingItem,
        val reviews: List<Review>,
        val previewReviews: List<Review>,
        val isWishlisted: Boolean,
        val ratingBreakdown: Map<Int, Int>
    ) : ProductDetailUiState()
    data class Error(val message: String) : ProductDetailUiState()
}

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val clothingRepository: ClothingRepository,
    private val reviewRepository: ReviewRepository,
    private val wishlistRepository: WishlistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Init)
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    private var currentItemId: Long = -1

    // Derived: average sub-ratings
    val avgSubRatings: StateFlow<Map<String, Float>> = _uiState.map { state ->
        (state as? ProductDetailUiState.Success)?.reviews?.let { reviews ->
            if (reviews.isEmpty()) return@let emptyMap()
            mapOf(
                "Fit" to reviews.map { it.fitRating.toFloat() }.average().toFloat(),
                "Warmth" to reviews.map { it.warmthRating.toFloat() }.average().toFloat(),
                "Durability" to reviews.map { it.durabilityRating.toFloat() }.average().toFloat(),
                "Value" to reviews.map { it.valueRating.toFloat() }.average().toFloat()
            )
        } ?: emptyMap()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun loadItem(itemId: Long) {
        if (currentItemId == itemId) return
        currentItemId = itemId
        viewModelScope.launch {
            _uiState.value = ProductDetailUiState.Loading
            combine(
                clothingRepository.getItemById(itemId),
                reviewRepository.getReviewsForItem(itemId),
                wishlistRepository.isItemWishlisted(1L, itemId)
            ) { item, reviews, wishlisted ->
                if (item == null) {
                    ProductDetailUiState.Error("Item not found")
                } else {
                    val breakdown = (1..5).associateWith { star ->
                        reviews.count { r -> r.rating.toInt() == star }
                    }
                    ProductDetailUiState.Success(
                        item = item,
                        reviews = reviews,
                        previewReviews = reviews.take(3),
                        isWishlisted = wishlisted,
                        ratingBreakdown = breakdown
                    )
                }
            }.catch { e ->
                _uiState.value = ProductDetailUiState.Error(e.message ?: "Failed to load")
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun toggleWishlist() {
        val state = _uiState.value as? ProductDetailUiState.Success ?: return
        viewModelScope.launch {
            val newValue = !state.isWishlisted
            if (newValue) {
                wishlistRepository.addToWishlist(1L, currentItemId)
            } else {
                wishlistRepository.removeFromWishlist(1L, currentItemId)
            }
            clothingRepository.toggleWishlist(currentItemId, newValue)
        }
    }

    fun markReviewHelpful(reviewId: Long) {
        viewModelScope.launch {
            reviewRepository.markHelpful(reviewId)
        }
    }
}
