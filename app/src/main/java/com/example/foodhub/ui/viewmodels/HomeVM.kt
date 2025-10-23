package com.example.foodhub.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub.data.local.entities.Product
import com.example.foodhub.data.repository.FoodRepository
import kotlinx.coroutines.flow.*

/** Estado: El estado completo de la pantalla Home. */
data class HomeState(
    val products: List<Product> = emptyList(), // La lista YA FILTRADA
    val searchQuery: String = "",
    val selectedCategory: String = "Todos"
)

/** ViewModel para la pantalla Home. */
class HomeVM(repo: FoodRepository) : ViewModel() {

    // --- ESTADOS INTERNOS (Filtros) ---
    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow("Todos")

    // --- ESTADO PÚBLICO (Observado por la UI) ---
    /**
     * Combina los flujos de búsqueda, categoría y productos.
     * La UI solo observa este 'state' y recibe la lista ya filtrada.
     */
    val state: StateFlow<HomeState> = combine(
        _searchQuery, // Flujo 1
        _selectedCategory, // Flujo 2
        repo.products() // Flujo 3 (Lista completa de la BD)
    ) { query, category, products ->

        // 1. Aplicar filtro de búsqueda
        val searchedProducts = if (query.isBlank()) {
            products
        } else {
            products.filter { it.name.contains(query, ignoreCase = true) }
        }

        // 2. Aplicar filtro de categoría (sobre la lista ya filtrada)
        val categorizedProducts = if (category == "Todos") {
            searchedProducts
        } else {
            searchedProducts.filter { it.category.equals(category, ignoreCase = true) }
        }

        // 3. Emite el estado final y filtrado
        HomeState(
            products = categorizedProducts,
            searchQuery = query,
            selectedCategory = category
        )
    }.stateIn( // Convierte a StateFlow
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeState()
    )

    /** Evento: La UI llama esto cuando el usuario escribe en la barra de búsqueda. */
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    /** Evento: La UI llama esto cuando el usuario selecciona un chip de categoría. */
    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }
}