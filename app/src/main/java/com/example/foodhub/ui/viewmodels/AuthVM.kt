package com.example.foodhub.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub.core.utils.Validators
import com.example.foodhub.data.local.entities.User
import com.example.foodhub.data.repository.FoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 1. Definimos las clases de estado AQUÍ para que sean visibles
data class AuthFormState(
    val name: String = "",
    val email: String = "",
    val pass: String = "",
    val role: String = "CLIENT", // Roles: CLIENT, ADMIN
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

// 2. La clase ViewModel completa
class AuthVM(
    private val repo: FoodRepository,
    private val sessionVM: SessionVM
) : ViewModel() {

    private val _state = MutableStateFlow(AuthScreenState())
    val state = _state.asStateFlow()

    // --- FUNCIONES DE CAMBIO DE TEXTO (VALIDACIÓN) ---

    fun onNameChange(text: String) {
        val error = Validators.name(text)?.message
        _state.update { it.copy(
            form = it.form.copy(name = text, nameError = error),
            generalError = null
        )}
    }

    fun onEmailChange(text: String) {
        val error = Validators.email(text)?.message
        _state.update { it.copy(
            form = it.form.copy(email = text, emailError = error),
            generalError = null
        )}
    }

    fun onPasswordChange(text: String) {
        val error = Validators.password(text)?.message
        _state.update { it.copy(
            form = it.form.copy(pass = text, passError = error),
            generalError = null
        )}
    }

    fun onRoleChange(text: String) {
        _state.update { it.copy(form = it.form.copy(role = text)) }
    }

    // --- ACCIONES ---

    fun login() {
        val form = _state.value.form
        // Validar antes de enviar
        val emailError = Validators.email(form.email)?.message
        val passError = Validators.password(form.pass)?.message

        if (emailError != null || passError != null) {
            _state.update { it.copy(
                form = form.copy(emailError = emailError, passError = passError)
            )}
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, generalError = null) }

            // Llamada al repo
            val user = repo.login(form.email.trim(), form.pass)

            if (user != null) {
                // Verificar rol si es necesario, aquí lo dejamos pasar
                sessionVM.onLoginSuccess(user)
                _state.update { it.copy(isLoading = false, loginSuccess = true) }
            } else {
                _state.update { it.copy(isLoading = false, generalError = "Credenciales incorrectas") }
            }
        }
    }

    fun register() {
        val form = _state.value.form
        // Validar todo
        val nameError = Validators.name(form.name)?.message
        val emailError = Validators.email(form.email)?.message
        val passError = Validators.password(form.pass)?.message

        if (nameError != null || emailError != null || passError != null) {
            _state.update { it.copy(
                form = form.copy(nameError = nameError, emailError = emailError, passError = passError)
            )}
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, generalError = null) }

            val newUser = User(
                name = form.name.trim(),
                email = form.email.trim(),
                password = form.pass,
                role = form.role
            )

            // Llamada al repo
            val res = repo.register(newUser)

            if (res != null) {
                _state.update { it.copy(isLoading = false, registrationSuccess = true) }
            } else {
                _state.update { it.copy(isLoading = false, generalError = "Error al registrar (¿correo duplicado?)") }
            }
        }
    }

    fun onNavigationDone() {
        _state.update { it.copy(loginSuccess = false, registrationSuccess = false) }
    }
}