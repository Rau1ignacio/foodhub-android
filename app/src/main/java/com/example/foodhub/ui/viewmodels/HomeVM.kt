package com.example.foodhub.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub.data.local.entities.Product
import com.example.foodhub.data.repository.FoodRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// El estado debe estar AQUÍ o en un archivo separado, pero visible.
data class HomeState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedCategory: String = "Todos"
)

class HomeVM(private val repo: FoodRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow("Todos")
    private val _isLoading = MutableStateFlow(false)

    val state: StateFlow<HomeState> = combine(
        repo.products, // Asegúrate que en repo sea 'val products'
        _searchQuery,
        _selectedCategory,
        _isLoading
    ) { products, query, category, loading ->
        val filtered = products.filter { product ->
            (category == "Todos" || product.category == category) &&
                    (product.name.contains(query, ignoreCase = true))
        }
        HomeState(filtered, loading, query, category)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeState())

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            _isLoading.value = true
            repo.refreshProducts()
            _isLoading.value = false
        }
    }

    fun onSearchChange(query: String) { _searchQuery.value = query }
    fun onCategoryChange(cat: String) { _selectedCategory.value = cat }
}