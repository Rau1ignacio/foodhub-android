package com.example.foodhub.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhubtest.data.local.entities.CartItem
import com.example.foodhubtest.data.local.entities.Order
import com.example.foodhubtest.data.local.entities.Product
import com.example.foodhubtest.data.repository.FoodRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Define el estado de la UI del Carrito.
 * 'items' es una lista de Pares (El Producto completo, El Item del carrito)
 * para así tener todos los detalles (nombre, precio, cantidad).
 */
data class CartState(
    val items: List<Pair<Product, CartItem>> = emptyList(),
    val total: Int = 0 // El precio total calculado
)

/**
 * ViewModel para la pantalla del Carrito.
 */
class CartVM(
    private val repo: FoodRepository,
    private val sessionVM: SessionVM // Se inyecta el VM de Sesión para saber el ID del usuario logueado
) : ViewModel() {

    // --- ESTADO PRINCIPAL (Observado por la UI) ---
    /**
     * Expone el estado del carrito (CartState) a la UI.
     * Es la parte más importante de este ViewModel.
     */
    val cartState: StateFlow<CartState> = repo.products()
        // 1. Combina el flujo de TODOS los producto
        .combine(repo.getCartItems()) { products, cartItems ->

            // 3. Cruza los datos: Busca el Producto completo para cada Item del carrito
            val itemsWithDetails = cartItems.mapNotNull { cartItem ->
                products.find { it.id == cartItem.productId }?.let { product ->
                    Pair(product, cartItem) // Crea el Par (Producto, Item)
                }
            }

            // 4. Calcula el total sumando (precio * cantidad) de los items encontrados
            val total = itemsWithDetails.sumOf { (product, cartItem) ->
                product.price * cartItem.quantity
            }

            // 5. Emite el estado final y completo para la UI
            CartState(items = itemsWithDetails, total = total)

        }.stateIn( // 6. Convierte el flujo (Flow) a un StateFlow (para que la UI lo observe)
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CartState() // Estado inicial mientras carga
        )

    // --- ACCIONES DEL USUARIO (Eventos) ---

    /**
     * Añade una unidad de un producto al carrito.
     * Si ya existe en el carrito, actualiza su cantidad (suma 1).
     */
    fun addToCart(productId: Long) {
        viewModelScope.launch {
            // Busca la cantidad actual (0 si no existe)
            val currentQuantity = cartState.value.items
                .find { it.first.id == productId }?.second?.quantity ?: 0

            // Llama al repo para insertar/actualizar (cantidad + 1)
            repo.addToCart(CartItem(productId, currentQuantity + 1))
        }
    }

    /** Elimina un item completo del carrito (sin importar la cantidad). */
    fun removeFromCart(cartItem: CartItem) {
        viewModelScope.launch {
            repo.removeFromCart(cartItem)
        }
    }

    /**
     * Confirma el pedido:
     * 1. Crea una nueva 'Order' en la BD (asociada al usuario de la sesión).
     * 2. Limpia el carrito.
     * 3. Llama a 'onSuccess' (ej. para navegar a la pantalla de resumen).
     */
    fun confirmOrder(onSuccess: (Long) -> Unit) {
        viewModelScope.launch {
            val total = cartState.value.total
            // Obtiene el ID del usuario logueado desde el SessionVM
            val userId = sessionVM.state.value.loggedInUser?.id

            // Valida que el carrito no esté vacío Y que haya un usuario logueado
            if (total > 0 && userId != null) {

                // 1. Inserta la orden (pasando el userId) y obtiene el ID de la nueva orden
                val newOrderId = repo.insertOrder(Order(userId = userId, total = total))

                // 2. Limpia el carrito
                repo.clearCart()

                // 3. Notifica a la UI que fue exitoso (pasando el ID de la orden creada)
                onSuccess(newOrderId)
            }
        }
    }
}