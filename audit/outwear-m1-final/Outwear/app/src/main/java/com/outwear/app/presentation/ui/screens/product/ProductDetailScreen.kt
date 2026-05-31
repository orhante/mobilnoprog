package com.outwear.app.presentation.ui.screens.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.outwear.app.presentation.ui.components.*
import com.outwear.app.presentation.ui.screens.product.components.*
import com.outwear.app.presentation.viewmodel.ProductDetailUiState
import com.outwear.app.presentation.viewmodel.ProductDetailViewModel

@Composable
fun ProductDetailScreen(
    itemId: Long,
    onNavigateBack: () -> Unit,
    onSeeAllReviews: (Long, String) -> Unit,
    onWriteReview: (Long, String) -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val avgSubRatings by viewModel.avgSubRatings.collectAsStateWithLifecycle()

    LaunchedEffect(itemId) { viewModel.loadItem(itemId) }

    ProductDetailStateless(
        uiState = uiState,
        avgSubRatings = avgSubRatings,
        onNavigateBack = onNavigateBack,
        onSeeAllReviews = onSeeAllReviews,
        onWriteReview = onWriteReview,
        onWishlistToggle = viewModel::toggleWishlist,
        onMarkHelpful = viewModel::markReviewHelpful
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailStateless(
    uiState: ProductDetailUiState,
    avgSubRatings: Map<String, Float>,
    onNavigateBack: () -> Unit,
    onSeeAllReviews: (Long, String) -> Unit,
    onWriteReview: (Long, String) -> Unit,
    onWishlistToggle: () -> Unit,
    onMarkHelpful: (Long) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (uiState is ProductDetailUiState.Success) {
                        Text(
                            uiState.item.name,
                            maxLines = 1,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (uiState is ProductDetailUiState.Success) {
                        IconButton(onClick = onWishlistToggle) {
                            Icon(
                                imageVector = if (uiState.isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Wishlist",
                                tint = if (uiState.isWishlisted) MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (uiState is ProductDetailUiState.Success) {
                BottomAppBar(containerColor = MaterialTheme.colorScheme.surface) {
                    Button(
                        onClick = { onWriteReview(uiState.item.id, uiState.item.name) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Write a Review")
                    }
                }
            }
        }
    ) { padding ->
        when (uiState) {
            is ProductDetailUiState.Init, is ProductDetailUiState.Loading ->
                LoadingScreen(Modifier.padding(padding))
            is ProductDetailUiState.Error ->
                Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${uiState.message}")
                }
            is ProductDetailUiState.Success -> {
                val item = uiState.item
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(item.imageUrl).crossfade(true).build(),
                            contentDescription = item.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                                .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                        )
                    }

                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.brand,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                PriceRangeChip(item.priceRange)
                            }

                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                StarRating(rating = item.averageRating)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "(${item.reviewCount} reviews)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text(item.category, style = MaterialTheme.typography.labelSmall) }
                                )
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text(item.material, style = MaterialTheme.typography.labelSmall) }
                                )
                            }

                            Spacer(Modifier.height(8.dp))
                            WeatherChip(item.weatherSuitability)

                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    item {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    }

                    item {
                        RatingsSummarySection(
                            averageRating = item.averageRating,
                            reviewCount = item.reviewCount,
                            ratingBreakdown = uiState.ratingBreakdown,
                            avgSubRatings = avgSubRatings
                        )
                    }

                    item {
                        SectionHeader(
                            title = "Reviews",
                            actionLabel = if (uiState.reviews.size > 3) "See all ${uiState.reviews.size}" else null,
                            onAction = { onSeeAllReviews(item.id, item.name) }
                        )
                    }

                    if (uiState.previewReviews.isEmpty()) {
                        item {
                            EmptyState(
                                title = "No reviews yet",
                                subtitle = "Be the first to review this item!"
                            )
                        }
                    } else {
                        items(uiState.previewReviews, key = { it.id }) { review ->
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
