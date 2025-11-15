package com.example.foodhub.data.repository

import com.example.foodhub.data.local.dao.OrderDao
import com.example.foodhub.data.local.dao.ProductDao
import com.example.foodhub.data.local.dao.UserDao
import com.example.foodhub.data.local.entities.CartItem
import com.example.foodhub.data.local.entities.Order
import com.example.foodhub.data.local.entities.Product
import com.example.foodhub.data.local.entities.User
import kotlinx.coroutines.flow.Flow

class FoodRepository(
    private val productDao: ProductDao,
    private val userDao: UserDao,
    private val orderDao: OrderDao
) {
    // --- Productos ---
    fun products(): Flow<List<Product>> = productDao.observeProducts()
    suspend fun getProduct(id: Long): Product? = productDao.getById(id)
    suspend fun insert(p: Product) = productDao.insert(p)
    // --- FUNCIONES AÑADIDAS ---
    suspend fun update(p: Product) = productDao.update(p)
    suspend fun delete(p: Product) = productDao.delete(p)

    // --- Carrito ---
    fun getCartItems(): Flow<List<CartItem>> = productDao.observeCartItems()
    suspend fun addToCart(item: CartItem) = productDao.upsertCartItem(item)
    suspend fun removeFromCart(item: CartItem) = productDao.deleteCartItem(item)
    suspend fun clearCart() = productDao.clearCart()

    // --- Órdenes ---
    suspend fun insertOrder(order: Order): Long = orderDao.insert(order)
    fun getOrdersForUser(userId: Long): Flow<List<Order>> = orderDao.getOrdersForUser(userId)

    // --- Usuarios ---
    suspend fun findUserByEmail(email: String): User? = userDao.findByEmail(email)
    suspend fun registerUser(user: User) = userDao.insert(user)


    // --- STOCK: RESTAR STOCK CUANDO SE COMPRA ---
    suspend fun decreaseStock(productId: Long, quantity: Int): Boolean {
        val product = productDao.getById(productId) ?: return false

        if (product.stock < quantity) return false

        val newStock = product.stock - quantity

        productDao.updateStock(productId, newStock)

        return true
    }
}