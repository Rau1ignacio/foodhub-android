package com.example.foodhub.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foodhubtest.data.repository.FoodRepository

/**
 * Fábrica simple. Sabe cómo crear ViewModels que
 * SÓLO necesitan el 'FoodRepository'.
 */
class ViewModelFactory(private val repo: FoodRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            // Si piden un AdminVM, lo creo con 'repo'
            modelClass.isAssignableFrom(AdminVM::class.java) -> AdminVM(repo) as T

            // Si piden un HomeVM, lo creo con 'repo'
            modelClass.isAssignableFrom(HomeVM::class.java) -> HomeVM(repo) as T

            // Si piden uno que no conozco, lanza error
            else -> throw IllegalArgumentException("Unknown ViewModel class provided to ViewModelFactory: ${modelClass.name}")
        }
    }
}