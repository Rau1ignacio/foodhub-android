package com.example.foodhub.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.foodhub.ui.viewmodels.AdminVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminListScreen(
    vm: AdminVM, // Recibe el ViewModel que tiene la lógica
    onAddProduct: () -> Unit, // Callback para navegar a la pantalla de formulario (nuevo)
    onEditProduct: (Long) -> Unit // Callback para navegar al formulario (editar)
) {
    // Observa los estados del ViewModel
    val products by vm.products.collectAsState()
    val productToDelete by vm.productToDelete.collectAsState()

    // --- DIÁLOGO DE CONFIRMACIÓN PARA BORRAR ---
    // Se muestra solo cuando 'productToDelete' (en el VM) no es nulo.
    productToDelete?.let { product ->
        AlertDialog(
            onDismissRequest = { vm.onDeleteCancelled() }, // Oculta el diálogo
            title = { Text("Confirmar Borrado") },
            text = { Text("¿Estás seguro de que quieres eliminar '${product.name}'?") },
            confirmButton = {
                Button(
                    onClick = { vm.onDeleteConfirmed() }, // Llama a la lógica de borrado en el VM
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                OutlinedButton(onClick = { vm.onDeleteCancelled() }) { Text("Cancelar") }
            }
        )
    }

    // --- ESTRUCTURA DE LA PANTALLA ---
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Gestión de Productos") }) // Barra superior
        },
        floatingActionButton = {
            // Botón flotante para añadir productos
            FloatingActionButton(onClick = onAddProduct) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Nuevo Producto")
            }
        }
    ) { padding ->
        // --- CUERPO DE LA PANTALLA ---
        if (products.isEmpty()) {
            // Mensaje de lista vacía
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No hay productos. ¡Añade uno nuevo!")
            }
        } else {
            // Lista de productos
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products, key = { it.id }) { product ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        ListItem(
                            headlineContent = { Text(product.name) },
                            supportingContent = { Text("Stock: ${product.stock} | Precio: $${product.price}") },
                            trailingContent = {
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    // Botón Editar: Llama al callback de navegación
                                    IconButton(onClick = { onEditProduct(product.id) }) {
                                        Icon(Icons.Default.Edit, "Editar Producto")
                                    }
                                    // Botón Borrar: Llama al VM para mostrar el diálogo
                                    IconButton(onClick = { vm.onDeleteTriggered(product) }) {
                                        Icon(Icons.Default.Delete, "Borrar Producto", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}