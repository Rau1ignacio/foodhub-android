package com.example.foodhub.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub.data.local.entities.Order
import com.example.foodhub.data.repository.FoodRepository
import kotlinx.coroutines.flow.*

/** Estado: El estado de la pantalla de Historial. */
data class OrderHistoryState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = true
)

/**
 * ViewModel para el Historial de Pedidos.
 * Depende de 'sessionVM' para saber QUÉ usuario está logueado.
 */
class OrderHistoryVM(
    repo: FoodRepository,
    sessionVM: SessionVM
) : ViewModel() {

    /**
     * Estado principal.
     * 'flatMapLatest' reacciona a cambios en el 'sessionVM'.
     * Si el usuario cambia (o inicia sesión), automáticamente busca las órdenes
     * para ESE usuario.
     */
    val state: StateFlow<OrderHistoryState> = sessionVM.state.flatMapLatest { sessionState ->
        sessionState.loggedInUser?.let { user ->
            // Si hay usuario, observa sus órdenes desde el repo
            repo.getOrdersForUser(user.id).map { orders ->
                OrderHistoryState(orders = orders, isLoading = false)
            }
        } ?: flowOf(OrderHistoryState(isLoading = false)) // Si no hay usuario, emite estado vacío
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = OrderHistoryState() // Estado inicial (cargando)
    )
}