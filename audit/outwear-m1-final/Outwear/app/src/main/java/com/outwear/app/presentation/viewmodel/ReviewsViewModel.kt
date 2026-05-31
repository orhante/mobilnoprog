package com.outwear.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.outwear.app.model.repository.ReviewRepository
import com.outwear.app.model.repository.mappers.Review
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ReviewsUiState {
    object Init : ReviewsUiState()
    object Loading : ReviewsUiState()
    data class Success(
        val reviews: List<Review>,
        val filteredReviews: List<Review>,
        val sortOption: SortOption,
        val filterRating: Int?
    ) : ReviewsUiState()
    data class Error(val message: String) : ReviewsUiState()
}

enum class SortOption(val label: String) {
    NEWEST("Newest"), HIGHEST_RATED("Highest Rated"), MOST_HELPFUL("Most Helpful")
}

@HiltViewModel
class ReviewsViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReviewsUiState>(ReviewsUiState.Init)
    val uiState: StateFlow<ReviewsUiState> = _uiState.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.NEWEST)
    private val _filterRating = MutableStateFlow<Int?>(null)

    // Derived: has filters applied
    val hasFilters: StateFlow<Boolean> = combine(_sortOption, _filterRating) { sort, rating ->
        sort != SortOption.NEWEST || rating != null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun loadReviews(itemId: Long) {
        viewModelScope.launch {
            _uiState.value = ReviewsUiState.Loading
            combine(
                reviewRepository.getReviewsForItem(itemId),
                _sortOption,
                _filterRating
            ) { reviews, sort, ratingFilter ->
                val filtered = if (ratingFilter != null) {
                    reviews.filter { it.rating.toInt() == ratingFilter }
                } else reviews

                val sorted = when (sort) {
                    SortOption.NEWEST -> filtered.sortedByDescending { it.createdAt }
                    SortOption.HIGHEST_RATED -> filtered.sortedByDescending { it.rating }
                    SortOption.MOST_HELPFUL -> filtered.sortedByDescending { it.helpfulCount }
                }

                ReviewsUiState.Success(
                    reviews = reviews,
                    filteredReviews = sorted,
                    sortOption = sort,
                    filterRating = ratingFilter
                )
            }.catch { e ->
                _uiState.value = ReviewsUiState.Error(e.message ?: "Failed to load reviews")
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun setSortOption(option: SortOption) {
        _sortOption.value = option
    }

    fun setRatingFilter(rating: Int?) {
        _filterRating.value = rating
    }

    fun clearFilters() {
        _sortOption.value = SortOption.NEWEST
        _filterRating.value = null
    }

    fun markHelpful(reviewId: Long) {
        viewModelScope.launch {
            reviewRepository.markHelpful(reviewId)
        }
    }
}
