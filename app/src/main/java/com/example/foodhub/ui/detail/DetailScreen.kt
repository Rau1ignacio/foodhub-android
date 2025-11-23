package com.example.foodhub.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.foodhub.ui.viewmodels.CartVM
import com.example.foodhub.ui.viewmodels.DetailVM

@Composable
fun DetailScreen(
    detailVM: DetailVM, // Recibe el VM de detalle
    cartVM: CartVM,     // Recibe el VM del carrito
    onBack: () -> Unit
) {
    val product by detailVM.product.collectAsState()

    // Si el producto es nulo (cargando o error), mostramos loading
    if (product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // Cuando ya tenemos el producto:
    val p = product!! // Forzamos desempaquetado porque ya validamos null arriba

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = onBack) {
            Text("Volver")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = p.name, style = MaterialTheme.typography.headlineMedium)
        Text(text = "$${p.price}", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = p.description ?: "Sin descripción")

        Spacer(modifier = Modifier.weight(1f)) // Empuja el botón al fondo

        Button(
            onClick = { cartVM.addToCart(p) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar al Carrito")
        }
    }
}