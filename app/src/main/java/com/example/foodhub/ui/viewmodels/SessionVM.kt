package com.example.foodhub.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.foodhub.data.local.entities.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SessionState(
    val loggedInUser: User? = null
)

class SessionVM : ViewModel() {

    private val _state = MutableStateFlow(SessionState())
    val state = _state.asStateFlow()

    fun onLoginSuccess(user: User) {
        _state.update { it.copy(loggedInUser = user) }
    }

    fun onLogout() {
        _state.update { it.copy(loggedInUser = null) }
    }
}
