package com.example.foodhub.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub.data.local.entities.CartItem
import com.example.foodhub.data.local.entities.Order
import com.example.foodhub.data.local.entities.Product
import com.example.foodhub.data.repository.FoodRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CartState(
    val items: List<Pair<Product, CartItem>> = emptyList(),
    val total: Int = 0
)

class CartVM(
    private val repo: FoodRepository,
    private val sessionVM: SessionVM
) : ViewModel() {

    // CORREGIDO: repo.products y repo.cartItems son propiedades (val), no funciones
    val cartState: StateFlow<CartState> = combine(
        repo.products,
        repo.cartItems
    ) { products, cartItems ->
        val itemsWithDetails = cartItems.mapNotNull { cartItem ->
            products.find { it.id == cartItem.productId }?.let { product ->
                Pair(product, cartItem)
            }
        }
        val total = itemsWithDetails.sumOf { (product, cartItem) ->
            product.price * cartItem.quantity
        }
        CartState(items = itemsWithDetails, total = total)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CartState())

    fun addToCart(product: Product) {
        viewModelScope.launch {
            // Obtenemos el usuario actual de la sesión
            val userId = sessionVM.state.value.loggedInUser?.id

            // Delegamos la lógica al repositorio
            repo.addToCart(product, userId)
        }
    }

    fun removeFromCart(cartItem: CartItem) {
        viewModelScope.launch { repo.removeFromCart(cartItem) }
    }

    fun confirmOrder(onOrderConfirmed: (Long) -> Unit) {
        viewModelScope.launch {
            val state = cartState.value
            if (state.items.isEmpty()) return@launch

            val userId = sessionVM.state.value.loggedInUser?.id ?: return@launch

            val summary = state.items.joinToString(", ") { "${it.second.quantity}x ${it.first.name}" }

            val order = Order(
                userId = userId,
                total = state.total,
                itemsSummary = summary,
                timestamp = System.currentTimeMillis(),
                status = "PENDING"
            )

            // createOrder ahora maneja la llamada al Backend dentro del Repo
            val success = repo.createOrder(order)

            if (success) {
                repo.clearCart() // Limpiamos carrito local
                onOrderConfirmed(0) // Navegamos
            }
        }
    }
}