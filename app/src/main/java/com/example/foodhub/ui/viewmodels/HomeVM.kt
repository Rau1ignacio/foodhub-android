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

/**
 * Estado de la pantalla de Home:
 * - lista completa de productos
 * - lista filtrada
 * - categoría seleccionada
 * - texto de búsqueda
 */
data class HomeState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val selectedCategory: String = "Todos",
    val searchQuery: String = "",
    val error: String? = null
)

class HomeVM(
    private val repo: FoodRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val categories = listOf("Todos", "Frutas", "Verduras", "Lácteos", "Bebidas", "Otros")

    init {
        loadProducts()
    }

    fun getCategories(): List<String> = categories

    /**
     * Carga la lista de productos desde el repositorio.
     */
    fun loadProducts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val products = repo.getAllProducts()
                _state.update {
                    it.copy(
                        isLoading = false,
                        products = products,
                        filteredProducts = applyFilters(
                            products = products,
                            category = it.selectedCategory,
                            query = it.searchQuery
                        )
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    /**
     * Se llama cuando el usuario selecciona una categoría (Frutas, Verduras, etc.)
     */
    fun onCategorySelected(category: String) {
        _state.update { current ->
            val newState = current.copy(selectedCategory = category)
            newState.copy(
                filteredProducts = applyFilters(
                    products = newState.products,
                    category = category,
                    query = newState.searchQuery
                )
            )
        }
    }

    /**
     * Se llama cuando el usuario escribe en el buscador.
     */
    fun onSearchQueryChange(query: String) {
        _state.update { current ->
            val newState = current.copy(searchQuery = query)
            newState.copy(
                filteredProducts = applyFilters(
                    products = newState.products,
                    category = newState.selectedCategory,
                    query = query
                )
            )
        }
    }

    /**
     * Aplica simultáneamente:
     * - filtro por categoría
     * - búsqueda por texto
     */
    private fun applyFilters(
        products: List<Product>,
        category: String,
        query: String
    ): List<Product> {
        return products
            .asSequence()
            .filter { p ->
                (category == "Todos" || p.category.equals(category, ignoreCase = true))
            }
            .filter { p ->
                query.isBlank() ||
                        p.name.contains(query, ignoreCase = true) ||
                        p.description.contains(query, ignoreCase = true)
            }
            .toList()
    }
}
