package com.example.foodhub.data.repository

import com.example.foodhub.data.local.dao.CartDao
import com.example.foodhub.data.local.dao.LoginRequestDto
import com.example.foodhub.data.local.dao.OrderDao
import com.example.foodhub.data.local.dao.ProductDao
import com.example.foodhub.data.local.dao.UserDao
import com.example.foodhub.data.local.entities.CartItem
import com.example.foodhub.data.local.entities.Order
import com.example.foodhub.data.local.entities.Product
import com.example.foodhub.data.local.entities.User
import com.example.foodhub.data.network.FoodApi
import com.example.foodhub.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio central:
 * - API REST (Retrofit) para productos y auth.
 * - Room (DAOs) para caché local, carrito y órdenes.
 */
class FoodRepository(
    private val productDao: ProductDao,
    private val cartDao: CartDao,
    private val orderDao: OrderDao,
    private val userDao: UserDao,
    private val api: FoodApi = RetrofitClient.api
) {

    // -------------------- PRODUCTOS --------------------

    /** Flow reactivo con todos los productos en Room. */
    val products: Flow<List<Product>> = productDao.observeProducts()

    /** Sincroniza productos desde el backend y los guarda en Room. */
    suspend fun syncProductsFromBackend() {
        val remote = api.getProducts()
        productDao.clearAll()
        productDao.insertAll(remote)
    }

    /** Carga productos desde backend y retorna la lista local. */
    suspend fun getAllProducts(): List<Product> {
        syncProductsFromBackend()
        return productDao.getAll()
    }

    /** Retorna un producto por id (primero local, luego remoto si hace falta). */
    suspend fun getProductById(id: Long): Product? {
        val local = productDao.getById(id)
        if (local != null) return local

        return try {
            val remote = api.getProductById(id)
            productDao.insert(remote)
            remote
        } catch (_: Exception) {
            null
        }
    }

    /** Guardar producto (crea o actualiza tanto en backend como en Room). */
    suspend fun saveProduct(product: Product) {
        val saved = if (product.id == 0L) {
            api.createProduct(product)
        } else {
            api.updateProduct(product.id, product)
        }
        productDao.insert(saved)
    }

    /** Eliminar producto en backend y en Room. */
    suspend fun deleteProduct(product: Product) {
        if (product.id != 0L) {
            try {
                api.deleteProduct(product.id)
            } catch (_: Exception) {
                // Si falla la red, igual intentamos limpiar local
            }
        }
        productDao.delete(product)
    }

    /** Devuelve productos sólo desde Room (sin llamar al backend). */
    suspend fun getProductsFromLocal(): List<Product> = productDao.getAll()

    // -------------------- AUTH --------------------

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            // Construimos el body tal como lo espera el backend
            val req = LoginRequestDto(
                email = email,
                password = password
            )

            val user = api.login(req)

            // Guardamos el usuario autenticado en Room
            userDao.clearUsers()
            userDao.insert(user)

            Result.success(user)
        } catch (e: Exception) {
            e.printStackTrace() // Muy importante: revisa Logcat para ver el error real
            Result.failure(e)
        }
    }

    /**
     * Registro contra el backend.
     */
    suspend fun register(user: User): Result<User> {
        return try {
            val saved = api.register(user)
            Result.success(saved)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }


    // -------------------- CARRITO (LOCAL) --------------------

    suspend fun getCartItems(): List<CartItem> = cartDao.getAll()

    fun observeCartItems(): Flow<List<CartItem>> = cartDao.observeCartItems()

    suspend fun addToCart(product: Product) {
        val existing = cartDao.getCartItemByProductId(product.id)
        val newQuantity = (existing?.quantity ?: 0) + 1

        val item = CartItem(
            id = existing?.id ?: 0L,
            productId = product.id,
            name = product.name,
            price = product.price,
            quantity = newQuantity,
            imageUrl = product.imageUrl
        )
        cartDao.upsertCartItem(item)
    }

    suspend fun updateCartItemQuantity(itemId: Long, newQuantity: Int) {
        if (newQuantity <= 0) {
            cartDao.deleteById(itemId)
        } else {
            cartDao.updateQuantity(itemId, newQuantity)
        }
    }

    suspend fun deleteCartItem(itemId: Long) {
        cartDao.deleteById(itemId)
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }

    // -------------------- ÓRDENES (historial local) --------------------

    /**
     * Crea una orden local a partir de los items del carrito.
     * También reduce el stock local en Room y limpia el carrito.
     */
    suspend fun createOrderLocal(
        userId: Long,
        items: List<CartItem>,
        total: Int
    ): Long {
        if (items.isEmpty()) return -1L

        // Resumen legible para mostrar en historial
        val summary = items.joinToString(separator = "\n") { item ->
            "${item.quantity}x ${item.name} - $${item.price * item.quantity}"
        }

        val order = Order(
            userId = userId,
            total = total,
            itemsSummary = summary,
            timestamp = System.currentTimeMillis(),
            status = "CONFIRMED"
        )

        val orderId = orderDao.insert(order)

        // Reducir stock localmente
        items.forEach { item ->
            productDao.decreaseStock(item.productId, item.quantity)
        }

        // Vaciar carrito
        cartDao.clearCart()

        return orderId
    }

    /** Flow con el historial de órdenes para un usuario. */
    fun getOrdersForUser(userId: Long): Flow<List<Order>> =
        orderDao.getOrdersForUser(userId)
}
