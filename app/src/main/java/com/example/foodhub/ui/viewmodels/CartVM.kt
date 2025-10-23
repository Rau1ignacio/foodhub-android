package com.example.foodhub.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhubtest.data.local.entities.CartItem
import com.example.foodhubtest.data.local.entities.Order
import com.example.foodhubtest.data.local.entities.Product
import com.example.foodhubtest.data.repository.FoodRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/** Estado: El estado de la UI del carrito. */
data class CartState(
    val items: List<Pair<Product, CartItem>> = emptyList(), // Pares de (Producto completo, Item del carrito)
    val total: Int = 0 // Total calculado
)

/**
 * ViewModel para el Carrito.
 * Recibe 'sessionVM' para saber el ID del usuario al confirmar el pedido.
 */
class CartVM(
    private val repo: FoodRepository,
    private val sessionVM: SessionVM // Inyecta el VM de Sesión
) : ViewModel() {

    // --- ESTADO PRINCIPAL ---
    /**
     * Combina el flujo de 'products' y 'cartItems'.
     * Crea un 'CartState' que contiene los detalles completos del producto
     * y el total calculado.
     */
    val cartState: StateFlow<CartState> = repo.products()
        .combine(repo.getCartItems()) { products, cartItems ->
            // 1. Cruza los datos (busca el Product para cada CartItem)
            val itemsWithDetails = cartItems.mapNotNull { cartItem ->
                products.find { it.id == cartItem.productId }?.let { product ->
                    Pair(product, cartItem)
                }
            }
            // 2. Calcula el total
            val total = itemsWithDetails.sumOf { (product, cartItem) ->
                product.price * cartItem.quantity
            }
            // 3. Emite el estado completo
            CartState(items = itemsWithDetails, total = total)
        }.stateIn( // Convierte el Flow a StateFlow
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CartState()
        )

    /** Evento: Añade 1 unidad de un producto. Si ya existe, actualiza la cantidad. */
    fun addToCart(productId: Long) {
        viewModelScope.launch {
            // Busca la cantidad actual (0 si no existe)
            val currentQuantity = cartState.value.items
                .find { it.first.id == productId }?.second?.quantity ?: 0
            // Inserta/Actualiza con cantidad + 1
            repo.addToCart(CartItem(productId, currentQuantity + 1))
        }
    }

    /** Evento: Elimina un item de la tabla CartItem. */
    fun removeFromCart(cartItem: CartItem) {
        viewModelScope.launch {
            repo.removeFromCart(cartItem)
        }
    }

    /** Evento: Confirma el pedido. */
    fun confirmOrder(onSuccess: (Long) -> Unit) {
        viewModelScope.launch {
            val total = cartState.value.total
            // Obtiene el ID del usuario desde el SessionVM
            val userId = sessionVM.state.value.loggedInUser?.id

            // Solo procede si el carrito no está vacío y hay un usuario logueado
            if (total > 0 && userId != null) {
                // 1. Inserta la Orden (asociada al userId)
                val newOrderId = repo.insertOrder(Order(userId = userId, total = total))
                // 2. Limpia el carrito
                repo.clearCart()
                // 3. Llama al callback (para navegar) con el ID de la nueva orden
                onSuccess(newOrderId)
            }
        }
    }
}