package com.example.foodhub.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub.data.local.entities.User
import com.example.foodhub.core.utils.Validators
import com.example.foodhub.data.repository.FoodRepository
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

class AuthVM(private val repo: FoodRepository, private val sessionVM: SessionVM) : ViewModel() {

    private val _state = MutableStateFlow(AuthScreenState())
    val state = _state.asStateFlow()

    // --- VALIDACIÓN EN TIEMPO REAL ---

    fun onNameChange(text: String) {
        // 1. Validamos inmediatamente lo que el usuario escribe
        val error = Validators.name(text)?.message
        // 2. Actualizamos el estado con el texto Y el error (si existe)
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

    // --- ACCIONES DE BOTONES ---

    fun login() {
        val form = _state.value.form

        // Validación final por si acaso el usuario apretó el botón muy rápido
        // o si los campos estaban vacíos desde el inicio.
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

            val user = repo.login(form.email.trim(), form.pass)

            if (user != null) {
                if (user.role != form.role) {
                    _state.update { it.copy(isLoading = false, generalError = "El usuario no es ${form.role}") }
                } else {
                    sessionVM.onLoginSuccess(user)
                    _state.update { it.copy(isLoading = false, loginSuccess = true) }
                }
            } else {
                _state.update { it.copy(isLoading = false, generalError = "Credenciales incorrectas") }
            }
        }
    }

    fun register() {
        val form = _state.value.form

        // Validación final de todos los campos
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

            val res = repo.registerUser(newUser)

            if (res != null) {
                _state.update { it.copy(isLoading = false, registrationSuccess = true) }
            } else {
                _state.update { it.copy(isLoading = false, generalError = "El correo ya existe") }
            }
        }
    }

    fun onNavigationDone() {
        _state.update { it.copy(loginSuccess = false, registrationSuccess = false) }
    }
}