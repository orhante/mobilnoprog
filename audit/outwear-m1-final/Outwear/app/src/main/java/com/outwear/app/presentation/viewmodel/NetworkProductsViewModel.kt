package com.outwear.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.outwear.app.model.data.remote.dto.CreateProductRequest
import com.outwear.app.model.data.remote.dto.RemoteProduct
import com.outwear.app.model.data.remote.dto.UpdateProductRequest
import com.outwear.app.model.repository.remote.NetworkResult
import com.outwear.app.model.repository.remote.ProductNetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class NetworkProductsUiState {
    object Idle : NetworkProductsUiState()
    object Loading : NetworkProductsUiState()
    data class Success(val products: List<RemoteProduct>) : NetworkProductsUiState()
    data class Error(val message: String) : NetworkProductsUiState()
}

sealed class NetworkActionState {
    object Idle : NetworkActionState()
    object Loading : NetworkActionState()
    data class Success(val product: RemoteProduct, val action: String) : NetworkActionState()
    data class Error(val message: String) : NetworkActionState()
}

@HiltViewModel
class NetworkProductsViewModel @Inject constructor(
    private val repository: ProductNetworkRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<NetworkProductsUiState>(NetworkProductsUiState.Idle)
    val uiState: StateFlow<NetworkProductsUiState> = _uiState.asStateFlow()

    private val _actionState = MutableStateFlow<NetworkActionState>(NetworkActionState.Idle)
    val actionState: StateFlow<NetworkActionState> = _actionState.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = NetworkProductsUiState.Loading
            when (val result = repository.fetchAllProducts()) {
                is NetworkResult.Success -> _uiState.value = NetworkProductsUiState.Success(result.data)
                is NetworkResult.Error   -> _uiState.value = NetworkProductsUiState.Error(result.message)
                else -> Unit
            }
        }
    }

    fun loadByCategory(category: String) {
        viewModelScope.launch {
            _uiState.value = NetworkProductsUiState.Loading
            when (val result = repository.fetchProductsByCategory(category)) {
                is NetworkResult.Success -> _uiState.value = NetworkProductsUiState.Success(result.data)
                is NetworkResult.Error   -> _uiState.value = NetworkProductsUiState.Error(result.message)
                else -> Unit
            }
        }
    }

    /** POST – demonstrates create */
    fun createProduct(title: String, price: Double, description: String, category: String) {
        viewModelScope.launch {
            _actionState.value = NetworkActionState.Loading
            val request = CreateProductRequest(
                title = title,
                price = price,
                description = description,
                image = "https://fakestoreapi.com/img/placeholder.jpg",
                category = category
            )
            when (val result = repository.createProduct(request)) {
                is NetworkResult.Success -> _actionState.value = NetworkActionState.Success(result.data, "Created")
                is NetworkResult.Error   -> _actionState.value = NetworkActionState.Error(result.message)
                else -> Unit
            }
        }
    }

    /** PUT – demonstrates update */
    fun updateProduct(id: Int, title: String, price: Double) {
        viewModelScope.launch {
            _actionState.value = NetworkActionState.Loading
            val request = UpdateProductRequest(
                title = title,
                price = price,
                description = "Updated via Retrofit",
                image = "https://fakestoreapi.com/img/placeholder.jpg",
                category = "clothing"
            )
            when (val result = repository.updateProduct(id, request)) {
                is NetworkResult.Success -> _actionState.value = NetworkActionState.Success(result.data, "Updated")
                is NetworkResult.Error   -> _actionState.value = NetworkActionState.Error(result.message)
                else -> Unit
            }
        }
    }

    /** DELETE – demonstrates delete */
    fun deleteProduct(id: Int) {
        viewModelScope.launch {
            _actionState.value = NetworkActionState.Loading
            when (val result = repository.deleteProduct(id)) {
                is NetworkResult.Success -> _actionState.value = NetworkActionState.Success(result.data, "Deleted")
                is NetworkResult.Error   -> _actionState.value = NetworkActionState.Error(result.message)
                else -> Unit
            }
        }
    }

    fun clearActionState() {
        _actionState.value = NetworkActionState.Idle
    }
}
