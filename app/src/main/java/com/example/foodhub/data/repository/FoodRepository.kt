package com.example.foodhub.data.repository

import com.example.foodhub.data.local.dao.*
import com.example.foodhub.data.local.entities.*
import com.example.foodhub.data.network.AddToCartRequest
import com.example.foodhub.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class FoodRepository(
    private val productDao: ProductDao,
    private val cartDao: CartDao,
    private val orderDao: OrderDao,
    private val userDao: UserDao
) {
    // --- PRODUCTOS ---
    val products: Flow<List<Product>> = productDao.observeProducts()

    suspend fun refreshProducts() {
        try {
            val remote = RetrofitClient.api.getProducts() // Asegúrate que en API se llame getProducts
            productDao.insertAll(remote)
        } catch (e: Exception) { e.printStackTrace() }
    }

    suspend fun getProductById(id: Long): Product? = productDao.getById(id)

    // Esta es la función que AdminVM necesita
    suspend fun saveProduct(product: Product) {
        try {
            // Lógica simple: Si ID=0 crear, si no actualizar.
            // Para simplificar en local, usamos insert (que reemplaza si hay conflicto)
            productDao.insert(product)

            // Intenta enviar al backend
            if (product.id == 0L) RetrofitClient.api.createProduct(product)
            else RetrofitClient.api.updateProduct(product.id, product)
        } catch (e: Exception) { e.printStackTrace() }
    }

    // Esta es la función que AdminVM necesita
    suspend fun deleteProduct(product: Product) {
        productDao.delete(product)
        try { RetrofitClient.api.deleteProduct(product.id) } catch (e: Exception) {}
    }

    // --- CARRITO ---
    val cartItems: Flow<List<CartItem>> = cartDao.observeCartItems()

    suspend fun addToCart(product: Product, userId: Long?) {
        // Local
        val current = cartItems.first()
        val existing = current.find { it.productId == product.id }
        if (existing != null) {
            cartDao.upsertCartItem(existing.copy(quantity = existing.quantity + 1))
        } else {
            cartDao.upsertCartItem(CartItem(productId = product.id, name = product.name, price = product.price, quantity = 1, imageUrl = product.imageUrl))
        }
        // Backend
        if (userId != null) {
            try { RetrofitClient.api.addToCart(AddToCartRequest(userId, product.id, 1)) } catch (e: Exception) {}
        }
    }

    suspend fun removeFromCart(item: CartItem) = cartDao.deleteCartItem(item)
    suspend fun clearCart() = cartDao.clearCart()

    // --- ORDENES Y AUTH ---
    suspend fun createOrder(order: Order): Boolean {
        return try {
            RetrofitClient.api.createOrder(order)
            true
        } catch (e: Exception) { false }
    }

    // Función que faltaba en OrderHistoryVM
    fun getOrdersForUser(userId: Long): Flow<List<Order>> = flow {
        try {
            val orders = RetrofitClient.api.getOrdersByUser(userId)
            emit(orders)
        } catch (e: Exception) { emit(emptyList()) }
    }

    suspend fun login(email: String, pass: String) = try {
        RetrofitClient.api.login(mapOf("email" to email, "password" to pass))
    } catch (e: Exception) { null }

    suspend fun register(user: User) = try {
        RetrofitClient.api.register(user)
    } catch (e: Exception) { null }
}