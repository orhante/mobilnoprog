package com.outwear.app.model.data.remote.api

import com.outwear.app.model.data.remote.dto.CreateProductRequest
import com.outwear.app.model.data.remote.dto.ProductDto
import com.outwear.app.model.data.remote.dto.UpdateProductRequest
import retrofit2.Response
import retrofit2.http.*

interface FakeStoreApiService {

    // GET all products
    @GET("products")
    suspend fun getAllProducts(): Response<List<ProductDto>>

    // GET products by category
    @GET("products/category/{category}")
    suspend fun getProductsByCategory(
        @Path("category") category: String
    ): Response<List<ProductDto>>

    // GET single product by id
    @GET("products/{id}")
    suspend fun getProductById(
        @Path("id") id: Int
    ): Response<ProductDto>

    // GET all available categories
    @GET("products/categories")
    suspend fun getCategories(): Response<List<String>>

    // POST – create a new product (FakeStore echoes back the created object)
    @POST("products")
    suspend fun createProduct(
        @Body request: CreateProductRequest
    ): Response<ProductDto>

    // PUT – replace an existing product
    @PUT("products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Body request: UpdateProductRequest
    ): Response<ProductDto>

    // DELETE – remove a product
    @DELETE("products/{id}")
    suspend fun deleteProduct(
        @Path("id") id: Int
    ): Response<ProductDto>
}
