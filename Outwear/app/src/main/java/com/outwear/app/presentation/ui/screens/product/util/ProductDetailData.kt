package com.outwear.app.presentation.ui.screens.product.util

/**
 * Product detail screen specific data models.
 */
data class RatingBreakdown(
    val average: Float,
    val totalCount: Int,
    val starCounts: Map<Int, Int>  // star (1-5) -> count
) {
    fun percentageFor(stars: Int): Float {
        if (totalCount == 0) return 0f
        return ((starCounts[stars] ?: 0).toFloat() / totalCount)
    }
}

data class SubRatingDisplay(
    val label: String,
    val value: Float,
    val icon: String
) {
    companion object {
        fun defaults() = listOf(
            SubRatingDisplay("Fit", 0f, "📐"),
            SubRatingDisplay("Warmth", 0f, "🌡️"),
            SubRatingDisplay("Durability", 0f, "🔨"),
            SubRatingDisplay("Value", 0f, "💰")
        )
    }
}

data class ProductAction(
    val label: String,
    val isEnabled: Boolean = true
)
