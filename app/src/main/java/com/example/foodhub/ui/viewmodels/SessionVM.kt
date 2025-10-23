package com.example.foodhub.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.foodhubtest.data.local.entities.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Estado: Contiene el usuario que ha iniciado sesión. */
data class SessionState(
    val loggedInUser: User? = null // null = sesión cerrada
)

/**
 * ViewModel de Sesión. Creado a un nivel superior y compartido.
 * Mantiene el estado de la sesión (quién está logueado) para toda la app.
 */
class SessionVM : ViewModel() {
    private val _state = MutableStateFlow(SessionState())
    val state = _state.asStateFlow()

    /** Evento: Llamado por AuthVM cuando el login es exitoso. */
    fun onLoginSuccess(user: User) {
        _state.update { it.copy(loggedInUser = user) }
    }

    /** Evento: Llamado al cerrar sesión. */
    fun onLogout() {
        _state.update { it.copy(loggedInUser = null) }
    }
}