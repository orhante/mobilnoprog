package com.outwear.app.presentation.ui.screens.review

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.outwear.app.presentation.ui.components.*
import com.outwear.app.presentation.ui.screens.review.components.SortFilterRow
import com.outwear.app.presentation.viewmodel.ReviewsUiState
import com.outwear.app.presentation.viewmodel.ReviewsViewModel
import com.outwear.app.presentation.viewmodel.SortOption

@Composable
fun ReviewsScreen(
    itemId: Long,
    itemName: String,
    onNavigateBack: () -> Unit,
    onWriteReview: () -> Unit,
    viewModel: ReviewsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val hasFilters by viewModel.hasFilters.collectAsStateWithLifecycle()

    LaunchedEffect(itemId) { viewModel.loadReviews(itemId) }

    ReviewsScreenStateless(
        itemName = itemName,
        uiState = uiState,
        hasFilters = hasFilters,
        onNavigateBack = onNavigateBack,
        onWriteReview = onWriteReview,
        onSortChange = viewModel::setSortOption,
        onRatingFilter = viewModel::setRatingFilter,
        onClearFilters = viewModel::clearFilters,
        onMarkHelpful = viewModel::markHelpful
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewsScreenStateless(
    itemName: String,
    uiState: ReviewsUiState,
    hasFilters: Boolean,
    onNavigateBack: () -> Unit,
    onWriteReview: () -> Unit,
    onSortChange: (SortOption) -> Unit,
    onRatingFilter: (Int?) -> Unit,
    onClearFilters: () -> Unit,
    onMarkHelpful: (Long) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Reviews", style = MaterialTheme.typography.titleMedium)
                        Text(
                            itemName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onWriteReview) {
                Icon(Icons.Filled.Edit, "Write Review")
            }
        }
    ) { padding ->
        when (uiState) {
            is ReviewsUiState.Init, is ReviewsUiState.Loading ->
                LoadingScreen(Modifier.padding(padding))
            is ReviewsUiState.Error ->
                Box(
                    Modifier.padding(padding).fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { Text("Error: ${uiState.message}") }
            is ReviewsUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(bottom = 88.dp)
                ) {
                    item {
                        SortFilterRow(
                            currentSort = uiState.sortOption,
                            currentRatingFilter = uiState.filterRating,
                            hasFilters = hasFilters,
                            onSortChange = onSortChange,
                            onRatingFilter = onRatingFilter,
                            onClearFilters = onClearFilters
                        )
                    }

                    item {
                        Text(
                            text = "${uiState.filteredReviews.size} reviews",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }

                    if (uiState.filteredReviews.isEmpty()) {
                        item {
                            EmptyState(
                                title = "No reviews match your filters",
                                subtitle = "Try adjusting or clearing the filters"
                            )
                        }
                    } else {
                        items(uiState.filteredReviews, key = { it.id }) { review ->
                            ReviewCard(
                                review = review,
                                onHelpfulClick = { onMarkHelpful(review.id) },
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
