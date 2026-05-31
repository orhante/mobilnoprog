package com.outwear.app.presentation.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
import com.outwear.app.presentation.ui.screens.profile.components.*
import com.outwear.app.presentation.viewmodel.ProfileUiState
import com.outwear.app.presentation.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onReviewClick: (Long) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val totalHelpfulVotes by viewModel.totalHelpfulVotes.collectAsStateWithLifecycle()
    val averageRatingGiven by viewModel.averageRatingGiven.collectAsStateWithLifecycle()

    ProfileScreenStateless(
        uiState = uiState,
        totalHelpfulVotes = totalHelpfulVotes,
        averageRatingGiven = averageRatingGiven,
        onReviewClick = onReviewClick,
        onEditToggle = viewModel::setEditing,
        onSaveProfile = viewModel::saveProfile
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenStateless(
    uiState: ProfileUiState,
    totalHelpfulVotes: Int,
    averageRatingGiven: Float,
    onReviewClick: (Long) -> Unit,
    onEditToggle: (Boolean) -> Unit,
    onSaveProfile: (String, String, String) -> Unit
) {
    when (uiState) {
        is ProfileUiState.Init, is ProfileUiState.Loading -> LoadingScreen()
        is ProfileUiState.Error -> Box(Modifier.fillMaxSize(), Alignment.Center) {
            Text("Error: ${uiState.message}")
        }
        is ProfileUiState.Success -> {
            val user = uiState.user

            // Edit dialog
            if (uiState.isEditing) {
                EditProfileDialog(
                    currentName = user.displayName,
                    currentBio = user.bio,
                    currentLocation = user.location,
                    onSave = onSaveProfile,
                    onDismiss = { onEditToggle(false) }
                )
            }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Profile") },
                        actions = {
                            IconButton(onClick = { onEditToggle(true) }) {
                                Icon(Icons.Filled.Edit, "Edit Profile")
                            }
                        }
                    )
                }
            ) { padding ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    // Profile header
                    item {
                        ProfileHeader(
                            user = user,
                            totalHelpfulVotes = totalHelpfulVotes,
                            averageRatingGiven = averageRatingGiven
                        )
                    }

                    // Stats row
                    item {
                        ProfileStatsRow(
                            reviewCount = user.reviewCount,
                            wishlistCount = uiState.wishlistItems.size,
                            helpfulVotes = totalHelpfulVotes
                        )
                    }

                    // Wishlist
                    if (uiState.wishlistItems.isNotEmpty()) {
                        item {
                            SectionHeader(title = "Wishlist (${uiState.wishlistItems.size})")
                        }
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(uiState.wishlistItems, key = { it.id }) { item ->
                                    ClothingItemCard(
                                        item = item,
                                        onClick = { onReviewClick(item.id) }
                                    )
                                }
                            }
                        }
                    }

                    // Reviews
                    item {
                        SectionHeader(title = "My Reviews (${uiState.reviews.size})")
                    }

                    if (uiState.reviews.isEmpty()) {
                        item {
                            EmptyState(
                                title = "No reviews yet",
                                subtitle = "Start reviewing your outerwear!"
                            )
                        }
                    } else {
                        items(uiState.reviews, key = { it.id }) { review ->
                            ReviewCard(
                                review = review,
                                compact = true,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
