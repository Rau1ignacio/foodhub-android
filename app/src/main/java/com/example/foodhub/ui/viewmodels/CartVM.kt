package com.example.foodhub.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub.data.local.entities.CartItem
import com.example.foodhub.data.local.entities.Product
import com.example.foodhub.data.repository.FoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CartItemUi(
    val id: Long,
    val product: Product,
    val quantity: Int
)

data class CartState(
    val isLoading: Boolean = false,
    val items: List<CartItemUi> = emptyList(),
    val error: String? = null
) {
    val total: Int get() = items.sumOf { it.product.price * it.quantity }
}

class CartVM(
    private val repo: FoodRepository,
    private val sessionVM: SessionVM
) : ViewModel() {

    private val _state = MutableStateFlow(CartState())
    val state: StateFlow<CartState> = _state.asStateFlow()

    init {
        loadCart()
    }

    fun loadCart() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val entities = repo.getCartItems()
                val products = repo.getProductsFromLocal()

                val itemsUi = entities.mapNotNull { entity ->
                    val product = products.find { it.id == entity.productId }
                    product?.let {
                        CartItemUi(
                            id = entity.id,
                            product = it,
                            quantity = entity.quantity
                        )
                    }
                }

                _state.update { it.copy(isLoading = false, items = itemsUi) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun changeQuantity(itemId: Long, newQuantity: Int) {
        viewModelScope.launch {
            try {
                if (newQuantity <= 0) {
                    repo.deleteCartItem(itemId)
                } else {
                    repo.updateCartItemQuantity(itemId, newQuantity)
                }
                loadCart()
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            try {
                repo.addToCart(product)
                loadCart()
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    /**
     * Confirmar compra:
     * - Crea orden local (historial)
     * - Reduce stock local
     * - Limpia carrito
     * - Llama al callback onOrderConfirmed() para navegar o mostrar mensaje
     */
    fun confirmOrder(onOrderConfirmed: () -> Unit) {
        viewModelScope.launch {
            try {
                val current = state.value
                val user = sessionVM.state.value.loggedInUser ?: return@launch
                if (current.items.isEmpty()) return@launch

                val cartEntities = current.items.map { ui ->
                    CartItem(
                        id = ui.id,
                        productId = ui.product.id,
                        name = ui.product.name,
                        price = ui.product.price,
                        quantity = ui.quantity,
                        imageUrl = ui.product.imageUrl
                    )
                }

                // Creamos la orden local (ignoramos el id a nivel de UI)
                repo.createOrderLocal(
                    userId = user.id,
                    items = cartEntities,
                    total = current.total
                )

                loadCart()
                onOrderConfirmed()
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }
}
