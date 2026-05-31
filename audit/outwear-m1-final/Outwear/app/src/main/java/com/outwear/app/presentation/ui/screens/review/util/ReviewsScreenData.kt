package com.outwear.app.presentation.ui.screens.review.util

/**
 * Reviews screen specific data models.
 */
data class ReviewSortOption(
    val label: String,
    val key: String
) {
    companion object {
        val all = listOf(
            ReviewSortOption("Newest", "newest"),
            ReviewSortOption("Highest Rated", "highest"),
            ReviewSortOption("Most Helpful", "helpful")
        )
    }
}

data class ReviewFilterState(
    val sortKey: String = "newest",
    val starFilter: Int? = null,
    val weatherFilter: String? = null,
    val verifiedOnly: Boolean = false
) {
    val hasActiveFilters: Boolean
        get() = starFilter != null || weatherFilter != null || verifiedOnly || sortKey != "newest"
}

data class ReviewStats(
    val totalCount: Int,
    val averageRating: Float,
    val verifiedCount: Int,
    val helpfulTotal: Int
)
