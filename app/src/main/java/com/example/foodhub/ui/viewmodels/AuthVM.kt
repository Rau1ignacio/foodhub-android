package com.example.foodhub.ui.viewmodels

package com.example.foodhubtest.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhubtest.core.utils.Validators
import com.example.foodhubtest.data.local.entities.User
import com.example.foodhubtest.data.repository.FoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthFormState(
    val name: String = "",
    val email: String = "",
    val pass: String = "",
    val role: String = "CLIENT",
    val nameError: String? = null,
    val emailError: String? = null,
    val passError: String? = null
)

data class AuthScreenState(
    val form: AuthFormState = AuthFormState(),
    val isLoading: Boolean = false,
    val generalError: String? = null,
    val loginSuccess: Boolean = false,
    val registrationSuccess: Boolean = false
)

class AuthVM(
    private val repo: FoodRepository,
    private val sessionVM: SessionVM
) : ViewModel() {
    private val _state = MutableStateFlow(AuthScreenState())
    val state = _state.asStateFlow()

    fun onFormChange(form: AuthFormState) {
        _state.update { it.copy(form = form, generalError = null) }
    }

    fun login() {
        val form = _state.value.form
        val emailError = Validators.email(form.email)
        val passError = Validators.password(form.pass)

        _state.update { it.copy(form = form.copy(emailError = emailError?.message, passError = passError?.message)) }

        if (emailError != null || passError != null) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, generalError = null) }
            val user = repo.findUserByEmail(form.email.trim())

            if (user == null || user.passwordHash != form.pass || user.role != form.role) {
                _state.update { it.copy(isLoading = false, generalError = "Credenciales incorrectas para el rol seleccionado.") }
            } else {
                sessionVM.onLoginSuccess(user)
                _state.update { it.copy(isLoading = false, loginSuccess = true) }
            }
        }
    }

    fun register() {
        val form = _state.value.form

        // --- VALIDACIONES AÑADIDAS ---
        val nameError = Validators.name(form.name)
        val emailError = Validators.email(form.email)
        val passError = Validators.password(form.pass)

        // Actualiza el estado de la UI con los posibles errores
        _state.update {
            it.copy(
                form = form.copy(
                    nameError = nameError?.message,
                    emailError = emailError?.message,
                    passError = passError?.message
                )
            )
        }

        // Si hay algún error, no continúa con el registro
        if (nameError != null || emailError != null || passError != null) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, generalError = null) }
            if (repo.findUserByEmail(form.email.trim()) != null) {
                _state.update { it.copy(isLoading = false, generalError = "El correo ya está registrado") }
            } else {
                val newUser = User(name = form.name.trim(), email = form.email.trim(), passwordHash = form.pass, role = form.role)
                repo.registerUser(newUser)
                _state.update { it.copy(isLoading = false, registrationSuccess = true) }
            }
        }
    }

    fun onNavigationDone() {
        _state.update { it.copy(loginSuccess = false, registrationSuccess = false, generalError = null) }
    }
}