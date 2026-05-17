package com.outwear.app.presentation.ui.screens.discover.util

/**
 * Discover screen specific data models.
 */
data class SearchState(
    val query: String = "",
    val isActive: Boolean = false,
    val resultCount: Int = 0
)

data class FilterState(
    val selectedCategory: String? = null,
    val selectedPriceRange: String? = null,
    val minRating: Float = 0f,
    val hasActiveFilters: Boolean = false
) {
    fun cleared() = FilterState()
}

enum class DiscoverViewMode { LIST, GRID }

data class DiscoverDisplayOptions(
    val viewMode: DiscoverViewMode = DiscoverViewMode.LIST,
    val showRatings: Boolean = true,
    val showPriceRange: Boolean = true
)
