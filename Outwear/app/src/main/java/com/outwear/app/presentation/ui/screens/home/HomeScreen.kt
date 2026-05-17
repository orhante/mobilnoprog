package com.outwear.app.presentation.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.outwear.app.presentation.ui.components.*
import com.outwear.app.presentation.ui.screens.home.components.*
import com.outwear.app.presentation.viewmodel.HomeUiState
import com.outwear.app.presentation.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onItemClick: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreenStateless(
        uiState = uiState,
        onItemClick = onItemClick,
        onRefresh = viewModel::refresh
    )
}

@Composable
fun HomeScreenStateless(
    uiState: HomeUiState,
    onItemClick: (Long) -> Unit,
    onRefresh: () -> Unit
) {
    when (uiState) {
        is HomeUiState.Init, is HomeUiState.Loading -> LoadingScreen()
        is HomeUiState.Error -> {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("Error: ${(uiState as HomeUiState.Error).message}")
                Button(onClick = onRefresh) { Text("Retry") }
            }
        }
        is HomeUiState.Success -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    HomeBanner()
                }

                item {
                    SectionHeader(
                        title = "Featured Outerwear",
                        actionLabel = "See all"
                    )
                }

                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.featuredItems, key = { it.id }) { item ->
                            ClothingItemCard(
                                item = item,
                                onClick = { onItemClick(item.id) }
                            )
                        }
                    }
                }

                item {
                    SectionHeader(title = "Top Rated")
                }

                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.topRatedItems, key = { it.id }) { item ->
                            TopRatedItemChip(item = item, onClick = { onItemClick(item.id) })
                        }
                    }
                }

                item {
                    SectionHeader(title = "Browse by Category")
                }

                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.categories) { category ->
                            CategoryPillCard(category = category)
                        }
                    }
                }

                item {
                    SectionHeader(title = "Recent Reviews")
                }

                if (uiState.recentReviews.isEmpty()) {
                    item {
                        EmptyState(
                            title = "No reviews yet",
                            subtitle = "Be the first to review an item!"
                        )
                    }
                } else {
                    items(uiState.recentReviews.take(5), key = { it.id }) { review ->
                        RecentReviewItem(
                            review = review,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
