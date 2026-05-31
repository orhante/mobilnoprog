package com.outwear.app.presentation.ui.screens.network.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * Dialog for demonstrating the POST endpoint (create product).
 * Validates that title is non-blank and price is a valid number.
 */
@Composable
fun CreateProductDialog(
    onDismiss: () -> Unit,
    onCreate: (title: String, price: Double) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    // Inline validation
    val titleError = title.isNotBlank() && title.trim().length < 3
    val priceError = price.isNotBlank() && price.toDoubleOrNull() == null
    val canSubmit = title.trim().length >= 3 && price.toDoubleOrNull() != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Product (POST)") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title *") },
                    isError = titleError,
                    supportingText = {
                        if (titleError) Text("Title must be at least 3 characters",
                            color = MaterialTheme.colorScheme.error)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price *") },
                    isError = priceError,
                    supportingText = {
                        if (priceError) Text("Enter a valid number",
                            color = MaterialTheme.colorScheme.error)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = price.toDoubleOrNull() ?: 0.0
                    if (title.trim().isNotBlank()) onCreate(title.trim(), p)
                },
                enabled = canSubmit
            ) { Text("Create") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
