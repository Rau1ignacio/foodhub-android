package com.example.foodhub.ui.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
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
 * Pantalla de carrito:
 * - Muestra los productos añadidos
 * - Permite modificar cantidades (botones + y -)
 * - Muestra total
 * - Botón para confirmar compra
 */
@Composable
fun CartScreen(
    vm: CartVM,
    onOrderConfirmed: () -> Unit
) {
    val state by vm.state.collectAsState()

    Scaffold(
        bottomBar = {
            if (state.items.isNotEmpty()) {
                CartBottomBar(
                    total = state.total,
                    onConfirm = {
                        vm.confirmOrder(onOrderConfirmed)
                    }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Carrito",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tu carrito está vacío.")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.items) { item ->
                        CartItemRow(
                            name = item.product.name,
                            price = item.product.price,
                            quantity = item.quantity,
                            stock = item.product.stock,
                            onIncrease = { vm.changeQuantity(item.id, item.quantity + 1) },
                            onDecrease = { vm.changeQuantity(item.id, item.quantity - 1) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(
    name: String,
    price: Int,
    quantity: Int,
    stock: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(name, fontWeight = FontWeight.SemiBold)
                Text("Precio: $$price")
                Text("Stock disponible: $stock")
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = onDecrease,
                    enabled = quantity > 1
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Disminuir")
                }
                Text(quantity.toString())
                IconButton(
                    onClick = onIncrease,
                    enabled = quantity < stock
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Aumentar")
                }
            }
        }
    }
}

@Composable
private fun CartBottomBar(
    total: Int,
    onConfirm: () -> Unit
) {
    Surface(
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Total", fontWeight = FontWeight.SemiBold)
                Text("$ $total")
            }
            Button(onClick = onConfirm) {
                Text("Confirmar compra")
            }
        }
    }
}
