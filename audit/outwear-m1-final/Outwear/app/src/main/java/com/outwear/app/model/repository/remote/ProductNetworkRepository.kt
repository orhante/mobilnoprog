package com.outwear.app.model.repository.remote

import com.outwear.app.model.data.remote.api.FakeStoreApiService
import com.outwear.app.model.data.remote.dto.CreateProductRequest
import com.outwear.app.model.data.remote.dto.RemoteProduct
import com.outwear.app.model.data.remote.dto.UpdateProductRequest
import com.outwear.app.model.data.remote.dto.toRemoteProduct
import javax.inject.Inject

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String, val code: Int? = null) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()
}

interface ProductNetworkRepository {
    suspend fun fetchAllProducts(): NetworkResult<List<RemoteProduct>>
    suspend fun fetchProductById(id: Int): NetworkResult<RemoteProduct>
    suspend fun fetchProductsByCategory(category: String): NetworkResult<List<RemoteProduct>>
    suspend fun fetchCategories(): NetworkResult<List<String>>
    suspend fun createProduct(request: CreateProductRequest): NetworkResult<RemoteProduct>
    suspend fun updateProduct(id: Int, request: UpdateProductRequest): NetworkResult<RemoteProduct>
    suspend fun deleteProduct(id: Int): NetworkResult<RemoteProduct>
}

class ProductNetworkRepositoryImpl @Inject constructor(
    private val api: FakeStoreApiService
) : ProductNetworkRepository {

    override suspend fun fetchAllProducts(): NetworkResult<List<RemoteProduct>> = safeApiCall {
        val response = api.getAllProducts()
        if (response.isSuccessful) {
            NetworkResult.Success(response.body()!!.map { it.toRemoteProduct() })
        } else {
            NetworkResult.Error("HTTP ${response.code()}: ${response.message()}", response.code())
        }
    }

    override suspend fun fetchProductById(id: Int): NetworkResult<RemoteProduct> = safeApiCall {
        val response = api.getProductById(id)
        if (response.isSuccessful) {
            NetworkResult.Success(response.body()!!.toRemoteProduct())
        } else {
            NetworkResult.Error("HTTP ${response.code()}: ${response.message()}", response.code())
        }
    }

    override suspend fun fetchProductsByCategory(category: String): NetworkResult<List<RemoteProduct>> = safeApiCall {
        val response = api.getProductsByCategory(category)
        if (response.isSuccessful) {
            NetworkResult.Success(response.body()!!.map { it.toRemoteProduct() })
        } else {
            NetworkResult.Error("HTTP ${response.code()}: ${response.message()}", response.code())
        }
    }

    override suspend fun fetchCategories(): NetworkResult<List<String>> = safeApiCall {
        val response = api.getCategories()
        if (response.isSuccessful) {
            NetworkResult.Success(response.body()!!)
        } else {
            NetworkResult.Error("HTTP ${response.code()}: ${response.message()}", response.code())
        }
    }

    override suspend fun createProduct(request: CreateProductRequest): NetworkResult<RemoteProduct> = safeApiCall {
        val response = api.createProduct(request)
        if (response.isSuccessful) {
            NetworkResult.Success(response.body()!!.toRemoteProduct())
        } else {
            NetworkResult.Error("HTTP ${response.code()}: ${response.message()}", response.code())
        }
    }

    override suspend fun updateProduct(id: Int, request: UpdateProductRequest): NetworkResult<RemoteProduct> = safeApiCall {
        val response = api.updateProduct(id, request)
        if (response.isSuccessful) {
            NetworkResult.Success(response.body()!!.toRemoteProduct())
        } else {
            NetworkResult.Error("HTTP ${response.code()}: ${response.message()}", response.code())
        }
    }

    override suspend fun deleteProduct(id: Int): NetworkResult<RemoteProduct> = safeApiCall {
        val response = api.deleteProduct(id)
        if (response.isSuccessful) {
            NetworkResult.Success(response.body()!!.toRemoteProduct())
        } else {
            NetworkResult.Error("HTTP ${response.code()}: ${response.message()}", response.code())
        }
    }

    private suspend fun <T> safeApiCall(call: suspend () -> NetworkResult<T>): NetworkResult<T> {
        return try {
            call()
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Network error occurred")
        }
    }
}
