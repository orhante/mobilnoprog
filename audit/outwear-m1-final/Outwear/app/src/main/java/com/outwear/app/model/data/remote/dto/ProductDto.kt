package com.outwear.app.model.data.remote.dto

import com.google.gson.annotations.SerializedName

// DTOs for https://fakestoreapi.com/products (free public REST API)

data class ProductDto(
    @SerializedName("id")       val id: Int,
    @SerializedName("title")    val title: String,
    @SerializedName("price")    val price: Double,
    @SerializedName("description") val description: String,
    @SerializedName("category") val category: String,
    @SerializedName("image")    val image: String,
    @SerializedName("rating")   val rating: RatingDto
)

data class RatingDto(
    @SerializedName("rate")  val rate: Double,
    @SerializedName("count") val count: Int
)

data class CreateProductRequest(
    @SerializedName("title")       val title: String,
    @SerializedName("price")       val price: Double,
    @SerializedName("description") val description: String,
    @SerializedName("image")       val image: String,
    @SerializedName("category")    val category: String
)

data class UpdateProductRequest(
    @SerializedName("title")       val title: String,
    @SerializedName("price")       val price: Double,
    @SerializedName("description") val description: String,
    @SerializedName("image")       val image: String,
    @SerializedName("category")    val category: String
)

// Helper: map remote DTO to local domain-like object
data class RemoteProduct(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val imageUrl: String,
    val rating: Float,
    val reviewCount: Int
)

fun ProductDto.toRemoteProduct() = RemoteProduct(
    id = id,
    title = title,
    price = price,
    description = description,
    category = category,
    imageUrl = image,
    rating = rating.rate.toFloat(),
    reviewCount = rating.count
)
