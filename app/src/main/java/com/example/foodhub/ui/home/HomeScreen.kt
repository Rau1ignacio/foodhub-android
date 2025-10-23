package com.example.foodhub.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.foodhub.data.repository.FoodRepository
import com.example.foodhub.ui.viewmodels.HomeVM
import com.example.foodhub.ui.viewmodels.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(repo: FoodRepository, onProductClick: (Long) -> Unit) {
    // Obtiene el VM (instanciado con su Factory)
    val vm: HomeVM = viewModel(factory = ViewModelFactory(repo))
    // Observa el estado del VM (query, categoría, lista filtrada)
    val state by vm.state.collectAsState()
    val categories = listOf("Todos", "Frutas", "Verduras", "Lácteos") // falta agregar | "Carnes", "Otros" | no los agregue por que se veia feo

    Column(Modifier.fillMaxSize()) {

        // --- BARRA DE BÚSQUEDA ---
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { vm.onSearchQueryChange(it) }, // Notifica al VM
            label = { Text("Buscar producto...") },
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        )

        // --- FILTROS DE CATEGORÍA ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                FilterChip(
                    selected = state.selectedCategory == category,
                    onClick = { vm.onCategorySelected(category) }, // Notifica al VM
                    label = { Text(category) }
                )
            }
        }

        // --- LISTA DE PRODUCTOS (Filtrada) ---
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            // Usa la lista 'state.products' que ya viene filtrada desde el VM
            items(state.products, key = { it.id }) { p ->
                ListItem(
                    headlineContent = { Text(p.name) },
                    supportingContent = { Text("$${p.price} · Stock: ${p.stock}") },
                    leadingContent = {
                        // Muestra la imagen (si existe) usando Coil
                        p.photoUri?.let { uri ->
                            AsyncImage(model = uri, contentDescription = null, modifier = Modifier.size(56.dp))
                        }
                    },
                    // Navega al detalle al hacer click
                    modifier = Modifier.clickable { onProductClick(p.id) }
                )
                Divider()
            }
        }
    }
}