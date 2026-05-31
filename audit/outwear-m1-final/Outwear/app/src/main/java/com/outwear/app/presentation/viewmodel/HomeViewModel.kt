package com.outwear.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.outwear.app.model.data.local.dao.ReviewDao
import com.outwear.app.model.data.local.util.DatabaseSeeder
import com.outwear.app.model.repository.*
import com.outwear.app.model.repository.mappers.ClothingItem
import com.outwear.app.model.repository.mappers.Review
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HomeUiState {
    object Init : HomeUiState()
    object Loading : HomeUiState()
    data class Success(
        val featuredItems: List<ClothingItem>,
        val topRatedItems: List<ClothingItem>,
        val recentReviews: List<Review>,
        val categories: List<String>
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val clothingRepository: ClothingRepository,
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository,
    private val brandRepository: BrandRepository,
    private val reviewDao: ReviewDao
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Init)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        seedAndLoad()
    }

    private fun seedAndLoad() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                userRepository.seedCurrentUserIfNeeded()
                brandRepository.seedIfEmpty()
                clothingRepository.seedIfEmpty()
                val existingReviewCount = reviewDao.getReviewCount()
                if (existingReviewCount == 0) {
                    reviewDao.insertReviews(DatabaseSeeder.getSeedReviews())
                }
                loadHomeData()
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            combine(
                clothingRepository.getAllItems(),
                clothingRepository.getTopRatedItems(5),
                reviewRepository.getRecentReviews(10),
                clothingRepository.getAllCategories()
            ) { all, top, reviews, categories ->
                HomeUiState.Success(
                    featuredItems = all.take(6),
                    topRatedItems = top,
                    recentReviews = reviews,
                    categories = categories
                )
            }.catch { e ->
                _uiState.value = HomeUiState.Error(e.message ?: "Failed to load data")
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun refresh() {
        seedAndLoad()
    }
}
