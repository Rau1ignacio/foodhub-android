package com.example.foodhub.ui.admin

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.foodhub.data.local.entities.Product
import com.example.foodhub.ui.viewmodels.AdminVM
import java.io.File

// --- Helper para crear URI de la cámara ---
private fun createImageUri(context: Context): Uri {
    val imagesDir = File(context.cacheDir, "images")
    if (!imagesDir.exists()) imagesDir.mkdirs()

    val imageFile = File(imagesDir, "img_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider", // com.example.foodhub.provider
        imageFile
    )
}

// Opción de categoría: lo que ve el usuario vs lo que va al backend/MySQL
data class CategoryOption(
    val label: String,       // Texto visible en la UI
    val backendValue: String // Valor que se guarda: FRUTAS, VERDURAS, etc.
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductFormScreen(
    vm: AdminVM,
    productId: Long?,
    navBack: () -> Unit
) {
    val context = LocalContext.current

    // Debe coincidir con tu ENUM / valores del backend
    val categories = listOf(
        CategoryOption("Frutas", "FRUTAS"),
        CategoryOption("Verduras", "VERDURAS"),
        CategoryOption("Lácteos", "LACTEOS"),
        CategoryOption("Bebidas", "BEBIDAS"),
        CategoryOption("Otros", "OTROS")
    )

    // Si estamos editando, cargamos el producto desde el VM
    val existing = remember(productId) { vm.getProductById(productId) }

    var name by remember { mutableStateOf(existing?.name ?: "") }
    var description by remember { mutableStateOf(existing?.description ?: "") }
    var imageUrl by remember { mutableStateOf(existing?.imageUrl ?: "") }
    var priceText by remember { mutableStateOf(existing?.price?.toString() ?: "") }
    var stockText by remember { mutableStateOf(existing?.stock?.toString() ?: "") }

    // Categoría inicial: si coincide con la del backend, la selecciona; si no, FRUTAS
    var selectedCategory by remember {
        mutableStateOf(
            categories.firstOrNull { it.backendValue == existing?.category }
                ?: categories.first()
        )
    }

    var categoryExpanded by remember { mutableStateOf(false) }

    // --- Launchers para galería y cámara ---
    var lastCameraUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { picked ->
                imageUrl = picked.toString() // guardamos el content://… en el campo
            }
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                lastCameraUri?.let { uri ->
                    imageUrl = uri.toString()
                }
            }
        }

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

            // URL / URI imagen (se llenará sola cuando elijan foto)
            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("URL / URI de imagen") },
                modifier = Modifier.fillMaxWidth()
            )

            // Botones para elegir imagen
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Desde galería")
                }
                OutlinedButton(
                    onClick = {
                        val uri = createImageUri(context)
                        lastCameraUri = uri
                        cameraLauncher.launch(uri)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Tomar foto")
                }
            }

            // Precio (solo números)
            OutlinedTextField(
                value = priceText,
                onValueChange = { priceText = it.filter { ch -> ch.isDigit() } },
                label = { Text("Precio") },
                modifier = Modifier.fillMaxWidth()
            )

            // Stock (solo números)
            OutlinedTextField(
                value = stockText,
                onValueChange = { stockText = it.filter { ch -> ch.isDigit() } },
                label = { Text("Stock") },
                modifier = Modifier.fillMaxWidth()
            )

            // --- Selector de categoría con ExposedDropdownMenuBox ---
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = selectedCategory.label, // lo que ve el usuario
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                DropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.label) },
                            onClick = {
                                selectedCategory = option   // aquí cambiamos realmente
                                categoryExpanded = false
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
                        category = selectedCategory.backendValue, // FRUTAS, VERDURAS, ...
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
