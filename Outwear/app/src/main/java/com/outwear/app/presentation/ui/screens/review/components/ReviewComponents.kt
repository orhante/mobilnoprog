package com.outwear.app.presentation.ui.screens.review.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.outwear.app.presentation.viewmodel.SortOption

@Composable
fun SortFilterRow(
    currentSort: SortOption,
    currentRatingFilter: Int?,
    hasFilters: Boolean,
    onSortChange: (SortOption) -> Unit,
    onRatingFilter: (Int?) -> Unit,
    onClearFilters: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            "Sort by",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SortOption.entries.forEach { option ->
                FilterChip(
                    selected = currentSort == option,
                    onClick = { onSortChange(option) },
                    label = { Text(option.label, style = MaterialTheme.typography.labelSmall) }
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(
            "Filter by stars",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            (5 downTo 1).forEach { stars ->
                FilterChip(
                    selected = currentRatingFilter == stars,
                    onClick = {
                        onRatingFilter(if (currentRatingFilter == stars) null else stars)
                    },
                    label = {
                        Text(
                            "★".repeat(stars),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                )
            }
            if (hasFilters) {
                IconButton(onClick = onClearFilters, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Filled.Clear, "Clear filters", modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
