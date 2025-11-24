package com.example.foodhub.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.foodhub.data.local.entities.Product
import com.example.foodhub.ui.viewmodels.AdminVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductFormScreen(
    vm: AdminVM,
    productId: Long?,
    navBack: () -> Unit
) {
    val categories = listOf("Frutas", "Verduras", "Lácteos", "Bebidas", "Otros")

    // Producto existente si estamos editando
    val existing = remember(productId) {
        vm.getProductById(productId)
    }

    var name by remember { mutableStateOf(existing?.name ?: "") }
    var description by remember { mutableStateOf(existing?.description ?: "") }
    var imageUrl by remember { mutableStateOf(existing?.imageUrl ?: "") }
    var priceText by remember { mutableStateOf(existing?.price?.toString() ?: "") }
    var stockText by remember { mutableStateOf(existing?.stock?.toString() ?: "") }
    var category by remember { mutableStateOf(existing?.category ?: categories.first()) }

    // Control del menú de categorías
    var categoryMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (productId == null || productId == 0L)
                            "Nuevo producto"
                        else
                            "Editar producto"
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Nombre
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            // Descripción
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )

            // URL imagen
            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("URL de imagen") },
                modifier = Modifier.fillMaxWidth()
            )

            // Precio (filtramos a dígitos, sin teclado especial)
            OutlinedTextField(
                value = priceText,
                onValueChange = { priceText = it.filter { ch -> ch.isDigit() } },
                label = { Text("Precio") },
                modifier = Modifier.fillMaxWidth()
            )

            // Stock (filtramos a dígitos, sin teclado especial)
            OutlinedTextField(
                value = stockText,
                onValueChange = { stockText = it.filter { ch -> ch.isDigit() } },
                label = { Text("Stock") },
                modifier = Modifier.fillMaxWidth()
            )

            // Selector de categoría (Dropdown normal)
            Box {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Seleccionar categoría"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { categoryMenuExpanded = true }
                )

                DropdownMenu(
                    expanded = categoryMenuExpanded,
                    onDismissRequest = { categoryMenuExpanded = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                category = cat
                                categoryMenuExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Guardar
            Button(
                onClick = {
                    val price = priceText.toIntOrNull() ?: 0
                    val stock = stockText.toIntOrNull() ?: 0

                    val product = Product(
                        id = existing?.id ?: 0L,
                        name = name,
                        description = description,
                        price = price,
                        imageUrl = imageUrl,
                        category = category,
                        stock = stock
                    )

                    if (existing == null) {
                        vm.createProduct(product)
                    } else {
                        vm.updateProduct(product)
                    }

                    navBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && priceText.isNotBlank()
            ) {
                Text("Guardar")
            }
        }
    }
}
