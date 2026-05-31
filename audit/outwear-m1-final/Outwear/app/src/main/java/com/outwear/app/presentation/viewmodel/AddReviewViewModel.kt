package com.outwear.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.outwear.app.model.repository.ReviewRepository
import com.outwear.app.model.repository.UserRepository
import com.outwear.app.model.repository.mappers.Review
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AddReviewUiState {
    object Init : AddReviewUiState()
    object Loading : AddReviewUiState()
    object Success : AddReviewUiState()
    data class Error(val message: String) : AddReviewUiState()
}

data class ReviewFormState(
    val rating: Float = 0f,
    val title: String = "",
    val body: String = "",
    val pros: String = "",
    val cons: String = "",
    val weatherCondition: String = "",
    val fitRating: Int = 0,
    val warmthRating: Int = 0,
    val durabilityRating: Int = 0,
    val valueRating: Int = 0,
    val titleError: String? = null,
    val bodyError: String? = null,
    val ratingError: String? = null
)

@HiltViewModel
class AddReviewViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddReviewUiState>(AddReviewUiState.Init)
    val uiState: StateFlow<AddReviewUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(ReviewFormState())
    val formState: StateFlow<ReviewFormState> = _formState.asStateFlow()

    // Derived: form is valid
    val isFormValid: StateFlow<Boolean> = _formState.map { form ->
        form.rating > 0f &&
        form.title.trim().length >= 5 &&
        form.body.trim().length >= 20 &&
        form.fitRating > 0 &&
        form.warmthRating > 0 &&
        form.durabilityRating > 0 &&
        form.valueRating > 0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Derived: character counts
    val titleCharCount: StateFlow<Int> = _formState.map { it.title.length }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val bodyCharCount: StateFlow<Int> = _formState.map { it.body.length }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun updateRating(rating: Float) {
        _formState.update { it.copy(rating = rating, ratingError = null) }
    }

    fun updateTitle(title: String) {
        val error = when {
            title.trim().length < 5 && title.isNotBlank() -> "Title must be at least 5 characters"
            else -> null
        }
        _formState.update { it.copy(title = title.take(100), titleError = error) }
    }

    fun updateBody(body: String) {
        val error = when {
            body.trim().length < 20 && body.isNotBlank() -> "Review must be at least 20 characters"
            else -> null
        }
        _formState.update { it.copy(body = body.take(2000), bodyError = error) }
    }

    fun updatePros(pros: String) = _formState.update { it.copy(pros = pros) }
    fun updateCons(cons: String) = _formState.update { it.copy(cons = cons) }
    fun updateWeatherCondition(condition: String) = _formState.update { it.copy(weatherCondition = condition) }
    fun updateFitRating(rating: Int) = _formState.update { it.copy(fitRating = rating) }
    fun updateWarmthRating(rating: Int) = _formState.update { it.copy(warmthRating = rating) }
    fun updateDurabilityRating(rating: Int) = _formState.update { it.copy(durabilityRating = rating) }
    fun updateValueRating(rating: Int) = _formState.update { it.copy(valueRating = rating) }

    fun submitReview(itemId: Long) {
        val form = _formState.value

        // Validate
        var hasError = false
        var updatedForm = form

        if (form.rating == 0f) {
            updatedForm = updatedForm.copy(ratingError = "Please select a rating")
            hasError = true
        }
        if (form.title.trim().length < 5) {
            updatedForm = updatedForm.copy(titleError = "Title must be at least 5 characters")
            hasError = true
        }
        if (form.body.trim().length < 20) {
            updatedForm = updatedForm.copy(bodyError = "Review must be at least 20 characters")
            hasError = true
        }

        if (hasError) {
            _formState.value = updatedForm
            return
        }

        viewModelScope.launch {
            _uiState.value = AddReviewUiState.Loading
            try {
                val review = Review(
                    id = 0,
                    clothingItemId = itemId,
                    userId = 1L,
                    rating = form.rating,
                    title = form.title.trim(),
                    body = form.body.trim(),
                    pros = form.pros.split(",").map { it.trim() }.filter { it.isNotBlank() },
                    cons = form.cons.split(",").map { it.trim() }.filter { it.isNotBlank() },
                    weatherCondition = form.weatherCondition,
                    fitRating = form.fitRating,
                    warmthRating = form.warmthRating,
                    durabilityRating = form.durabilityRating,
                    valueRating = form.valueRating,
                    helpfulCount = 0,
                    createdAt = System.currentTimeMillis(),
                    isVerifiedPurchase = false
                )
                reviewRepository.addReview(review)
                _uiState.value = AddReviewUiState.Success
            } catch (e: Exception) {
                _uiState.value = AddReviewUiState.Error(e.message ?: "Failed to submit review")
            }
        }
    }
}
