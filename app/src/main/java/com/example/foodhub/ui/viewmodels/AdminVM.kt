package com.example.foodhub.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub.data.local.entities.Product
import com.example.foodhub.data.repository.FoodRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminVM(
    private val repo: FoodRepository
) : ViewModel() {

    // Lista reactiva de productos para el admin
    val products: StateFlow<List<Product>> =
        repo.products.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    fun getProductById(id: Long?): Product? {
        if (id == null || id == 0L) return null
        return products.value.firstOrNull { it.id == id }
    }

    fun createProduct(product: Product) {
        viewModelScope.launch {
            repo.saveProduct(product.copy(id = 0L))
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repo.saveProduct(product)
        }
    }

    fun deleteProduct(id: Long) {
        viewModelScope.launch {
            val p = products.value.firstOrNull { it.id == id } ?: return@launch
            repo.deleteProduct(p)
        }
    }
}
