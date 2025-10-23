package com.example.foodhub.ui.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.foodhub.ui.viewmodels.AdminVM
import java.io.File
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductFormScreen(
    vm: AdminVM,
    productId: Long?, // ID del producto (nulo si es "Crear", con valor si es "Editar")
    navBack: () -> Unit // Callback para volver atrás al guardar
) {
    // Observa el estado del formulario desde el VM
    val formState by vm.formState.collectAsState()
    val errors = formState.validate() // Valida el estado actual (en el Composable, aunque idealmente se hace en el VM)
    val context = LocalContext.current
    var tempImageUri by remember { mutableStateOf<Uri?>(null) } // Almacen temporal para la foto de la cámara

    // --- EFECTO DE CARGA ---
    // Se ejecuta 1 vez. Carga el producto si 'productId' no es nulo.
    LaunchedEffect(productId) {
        if (productId != null && productId != 0L) {
            vm.loadProductForEdit(productId) // Modo Editar
        } else {
            vm.clearForm() // Modo Crear
        }
    }

    // --- LANZADORES DE ACTIVIDAD (Para Cámara y Galería) ---
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        // Cuando la cámara termina, actualiza el formState en el VM
        if (success) {
            tempImageUri?.let { vm.onFormChange(formState.copy(photoUri = it.toString())) }
        }
    }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        // Cuando la galería termina, actualiza el formState en el VM
        uri?.let { vm.onFormChange(formState.copy(photoUri = it.toString())) }
    }

    // --- CUERPO DEL FORMULARIO ---
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())
    ) {
        // Título dinámico
        Text(if (formState.id == 0L) "Nuevo Producto" else "Editar Producto", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        // --- CAMPO NOMBRE ---
        OutlinedTextField(
            value = formState.name,
            onValueChange = { vm.onFormChange(formState.copy(name = it)) }, // Notifica al VM
            label = { Text("Nombre") },
            isError = errors.containsKey("name"), // Muestra error si existe
            modifier = Modifier.fillMaxWidth()
        )
        // Muestra el mensaje de error
        errors["name"]?.let { Text(it.message, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
        Spacer(Modifier.height(8.dp))

        // --- CAMPO PRECIO ---
        OutlinedTextField(
            value = formState.price,
            onValueChange = { vm.onFormChange(formState.copy(price = it)) }, // Notifica al VM
            label = { Text("Precio (CLP)") },
            isError = errors.containsKey("price"),
            modifier = Modifier.fillMaxWidth()
        )
        errors["price"]?.let { Text(it.message, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
        Spacer(Modifier.height(8.dp))

        // --- CAMPO STOCK --- (Similar a los anteriores)
        OutlinedTextField(
            value = formState.stock,
            onValueChange = { vm.onFormChange(formState.copy(stock = it)) },
            label = { Text("Stock") },
            isError = errors.containsKey("stock"),
            modifier = Modifier.fillMaxWidth()
        )
        errors["stock"]?.let { Text(it.message, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
        Spacer(Modifier.height(8.dp))

        // --- CAMPO CATEGORÍA (Menú desplegable) ---
        val categories = listOf("Frutas", "Verduras", "Lácteos", "Carnes", "Otros")
        var isCategoryMenuExpanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = isCategoryMenuExpanded,
            onExpandedChange = { isCategoryMenuExpanded = it }
        ) {
            OutlinedTextField( // Campo de texto (falso) que muestra la selección
                value = formState.category,
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoría") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryMenuExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu( // El menú real
                expanded = isCategoryMenuExpanded,
                onDismissRequest = { isCategoryMenuExpanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            vm.onFormChange(formState.copy(category = category)) // Notifica al VM
                            isCategoryMenuExpanded = false // Cierra el menú
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        // --- SWITCH DISPONIBILIDAD ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(
                checked = formState.available,
                onCheckedChange = { vm.onFormChange(formState.copy(available = it)) } // Notifica al VM
            )
            Spacer(Modifier.width(8.dp))
            Text("Disponible para la venta")
        }
        Spacer(Modifier.height(16.dp))

        // --- BOTONES DE IMAGEN ---
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { // Botón Galería
                galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }) { Text("Elegir Foto") }

            Button(onClick = { // Botón Cámara
                // Crea una URI temporal para guardar la foto
                val uri = FileProvider.getUriForFile(
                    Objects.requireNonNull(context),
                    "com.example.foodhubtest.provider", // Debe coincidir con el Manifest
                    File.createTempFile("camera_photo_", ".jpg", context.cacheDir)
                )
                tempImageUri = uri
                cameraLauncher.launch(uri) // Lanza la cámara
            }) { Text("Tomar Foto") }
        }

        // ... (Espaciador y botón de guardar)
        Spacer(Modifier.weight(1f)) // Empuja el botón al fondo

        // --- BOTÓN GUARDAR ---
        Button(
            enabled = formState.isValid, // Se activa solo si el formulario es válido
            onClick = { vm.saveOrUpdateProduct(onSuccess = navBack) }, // Llama al VM para guardar
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Guardar Producto")
        }
    }
}