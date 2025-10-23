package com.example.foodhub.ui.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.foodhub.ui.viewmodels.CartVM

/**
 * Las cosas que nos faltan agregar en el carrito es que el cleinte puede editar las cantidades
 * a pedir antes de confirmar el pedido.
 *
 */

@Composable
fun CartScreen(
    vm: CartVM, // Recibe el ViewModel del carrito
    onConfirmOrder: (Long) -> Unit // Callback para navegar al Resumen de Orden
) {
    // Observa el estado combinado (productos + total) del VM
    val state by vm.cartState.collectAsState()

    if (state.items.isEmpty()) {
        // --- ESTADO VACÍO ---
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Tu carrito está vacío")
        }
    } else {
        // --- ESTADO CON ITEMS ---
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // Lista de items en el carrito
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.items, key = { it.first.id }) { (product, cartItem) ->
                    ListItem(
                        headlineContent = { Text(product.name) },
                        supportingContent = { Text("Cantidad: ${cartItem.quantity}") },
                        trailingContent = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Muestra el subtotal (precio * cantidad)
                                Text("$${product.price * cartItem.quantity}")
                                // Botón Eliminar: Llama al VM
                                IconButton(onClick = { vm.removeFromCart(cartItem) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                }
                            }
                        }
                    )
                    Divider()
                }
            }

            // --- RESUMEN (Total y Botón) ---
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Total: $${state.total}", // Muestra el total calculado por el VM
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.End)
            )
            Spacer(Modifier.height(16.dp))
            Button(
                // Llama al VM para confirmar. El VM limpiará el carrito
                // y llamará al callback 'onConfirmOrder' con el nuevo ID.
                onClick = { vm.confirmOrder(onSuccess = onConfirmOrder) },
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Confirmar Pedido")
            }
        }
    }
}