package com.example.foodhub.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhubtest.data.local.entities.Product
import com.example.foodhubtest.data.repository.FoodRepository
import com.example.foodhubtest.domain.models.ProductForm
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de Administración (CRUD de Productos).
 * Recibe 'repo' (FoodRepository) por inyección de dependencias para
 * acceder a la capa de datos.
 */
class AdminVM(private val repo: FoodRepository) : ViewModel() {

        // --- ESTADO 1: LISTA DE PRODUCTOS ---
        // Expone un 'StateFlow' (flujo de estado) que se actualiza automáticamente
        // desde la base de datos (a través del repositorio).
        // La UI "observará" este 'products' para mostrar la lista.

    val products: StateFlow<List<Product>> = repo.products()
        .stateIn( // Convierte un Flow (frío) en un StateFlow (caliente)
            scope = viewModelScope, // Se ata al ciclo de vida del ViewModel
            started = SharingStarted.WhileSubscribed(5000), // Mantiene el flujo activo 5s después de que la UI deje de observar
            initialValue = emptyList() // Valor inicial mientras carga
        )

        // --- ESTADO 2: FORMULARIO (Crear/Editar) ---
    private val _formState = MutableStateFlow(ProductForm())
    val formState = _formState.asStateFlow()

        // --- ESTADO 3: DIÁLOGO DE BORRADO ---
        // Mantiene el producto que se quiere borrar. Si es 'null', el diálogo está oculto.
    private val _productToDelete = MutableStateFlow<Product?>(null)
    val productToDelete = _productToDelete.asStateFlow()

        /**
         * Actualiza el estado del formulario cada vez que el usuario modifica un campo en la UI.
         */
    fun onFormChange(form: ProductForm) {
        _formState.value = form
    }

        // --- LÓGICA DE BORRADO ---

        /** Muestra el diálogo de confirmación seteando el producto a borrar. */
    fun onDeleteTriggered(product: Product) {
        _productToDelete.value = product
    }

        /** * Confirma el borrado. Lanza una corutina para borrar de la BD
         * y luego oculta el diálogo (poniendo el estado en null).
         */
    fun onDeleteConfirmed() {
        _productToDelete.value?.let { product ->
            viewModelScope.launch {
                repo.delete(product)
                _productToDelete.value = null // Oculta el diálogo
            }
        }
    }

        /** Oculta el diálogo de borrado sin borrar el producto. */
    fun onDeleteCancelled() {
        _productToDelete.value = null
    }

        // --- LÓGICA DE EDICIÓN ---

        /**
         * Carga un producto existente en el formulario para editarlo.
         * Busca el producto por ID en la BD y actualiza el '_formState'.
         */
    fun loadProductForEdit(productId: Long) {
        viewModelScope.launch {
            val product = repo.getProduct(productId)
            if (product != null) {
                // Actualiza el 'formState' con los datos del producto
                _formState.value = ProductForm(
                    id = product.id,
                    name = product.name,
                    price = product.price.toString(), // Convierte a String para el formulario
                    stock = product.stock.toString(), // Convierte a String para el formulario
                    category = product.category,
                    available = product.available,
                    photoUri = product.photoUri
                )
            }
        }
    }

        // --- LÓGICA DE GUARDADO ---

        /**
         * Guarda un producto nuevo (INSERT) o actualiza uno existente (UPDATE).
         * Se ejecuta en una corutina.
         */
    fun saveOrUpdateProduct(onSuccess: () -> Unit) {
        viewModelScope.launch {
            // 1. Valida el estado actual del formulario
            if (_formState.value.isValid) {
                // 2. Convierte el formulario (String) a entidad (Double, Int)
                val productEntity = _formState.value.toEntity()

                // 3. Decide si insertar (ID=0) o actualizar (ID != 0)
                if (productEntity.id == 0L) {
                    repo.insert(productEntity)
                } else {
                    repo.update(productEntity)
                }

                // 4. Llama al callback (ej. para navegar) y limpia el formulario
                onSuccess()
                clearForm()
            }
            // (Si no es válido, no hace nada, la UI debería mostrar los errores)
        }
    }

        /** Resetea el formulario a su estado vacío por defecto. */
    fun clearForm() {
        _formState.value = ProductForm()
    }
}
