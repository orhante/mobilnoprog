package com.outwear.app.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.outwear.app.model.repository.mappers.Review
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReviewCard(
    review: Review,
    onHelpfulClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val authorName = review.authorName.ifBlank { "User ${review.userId}" }
                    UserAvatar(name = authorName, size = 32.dp)
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = authorName,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (review.isVerifiedPurchase) {
                                Spacer(Modifier.width(4.dp))
                                Icon(
                                    Icons.Filled.Verified,
                                    contentDescription = "Verified",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Text(
                            text = dateFormat.format(Date(review.createdAt)),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                StarRating(rating = review.rating, showValue = false, starSize = 14.dp)
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = review.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = review.body,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = if (compact) 3 else Int.MAX_VALUE,
                overflow = if (compact) TextOverflow.Ellipsis else TextOverflow.Clip
            )

            if (!compact) {
                if (review.pros.isNotEmpty() || review.cons.isNotEmpty()) {
                    Spacer(Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        if (review.pros.isNotEmpty()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Pros",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                review.pros.forEach { pro ->
                                    Text("+ $pro", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                        if (review.cons.isNotEmpty()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Cons",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold
                                )
                                review.cons.forEach { con ->
                                    Text("− $con", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (review.weatherCondition.isNotBlank()) {
                        WeatherChip(condition = review.weatherCondition)
                    }
                    if (onHelpfulClick != null) {
                        TextButton(onClick = onHelpfulClick) {
                            Icon(
                                Icons.Outlined.ThumbUp,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "Helpful (${review.helpfulCount})",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }
    }
}
