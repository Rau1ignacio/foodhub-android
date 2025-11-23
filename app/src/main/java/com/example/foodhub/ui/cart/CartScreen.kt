package com.example.foodhub.ui.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodhub.ui.viewmodels.CartVM

@Composable
fun CartScreen(
    vm: CartVM,
    onOrderConfirmed: (Long) -> Unit
) {
    val state by vm.cartState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Tu Carrito", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        if (state.items.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                Text("El carrito está vacío", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.items) { (product, item) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.name, fontWeight = FontWeight.Bold)
                            Text("${item.quantity} x $${product.price} = $${item.quantity * product.price}")
                        }
                        IconButton(onClick = { vm.removeFromCart(item) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                        }
                    }
                    Divider()
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- RESUMEN Y BOTÓN ---
            Text("Total: $${state.total}", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    vm.confirmOrder(onOrderConfirmed = onOrderConfirmed)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.items.isNotEmpty()
            ) {
                Text("Confirmar Compra")
            }
        }
    }
}