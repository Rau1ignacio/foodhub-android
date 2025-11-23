package com.example.foodhub.ui.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.foodhub.ui.viewmodels.AdminVM
import java.io.File
import java.util.Objects

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductFormScreen(
    vm: AdminVM,
    productId: Long?,
    navBack: () -> Unit
) {
    val formState by vm.formState.collectAsState()
    val errors = formState.isValid
    val context = LocalContext.current

    val nameError = if (formState.name.isBlank()) "Requerido" else null
    val priceError = if (formState.price.toIntOrNull() == null) "Inválido" else null

    LaunchedEffect(productId) {
        if (productId != null && productId != 0L) vm.loadProductForEdit(productId) else vm.clearForm()
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            vm.formState.value.let { currentState ->
                vm.onFormChange(currentState.copy(imageUrl = currentState.imageUrl))
            }
        }
    }

    var tempUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher de Galería
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { vm.onFormChange(formState.copy(imageUrl = it.toString())) }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())
    ) {
        Text(if (formState.id == 0L) "Nuevo Producto" else "Editar Producto", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = formState.name,
            onValueChange = { vm.onFormChange(formState.copy(name = it)) },
            label = { Text("Nombre") },
            isError = nameError != null,
            modifier = Modifier.fillMaxWidth()
        )
        if (nameError != null) Text(nameError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = formState.price,
            onValueChange = { vm.onFormChange(formState.copy(price = it)) },
            label = { Text("Precio") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = formState.stock,
            onValueChange = { vm.onFormChange(formState.copy(stock = it)) },
            label = { Text("Stock") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) { Text("Galería") }
            Button(onClick = {
                val photoFile = File.createTempFile("img_", ".jpg", context.cacheDir)
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", photoFile)
                tempUri = uri
                // Actualizamos el estado con la URI temporal antes de abrir cámara
                vm.onFormChange(formState.copy(imageUrl = uri.toString()))
                cameraLauncher.launch(uri)
            }) { Text("Cámara") }
        }

        Spacer(Modifier.height(32.dp))

        Button(
            enabled = formState.isValid,
            onClick = { vm.saveOrUpdateProduct(onSuccess = navBack) },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) { Text("Guardar") }
    }
}