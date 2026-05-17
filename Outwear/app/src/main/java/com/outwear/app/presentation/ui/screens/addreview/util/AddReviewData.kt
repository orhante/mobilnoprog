package com.outwear.app.presentation.ui.screens.addreview.util

/**
 * Add review screen specific data models and validation constants.
 */
object ReviewValidation {
    const val TITLE_MIN_LENGTH = 5
    const val TITLE_MAX_LENGTH = 100
    const val BODY_MIN_LENGTH = 20
    const val BODY_MAX_LENGTH = 2000
    const val MIN_RATING = 1
    const val MAX_RATING = 5
}

data class SubRatingField(
    val key: String,
    val label: String,
    val hint: String,
    val value: Int = 0
) {
    val isValid: Boolean get() = value in 1..5

    companion object {
        fun defaults() = listOf(
            SubRatingField("fit", "Fit & Sizing", "How well does it fit?"),
            SubRatingField("warmth", "Warmth", "How warm does it keep you?"),
            SubRatingField("durability", "Durability", "How well is it built?"),
            SubRatingField("value", "Value for Money", "Worth the price?")
        )
    }
}

data class ReviewDraft(
    val itemId: Long,
    val rating: Float = 0f,
    val title: String = "",
    val body: String = "",
    val pros: String = "",
    val cons: String = "",
    val weatherCondition: String = ""
)
