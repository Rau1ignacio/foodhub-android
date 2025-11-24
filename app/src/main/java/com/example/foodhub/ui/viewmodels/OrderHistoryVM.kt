package com.example.foodhub.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub.data.local.entities.Order
import com.example.foodhub.data.repository.FoodRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

data class OrderHistoryState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = true
)

class OrderHistoryVM(
    repo: FoodRepository,
    sessionVM: SessionVM
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<OrderHistoryState> =
        sessionVM.state.flatMapLatest { sessionState ->
            val user = sessionState.loggedInUser
            if (user != null) {
                repo.getOrdersForUser(user.id).map { ordersList ->
                    OrderHistoryState(orders = ordersList, isLoading = false)
                }
            } else {
                flowOf(OrderHistoryState(isLoading = false))
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = OrderHistoryState()
        )
}
