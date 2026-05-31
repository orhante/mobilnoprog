package com.outwear.app.model

/**
 * HardcodedData - Static reference data used before Room database is seeded.
 * This data is used for initial UI previews and fallback states.
 * Milestone 2: Required hardcoded data file per assignment specification.
 */
object HardcodedData {

    val categories = listOf(
        "Jacket", "Parka", "Fleece", "Rain Jacket",
        "Down Jacket", "Hoodie", "Coat", "Vest"
    )

    val brands = listOf(
        "Arc'teryx", "Patagonia", "The North Face",
        "Columbia", "Marmot", "Canada Goose", "Fjällräven", "Uniqlo"
    )

    val priceRanges = listOf("Budget", "Mid-range", "Premium", "Luxury")

    val weatherConditions = listOf(
        "Rain", "Snow", "Wind", "Cold",
        "All-season", "Extreme Cold", "Light Rain"
    )

    val sortOptions = listOf("Newest", "Highest Rated", "Most Helpful")

    val ratingLabels = mapOf(
        1 to "Poor",
        2 to "Fair",
        3 to "Good",
        4 to "Very Good",
        5 to "Excellent"
    )

    // Sample item for previews / loading placeholders
    data class SampleItem(
        val name: String,
        val brand: String,
        val category: String,
        val priceRange: String,
        val imageUrl: String
    )

    val sampleItems = listOf(
        SampleItem("Beta AR Jacket", "Arc'teryx", "Jacket", "Luxury",
            "https://images.unsplash.com/photo-1544441893-675973e31985?w=800"),
        SampleItem("Nano Puff Jacket", "Patagonia", "Jacket", "Premium",
            "https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=800"),
        SampleItem("McMurdo Parka III", "The North Face", "Parka", "Premium",
            "https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=800")
    )
}
