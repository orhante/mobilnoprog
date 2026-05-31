package com.outwear.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.outwear.app.model.repository.ClothingRepository
import com.outwear.app.model.repository.mappers.ClothingItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DiscoverUiState {
    object Init : DiscoverUiState()
    object Loading : DiscoverUiState()
    data class Success(
        val items: List<ClothingItem>,
        val categories: List<String>,
        val selectedCategory: String?,
        val searchQuery: String,
        val totalCount: Int
    ) : DiscoverUiState()
    data class Error(val message: String) : DiscoverUiState()
}

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val clothingRepository: ClothingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DiscoverUiState>(DiscoverUiState.Init)
    val uiState: StateFlow<DiscoverUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow<String?>(null)

    // Derived state: filtered items count
    val filteredCount: StateFlow<Int> = _uiState.map { state ->
        (state as? DiscoverUiState.Success)?.totalCount ?: 0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Derived state: is search active
    val isSearchActive: StateFlow<Boolean> = _searchQuery.map { it.isNotBlank() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Derived state: has active filter
    val hasActiveFilter: StateFlow<Boolean> = _selectedCategory.map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        observeItems()
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeItems() {
        viewModelScope.launch {
            _uiState.value = DiscoverUiState.Loading
            combine(
                _searchQuery.debounce(300),
                _selectedCategory,
                clothingRepository.getAllCategories()
            ) { query, category, categories ->
                Triple(query, category, categories)
            }.flatMapLatest { (query, category, categories) ->
                val itemsFlow = when {
                    query.isNotBlank() -> clothingRepository.searchItems(query)
                    category != null -> clothingRepository.getItemsByCategory(category)
                    else -> clothingRepository.getAllItems()
                }
                itemsFlow.map { items -> Triple(items, categories, Pair(query, category)) }
            }.catch { e ->
                _uiState.value = DiscoverUiState.Error(e.message ?: "Search failed")
            }.collect { (items, categories, filters) ->
                val (query, category) = filters
                _uiState.value = DiscoverUiState.Success(
                    items = items,
                    categories = categories,
                    selectedCategory = category,
                    searchQuery = query,
                    totalCount = items.size
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: String?) {
        _selectedCategory.value = category
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun clearFilters() {
        _selectedCategory.value = null
        _searchQuery.value = ""
    }
}
