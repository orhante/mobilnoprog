package com.outwear.app.presentation.ui.screens.discover.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.outwear.app.presentation.ui.components.CategoryChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Filter by Category",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))
            CategoryChip(
                label = "All Categories",
                selected = selectedCategory == null,
                onClick = {
                    onCategorySelected(null)
                    onDismiss()
                }
            )
            Spacer(Modifier.height(8.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    CategoryChip(
                        label = cat,
                        selected = selectedCategory == cat,
                        onClick = {
                            onCategorySelected(cat)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
