package com.outwear.app.presentation.ui.screens.profile.util

/**
 * Profile screen specific data models.
 */
data class ProfileEditForm(
    val displayName: String = "",
    val bio: String = "",
    val location: String = "",
    val nameError: String? = null
) {
    val isValid: Boolean get() = displayName.trim().length >= 2 && nameError == null
}

data class ProfileStats(
    val reviewCount: Int,
    val wishlistCount: Int,
    val totalHelpfulVotes: Int,
    val averageRatingGiven: Float
)

data class ProfileTab(
    val label: String,
    val badgeCount: Int = 0
) {
    companion object {
        fun defaults(reviewCount: Int, wishlistCount: Int) = listOf(
            ProfileTab("Reviews", reviewCount),
            ProfileTab("Wishlist", wishlistCount)
        )
    }
}
