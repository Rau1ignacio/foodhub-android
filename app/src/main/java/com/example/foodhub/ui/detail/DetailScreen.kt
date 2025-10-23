package com.example.foodhub.ui.detail

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.foodhub.data.local.entities.Product
import com.example.foodhub.data.repository.FoodRepository
import com.example.foodhub.ui.viewmodels.CartVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    repo: FoodRepository, // Necesario para buscar el producto por ID
    cartVM: CartVM, // Necesario para la acción "Añadir al carrito"
    id: Long, // El ID del producto a mostrar
    onBack: () -> Unit // Callback de navegación
) {
    // Estado local para guardar el producto
    var product by remember { mutableStateOf<Product?>(null) }

    // Efecto que busca el producto en la BD cuando la pantalla se abre
    LaunchedEffect(id) {
        product = repo.getProduct(id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product?.name ?: "Cargando...") },
                navigationIcon = {
                    IconButton(onClick = onBack) { // Botón para volver
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        // Muestra la info solo cuando 'product' no es nulo
        product?.let { p ->
            var added by remember { mutableStateOf(false) } // Estado para la animación
            val context = LocalContext.current

            Column(Modifier.padding(padding).padding(16.dp)) {
                Text(p.name, style = MaterialTheme.typography.headlineSmall)
                Text("Categoría: ${p.category}")
                Text("$${p.price}")
                Spacer(Modifier.height(12.dp))

                // Botón "Añadir al carrito"
                Button(onClick = {
                    cartVM.addToCart(p.id) // Llama al VM del carrito
                    added = true // Activa la animación
                    vibrateOnce(context) // Feedback háptico
                }) { Text("Añadir al carrito") }

                // Animación simple de confirmación
                AnimatedVisibility(visible = added) {
                    Text("¡Añadido!", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

// --- FUNCIÓN DE UTILIDAD (Vibración) ---

// funcion NO PROBADA SU FUNCIONAMIENTOOOOOO

// (Maneja la compatibilidad de versiones de Android para vibrar)
private fun vibrateOnce(ctx: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = ctx.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(50)
    }
}