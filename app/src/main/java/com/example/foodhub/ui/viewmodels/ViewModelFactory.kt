package com.example.foodhub.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foodhub.data.repository.FoodRepository

class ViewModelFactory(private val repo: FoodRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            // Admin de productos
            modelClass.isAssignableFrom(AdminVM::class.java) ->
                AdminVM(repo) as T

            // Home (listado / búsqueda de productos)
            modelClass.isAssignableFrom(HomeVM::class.java) ->
                HomeVM(repo) as T

            // Detalle de producto
            modelClass.isAssignableFrom(DetailVM::class.java) ->
                DetailVM(repo) as T

            else -> throw IllegalArgumentException(
                "Unknown ViewModel class provided to ViewModelFactory: ${modelClass.name}"
            )
        }
    }
}
