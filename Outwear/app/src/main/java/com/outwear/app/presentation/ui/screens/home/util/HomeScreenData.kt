package com.outwear.app.presentation.ui.screens.home.util

/**
 * Home screen specific data models and constants.
 */
data class FeaturedSection(
    val title: String,
    val subtitle: String,
    val itemIds: List<Long> = emptyList()
)

data class CategoryDisplay(
    val name: String,
    val emoji: String,
    val itemCount: Int = 0
) {
    companion object {
        val emojiMap = mapOf(
            "Jacket" to "🧥", "Parka" to "🥼", "Fleece" to "🫧",
            "Rain Jacket" to "🌧️", "Hoodie" to "👕",
            "Down Jacket" to "❄️", "Coat" to "🧣", "Vest" to "🦺"
        )
        fun emojiFor(category: String) = emojiMap[category] ?: "👗"
    }
}

data class HomeSection(
    val type: HomeSectionType,
    val title: String,
    val isLoading: Boolean = false
)

enum class HomeSectionType {
    BANNER, FEATURED, TOP_RATED, CATEGORIES, RECENT_REVIEWS
}
