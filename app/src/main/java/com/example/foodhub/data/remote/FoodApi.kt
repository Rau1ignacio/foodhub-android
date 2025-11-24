package com.example.foodhub.data.network

import com.example.foodhub.data.local.dao.LoginRequestDto
import com.example.foodhub.data.local.entities.CartItem
import com.example.foodhub.data.local.entities.Order
import com.example.foodhub.data.local.entities.Product
import com.example.foodhub.data.local.entities.User
import retrofit2.http.*

// Mantenemos esto aquí, pero asegúrate de importarlo en el Repository
data class AddToCartRequest(
    val userId: Long,
    val productId: Long,
    val quantity: Int
)

interface FoodApi {

    // --- PRODUCTOS (General) ---
    @GET("/api/products")
    suspend fun getProducts(): List<Product>

    @GET("api/products/{id}")
    suspend fun getProductById(@Path("id") id: Long): Product

    // --- PRODUCTOS (Admin - Faltaban estos métodos) ---
    @POST("api/products")
    suspend fun createProduct(@Body product: Product): Product

    @PUT("api/products/{id}")
    suspend fun updateProduct(@Path("id") id: Long, @Body product: Product): Product

    @DELETE("api/products/{id}")
    suspend fun deleteProduct(@Path("id") id: Long)

    // --- AUTH ---
    @POST("/api/auth/register")
    suspend fun register(@Body user: User): User

    @POST("/api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequestDto): User

    // --- CARRITO ---
    @GET("api/cart/{userId}")
    suspend fun getCart(@Path("userId") userId: Long): List<CartItem>

    @POST("api/cart/add")
    suspend fun addToCart(@Body request: AddToCartRequest): CartItem

    @PUT("api/cart/{itemId}")
    suspend fun updateQuantity(
        @Path("itemId") itemId: Long,
        @Query("quantity") quantity: Int
    ): CartItem

    @DELETE("api/cart/{itemId}")
    suspend fun deleteCartItem(@Path("itemId") itemId: Long)

    // --- PEDIDOS (Faltaban estos métodos) ---
    @POST("api/orders")
    suspend fun createOrder(@Body order: Order): Order

    @GET("api/orders/user/{userId}")
    suspend fun getOrdersByUser(@Path("userId") userId: Long): List<Order>
}