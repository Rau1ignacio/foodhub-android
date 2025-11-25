package com.example.foodhub.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub.data.local.entities.Product
import com.example.foodhub.data.repository.FoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val products: List<Product> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String = "Todos",
    val filteredProducts: List<Product> = emptyList()
)

/**
 * ViewModel de Home:
 * - Observa los productos de Room (repo.products).
 * - Sincroniza con backend.
 * - Aplica filtros por búsqueda y categoría.
 */
class HomeVM(
    private val repo: FoodRepository
) : ViewModel() {

    private val _state = MutableStateFlow(
        HomeState(
            isLoading = true
        )
    )
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        // 1) Observar cambios en productos de Room (altas/bajas desde Admin, etc.)
        viewModelScope.launch {
            repo.products.collect { list ->
                _state.update { current ->
                    val base = current.copy(
                        products = list
                    )
                    base.copy(
                        filteredProducts = applyFilters(
                            products = list,
                            searchQuery = base.searchQuery,
                            category = base.selectedCategory
                        ),
                        isLoading = false,
                        error = null
                    )
                }
            }
        }

        // 2) Sincronizar con el backend al iniciar
        viewModelScope.launch {
            try {
                repo.syncProductsFromBackend()
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error al cargar productos"
                    )
                }
            }
        }
    }

    private fun applyFilters(
        products: List<Product>,
        searchQuery: String,
        category: String
    ): List<Product> {
        val q = searchQuery.trim().lowercase()

        return products
            .filter { product ->
                // Filtrar por categoría
                if (category == "Todos") true
                else product.category == category
            }
            .filter { product ->
                // Filtrar por búsqueda
                if (q.isBlank()) true
                else {
                    product.name.lowercase().contains(q) ||
                            product.description.lowercase().contains(q)
                }
            }
    }

    fun onSearchQueryChange(text: String) {
        _state.update { current ->
            val newState = current.copy(searchQuery = text)
            newState.copy(
                filteredProducts = applyFilters(
                    products = newState.products,
                    searchQuery = newState.searchQuery,
                    category = newState.selectedCategory
                )
            )
        }
    }

    fun onCategorySelected(category: String) {
        _state.update { current ->
            val newState = current.copy(selectedCategory = category)
            newState.copy(
                filteredProducts = applyFilters(
                    products = newState.products,
                    searchQuery = newState.searchQuery,
                    category = newState.selectedCategory
                )
            )
        }
    }

    /**
     * Devuelve las categorías disponibles para los chips:
     * "Todos" + categorías distintas que existan en productos.
     * (Ej: FRUTAS, VERDURAS, LACTEOS, BEBIDAS, OTROS)
     */
    fun getCategories(): List<String> {
        val dynamic = _state.value.products
            .map { it.category }
            .distinct()
            .sorted()

        return listOf("Todos") + dynamic
    }
}
