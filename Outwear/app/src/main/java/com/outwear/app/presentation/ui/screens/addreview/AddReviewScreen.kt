package com.outwear.app.presentation.ui.screens.addreview

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.outwear.app.presentation.ui.components.InteractiveStarRating
import com.outwear.app.presentation.ui.components.LoadingScreen
import com.outwear.app.presentation.ui.screens.addreview.components.*
import com.outwear.app.presentation.viewmodel.AddReviewUiState
import com.outwear.app.presentation.viewmodel.AddReviewViewModel

@Composable
fun AddReviewScreen(
    itemId: Long,
    itemName: String,
    onNavigateBack: () -> Unit,
    onReviewSubmitted: () -> Unit,
    viewModel: AddReviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val isFormValid by viewModel.isFormValid.collectAsStateWithLifecycle()
    val titleCharCount by viewModel.titleCharCount.collectAsStateWithLifecycle()
    val bodyCharCount by viewModel.bodyCharCount.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is AddReviewUiState.Success) onReviewSubmitted()
    }

    AddReviewStateless(
        itemName = itemName,
        formState = formState,
        uiState = uiState,
        isFormValid = isFormValid,
        titleCharCount = titleCharCount,
        bodyCharCount = bodyCharCount,
        onNavigateBack = onNavigateBack,
        onRatingChange = viewModel::updateRating,
        onTitleChange = viewModel::updateTitle,
        onBodyChange = viewModel::updateBody,
        onProsChange = viewModel::updatePros,
        onConsChange = viewModel::updateCons,
        onWeatherChange = viewModel::updateWeatherCondition,
        onFitRatingChange = viewModel::updateFitRating,
        onWarmthRatingChange = viewModel::updateWarmthRating,
        onDurabilityRatingChange = viewModel::updateDurabilityRating,
        onValueRatingChange = viewModel::updateValueRating,
        onSubmit = { viewModel.submitReview(itemId) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReviewStateless(
    itemName: String,
    formState: com.outwear.app.presentation.viewmodel.ReviewFormState,
    uiState: AddReviewUiState,
    isFormValid: Boolean,
    titleCharCount: Int,
    bodyCharCount: Int,
    onNavigateBack: () -> Unit,
    onRatingChange: (Float) -> Unit,
    onTitleChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
    onProsChange: (String) -> Unit,
    onConsChange: (String) -> Unit,
    onWeatherChange: (String) -> Unit,
    onFitRatingChange: (Int) -> Unit,
    onWarmthRatingChange: (Int) -> Unit,
    onDurabilityRatingChange: (Int) -> Unit,
    onValueRatingChange: (Int) -> Unit,
    onSubmit: () -> Unit
) {
    val weatherOptions = listOf("Rain", "Snow", "Wind", "Cold", "All-season", "Extreme Cold")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Write a Review", style = MaterialTheme.typography.titleMedium)
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
        }
    ) { padding ->
        if (uiState is AddReviewUiState.Loading) {
            LoadingScreen(Modifier.padding(padding))
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Overall rating
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Overall Rating *",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(8.dp))
                    InteractiveStarRating(
                        rating = formState.rating.toInt(),
                        onRatingChange = { onRatingChange(it.toFloat()) }
                    )
                    formState.ratingError?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            // Title
            OutlinedTextField(
                value = formState.title,
                onValueChange = onTitleChange,
                label = { Text("Review Title *") },
                placeholder = { Text("Summarize your experience") },
                supportingText = {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        formState.titleError?.let {
                            Text(it, color = MaterialTheme.colorScheme.error)
                        } ?: Spacer(Modifier.weight(1f))
                        Text("$titleCharCount/100", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                isError = formState.titleError != null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Body
            OutlinedTextField(
                value = formState.body,
                onValueChange = onBodyChange,
                label = { Text("Your Review *") },
                placeholder = { Text("Share your detailed experience with this item…") },
                supportingText = {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        formState.bodyError?.let {
                            Text(it, color = MaterialTheme.colorScheme.error)
                        } ?: Text(
                            if (bodyCharCount < 20) "${20 - bodyCharCount} more characters needed"
                            else "✓ Minimum met",
                            color = if (bodyCharCount < 20) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.primary
                        )
                        Text("$bodyCharCount/2000", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                isError = formState.bodyError != null,
                modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                maxLines = 10
            )

            // Pros & Cons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = formState.pros,
                    onValueChange = onProsChange,
                    label = { Text("Pros") },
                    placeholder = { Text("e.g. Waterproof, Light") },
                    modifier = Modifier.weight(1f),
                    supportingText = { Text("Separate with commas") }
                )
                OutlinedTextField(
                    value = formState.cons,
                    onValueChange = onConsChange,
                    label = { Text("Cons") },
                    placeholder = { Text("e.g. Expensive") },
                    modifier = Modifier.weight(1f),
                    supportingText = { Text("Separate with commas") }
                )
            }

            // Weather condition
            WeatherConditionSelector(
                options = weatherOptions,
                selected = formState.weatherCondition,
                onSelected = onWeatherChange
            )

            // Sub-ratings
            SubRatingsSection(
                fitRating = formState.fitRating,
                warmthRating = formState.warmthRating,
                durabilityRating = formState.durabilityRating,
                valueRating = formState.valueRating,
                onFitChange = onFitRatingChange,
                onWarmthChange = onWarmthRatingChange,
                onDurabilityChange = onDurabilityRatingChange,
                onValueChange = onValueRatingChange
            )

            if (uiState is AddReviewUiState.Error) {
                Text(
                    "Error: ${uiState.message}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = onSubmit,
                enabled = isFormValid,
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text("Submit Review", style = MaterialTheme.typography.titleSmall)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
