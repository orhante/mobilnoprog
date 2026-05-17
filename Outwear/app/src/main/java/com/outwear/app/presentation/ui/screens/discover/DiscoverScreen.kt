package com.outwear.app.presentation.ui.screens.discover

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.outwear.app.presentation.ui.components.*
import com.outwear.app.presentation.ui.screens.discover.components.FilterBottomSheet
import com.outwear.app.presentation.viewmodel.DiscoverUiState
import com.outwear.app.presentation.viewmodel.DiscoverViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    onItemClick: (Long) -> Unit,
    viewModel: DiscoverViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isSearchActive by viewModel.isSearchActive.collectAsStateWithLifecycle()
    val hasActiveFilter by viewModel.hasActiveFilter.collectAsStateWithLifecycle()

    DiscoverScreenStateless(
        uiState = uiState,
        isSearchActive = isSearchActive,
        hasActiveFilter = hasActiveFilter,
        onItemClick = onItemClick,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onCategorySelected = viewModel::onCategorySelected,
        onClearSearch = viewModel::clearSearch,
        onClearFilters = viewModel::clearFilters
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreenStateless(
    uiState: DiscoverUiState,
    isSearchActive: Boolean,
    hasActiveFilter: Boolean,
    onItemClick: (Long) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelected: (String?) -> Unit,
    onClearSearch: () -> Unit,
    onClearFilters: () -> Unit
) {
    var showFilterSheet by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val showScrollTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 3 }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Discover", style = MaterialTheme.typography.headlineSmall) })
        },
        floatingActionButton = {
            if (showScrollTop) {
                FloatingActionButton(
                    onClick = { coroutineScope.launch { listState.animateScrollToItem(0) } },
                    modifier = Modifier.size(44.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(Icons.Filled.KeyboardArrowUp, "Scroll to top")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(bottom = 80.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                // Search bar
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = if (uiState is DiscoverUiState.Success) uiState.searchQuery else "",
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Search jackets, brands…") },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                        trailingIcon = {
                            if (isSearchActive) {
                                IconButton(onClick = onClearSearch) {
                                    Icon(Icons.Filled.Clear, "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(
                            Icons.Filled.FilterList,
                            contentDescription = "Filter",
                            tint = if (hasActiveFilter) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            if (uiState is DiscoverUiState.Success) {
                // Category chips
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            CategoryChip(
                                label = "All",
                                selected = uiState.selectedCategory == null,
                                onClick = { onCategorySelected(null) }
                            )
                        }
                        items(uiState.categories) { cat ->
                            CategoryChip(
                                label = cat,
                                selected = uiState.selectedCategory == cat,
                                onClick = { onCategorySelected(cat) }
                            )
                        }
                    }
                }

                item { Spacer(Modifier.height(8.dp)) }

                item {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${uiState.totalCount} items",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (hasActiveFilter || isSearchActive) {
                            Spacer(Modifier.width(8.dp))
                            TextButton(onClick = onClearFilters) {
                                Text("Clear all", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }

                if (uiState.items.isEmpty()) {
                    item {
                        EmptyState(
                            title = "No items found",
                            subtitle = "Try a different search or category"
                        )
                    }
                } else {
                    items(uiState.items, key = { it.id }) { item ->
                        ClothingItemRow(
                            item = item,
                            onClick = { onItemClick(item.id) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            if (uiState is DiscoverUiState.Loading) {
                item { LoadingScreen(Modifier.height(300.dp)) }
            }
        }

        if (showFilterSheet && uiState is DiscoverUiState.Success) {
            FilterBottomSheet(
                categories = uiState.categories,
                selectedCategory = uiState.selectedCategory,
                onCategorySelected = onCategorySelected,
                onDismiss = { showFilterSheet = false }
            )
        }
    }
}
