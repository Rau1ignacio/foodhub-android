package com.example.foodhub.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub.data.local.entities.Product
import com.example.foodhub.data.repository.FoodRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProductFormState(
    val id: Long = 0,
    val name: String = "",
    val price: String = "",
    val stock: String = "",
    val category: String = "Otros",
    val available: Boolean = true,
    val imageUrl: String = ""
) {
    val isValid: Boolean get() = name.isNotBlank() && price.toIntOrNull() != null
}

class AdminVM(private val repo: FoodRepository) : ViewModel() {

    // CORREGIDO: 'products' es una variable (val), no una función. Quitamos los ()
    val products = repo.products.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _formState = MutableStateFlow(ProductFormState())
    val formState = _formState.asStateFlow()

    private val _productToDelete = MutableStateFlow<Product?>(null)
    val productToDelete = _productToDelete.asStateFlow()

    fun onFormChange(s: ProductFormState) { _formState.value = s }

    fun clearForm() { _formState.value = ProductFormState() }

    fun loadProductForEdit(id: Long) {
        if (id == 0L) {
            clearForm()
            return
        }
        viewModelScope.launch {
            // CORREGIDO: El método en el repo se llama 'getProductById'
            repo.getProductById(id)?.let { p ->
                _formState.value = ProductFormState(
                    p.id, p.name, p.price.toString(),
                    p.stock.toString(), p.category, p.available, p.imageUrl ?: ""
                )
            }
        }
    }

    fun saveOrUpdateProduct(onSuccess: () -> Unit) {
        val c = _formState.value
        if (!c.isValid) return

        viewModelScope.launch {
            val p = Product(
                id = c.id,
                name = c.name,
                price = c.price.toInt(),
                description = "", // Ojo: Agrega campo descripción si lo necesitas en el form
                stock = c.stock.toIntOrNull() ?: 0,
                category = c.category,
                available = c.available,
                imageUrl = c.imageUrl
            )

            // CORREGIDO: Usamos saveProduct que decide si es crear o actualizar
            repo.saveProduct(p)

            onSuccess()
            clearForm()
        }
    }

    fun onDeleteTriggered(p: Product) { _productToDelete.value = p }

    fun onDeleteConfirmed() {
        _productToDelete.value?.let { p ->
            viewModelScope.launch {
                // CORREGIDO: El método en el repo se llama 'deleteProduct'
                repo.deleteProduct(p)
                _productToDelete.value = null
            }
        }
    }

    fun onDeleteCancelled() { _productToDelete.value = null }
}