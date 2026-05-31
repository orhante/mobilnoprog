package com.outwear.app.presentation.ui.screens.addreview.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.outwear.app.presentation.ui.components.InteractiveStarRating

@Composable
fun WeatherConditionSelector(
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            "Weather Condition Tested In",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        val rows = options.chunked(3)
        rows.forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowItems.forEach { option ->
                    FilterChip(
                        selected = selected == option,
                        onClick = { onSelected(if (selected == option) "" else option) },
                        label = { Text(option, style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(3 - rowItems.size) { Spacer(Modifier.weight(1f)) }
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
fun SubRatingsSection(
    fitRating: Int,
    warmthRating: Int,
    durabilityRating: Int,
    valueRating: Int,
    onFitChange: (Int) -> Unit,
    onWarmthChange: (Int) -> Unit,
    onDurabilityChange: (Int) -> Unit,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                "Attribute Ratings *",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "Rate each attribute from 1 (poor) to 5 (excellent)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            SubRatingRow("Fit & Sizing", fitRating, onFitChange)
            SubRatingRow("Warmth", warmthRating, onWarmthChange)
            SubRatingRow("Durability", durabilityRating, onDurabilityChange)
            SubRatingRow("Value for Money", valueRating, onValueChange)
        }
    }
}

@Composable
fun SubRatingRow(
    label: String,
    value: Int,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(110.dp)
        )
        InteractiveStarRating(
            rating = value,
            onRatingChange = onChange,
            starSize = 22.dp
        )
    }
}
