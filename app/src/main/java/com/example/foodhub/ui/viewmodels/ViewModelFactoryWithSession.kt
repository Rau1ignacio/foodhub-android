package com.example.foodhub.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foodhubtest.data.repository.FoodRepository

/**
 * Fábrica especializada que sabe cómo inyectar el 'repo' Y el 'sessionVM'
 * a los ViewModels que los requieran.
 */
class ViewModelFactoryWithSession(
    private val repo: FoodRepository,
    private val sessionVM: SessionVM // La dependencia compartida
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            // Si piden un AuthVM, lo creo con 'repo' y 'sessionVM'
            modelClass.isAssignableFrom(AuthVM::class.java) -> AuthVM(repo, sessionVM) as T

            // Si piden un OrderHistoryVM, lo creo con 'repo' y 'sessionVM'
            modelClass.isAssignableFrom(OrderHistoryVM::class.java) -> OrderHistoryVM(repo, sessionVM) as T

            // Si piden un CartVM, lo creo con 'repo' y 'sessionVM'
            modelClass.isAssignableFrom(CartVM::class.java) -> CartVM(repo, sessionVM) as T

            // Si piden otro VM (que no necesita SessionVM), uso la otra fábrica
            else -> ViewModelFactory(repo).create(modelClass)
        }
    }
}