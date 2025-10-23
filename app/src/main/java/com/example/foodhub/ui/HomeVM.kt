package com.example.foodhub.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhubtest.data.local.entities.Product
import com.example.foodhubtest.data.repository.FoodRepository
import kotlinx.coroutines.flow.*

/**
 * Define la "foto" completa del estado de la UI de la pantalla Home.
 * Es inmutable (data class). La UI solo debe recibir esto.
 */
data class HomeState(
    val products: List<Product> = emptyList(), // La lista de productos ya filtrada
    val searchQuery: String = "",              // El texto de búsqueda actual
    val selectedCategory: String = "Todos"     // La categoría seleccionada
)

    /**
     * ViewModel para la pantalla Home.
     * Recibe el repositorio para acceder a los datos.
     */
class HomeVM(repo: FoodRepository) : ViewModel() {

    // --- ESTADOS INTERNOS (Privados) ---
    // Representan las acciones del usuario (lo que escribe, lo que selecciona).

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow("Todos")

    // --- ESTADO PÚBLICO (Observado por la UI) ---
    /**
     * Expone un único 'StateFlow<HomeState>' a la UI.
     * Utiliza 'combine' para mezclar 3 flujos en 1 solo estado.
     */

    val state: StateFlow<HomeState> = combine(
        _searchQuery,        // 1. El flujo del texto de búsqueda
        _selectedCategory,   // 2. El flujo de la categoría
        repo.products()      // 3. El flujo de TODOS los productos desde la BD
    ) { query, category, products ->

        // --- Lógica de Filtro 1: Búsqueda ---

        val searchedProducts = if (query.isBlank()) {
            products // Si no hay búsqueda, usa la lista completa
        } else {
            // Filtra la lista por nombre
            products.filter { it.name.contains(query, ignoreCase = true) }
        }

        // --- Lógica de Filtro 2: Categoría ---

        val categorizedProducts = if (category == "Todos") {
            searchedProducts // Si es "Todos", usa la lista ya filtrada por búsqueda
        } else {
            // Filtra la lista (ya filtrada por búsqueda) por categoría
            searchedProducts.filter { it.category.equals(category, ignoreCase = true) }
        }

        // --- Creación del Estado Final ---
        // Devuelve el objeto de estado actualizado que recibirá la UI.

        HomeState(
            products = categorizedProducts, // La lista final y filtrada
            searchQuery = query,
            selectedCategory = category
        )
    }.stateIn( // Convierte el 'combine' (Flow) en un 'StateFlow'
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), // Mantiene el flujo activo 5s
        initialValue = HomeState() // Estado inicial mientras carga
    )

    // --- ACCIONES DEL USUARIO (Eventos) ---

    /** Actualiza el estado de búsqueda (lo que dispara el 'combine'). */

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    /** Actualiza el estado de categoría (lo que dispara el 'combine'). */

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }
}