package com.example.foodhub.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foodhub.data.repository.FoodRepository

class ViewModelFactory(
    private val repo: FoodRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AdminVM::class.java) ->
                AdminVM(repo) as T

            modelClass.isAssignableFrom(HomeVM::class.java) ->
                HomeVM(repo) as T

            modelClass.isAssignableFrom(DetailVM::class.java) ->
                DetailVM(repo) as T

            else -> throw IllegalArgumentException(
                "Unknown ViewModel class: ${modelClass.name}"
            )
        }
    }
}