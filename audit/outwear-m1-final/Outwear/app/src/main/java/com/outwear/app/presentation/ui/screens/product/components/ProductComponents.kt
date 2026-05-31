package com.outwear.app.presentation.ui.screens.product.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.outwear.app.presentation.ui.components.RatingBar
import com.outwear.app.presentation.ui.components.StarRating

@Composable
fun RatingsSummarySection(
    averageRating: Float,
    reviewCount: Int,
    ratingBreakdown: Map<Int, Int>,
    avgSubRatings: Map<String, Float>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = "Rating Summary",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Big average number
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(90.dp)
            ) {
                Text(
                    text = String.format("%.1f", averageRating),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                StarRating(rating = averageRating, showValue = false, starSize = 14.dp)
                Text(
                    text = "$reviewCount reviews",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.width(16.dp))

            // Star breakdown bars
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                (5 downTo 1).forEach { star ->
                    val count = ratingBreakdown[star] ?: 0
                    val fraction = if (reviewCount > 0) count.toFloat() / reviewCount else 0f
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "$star",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.width(12.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        LinearProgressIndicator(
                            progress = { fraction },
                            modifier = Modifier.weight(1f).height(6.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "$count",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.width(20.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        if (avgSubRatings.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Attribute Ratings",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            avgSubRatings.forEach { (label, value) ->
                RatingBar(label = label, value = value)
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}
