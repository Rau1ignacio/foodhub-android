package com.example.foodhub.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.foodhub.data.repository.FoodRepository
import com.example.foodhub.ui.viewmodels.HomeVM
import com.example.foodhub.ui.viewmodels.ViewModelFactory

@Composable
fun HomeScreen(repo: FoodRepository, onProductClick: (Long) -> Unit) {
    // Usamos el Factory para crear el ViewModel
    val vm: HomeVM = viewModel(factory = ViewModelFactory(repo))
    val state by vm.state.collectAsState()

    val categories = listOf("Todos", "Frutas", "Verduras", "Lácteos", "Carnes", "Otros")

    Column(Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { vm.onSearchChange(it) }, // Llamada correcta
            label = { Text("Buscar...") },
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        )

        LazyRow(modifier = Modifier.padding(8.dp)) {
            items(categories) { category ->
                FilterChip(
                    selected = state.selectedCategory == category,
                    onClick = { vm.onCategoryChange(category) },
                    label = { Text(category) },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        if (state.isLoading) LinearProgressIndicator(Modifier.fillMaxWidth())

        LazyColumn {
            items(state.products) { p ->
                ListItem(
                    headlineContent = { Text(p.name) },
                    supportingContent = { Text("$${p.price}") },
                    leadingContent = {
                        if (p.imageUrl?.isNotEmpty() == true) {
                            AsyncImage(model = p.imageUrl, contentDescription = null, modifier = Modifier.size(50.dp))
                        }
                    },
                    modifier = Modifier.clickable { onProductClick(p.id) }
                )
            }
        }
    }
}