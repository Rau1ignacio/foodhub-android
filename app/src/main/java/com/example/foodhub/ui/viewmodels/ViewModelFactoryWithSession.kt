package com.example.foodhub.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foodhub.data.repository.FoodRepository

class ViewModelFactoryWithSession(
    private val repo: FoodRepository,
    private val sessionVM: SessionVM
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthVM::class.java) ->
                AuthVM(repo, sessionVM) as T

            modelClass.isAssignableFrom(CartVM::class.java) ->
                CartVM(repo, sessionVM) as T

            modelClass.isAssignableFrom(OrderHistoryVM::class.java) ->
                OrderHistoryVM(repo, sessionVM) as T

            else -> throw IllegalArgumentException(
                "Unknown ViewModel class: ${modelClass.name}"
            )
        }
    }
}
