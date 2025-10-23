package com.example.foodhub.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub.core.utils.Validators // Importa las reglas de validación
import com.example.foodhub.data.local.entities.User
import com.example.foodhub.data.repository.FoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Estado: Datos del formulario de autenticación y sus errores. */
data class AuthFormState(
    val name: String = "",
    val email: String = "",
    val pass: String = "",
    val role: String = "CLIENT", // Rol seleccionado
    val nameError: String? = null,
    val emailError: String? = null,
    val passError: String? = null
)

/** Estado: Estado completo de la pantalla (form, carga, errores, éxito). */
data class AuthScreenState(
    val form: AuthFormState = AuthFormState(),
    val isLoading: Boolean = false, // Para mostrar un spinner
    val generalError: String? = null, // Ej. "Credenciales incorrectas"
    val loginSuccess: Boolean = false, // Bandera para navegar
    val registrationSuccess: Boolean = false // Bandera para navegar
)

/**
 * ViewModel para Login y Registro.
 * Recibe 'repo' (para la BD) y 'sessionVM' (para actualizar la sesión global).
 */
class AuthVM(
    private val repo: FoodRepository,
    private val sessionVM: SessionVM
) : ViewModel() {

    // Estado principal que la UI observa
    private val _state = MutableStateFlow(AuthScreenState())
    val state = _state.asStateFlow()

    /** Evento: La UI lo llama al cambiar el formulario. Limpia el error general. */
    fun onFormChange(form: AuthFormState) {
        _state.update { it.copy(form = form, generalError = null) }
    }

    /** Evento: Intenta iniciar sesión. */
    fun login() {
        val form = _state.value.form
        // 1. Validar campos
        val emailError = Validators.email(form.email)
        val passError = Validators.password(form.pass)

        // 2. Mostrar errores de campos
        _state.update { it.copy(form = form.copy(emailError = emailError?.message, passError = passError?.message)) }

        // 3. Detener si hay errores de validación
        if (emailError != null || passError != null) return

        // 4. Iniciar lógica de login (en corutina)
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, generalError = null) } // Activa spinner
            val user = repo.findUserByEmail(form.email.trim())

            // 5. Verificar usuario, contraseña Y ROL
            if (user == null || user.passwordHash != form.pass || user.role != form.role) {
                // Error
                _state.update { it.copy(isLoading = false, generalError = "Credenciales incorrectas para el rol seleccionado.") }
            } else {
                // Éxito
                sessionVM.onLoginSuccess(user) // Actualiza la sesión global
                _state.update { it.copy(isLoading = false, loginSuccess = true) } // Activa bandera de navegación
            }
        }
    }

    /** Evento: Intenta registrar un usuario. */
    fun register() {
        val form = _state.value.form

        // 1. Validar todos los campos
        val nameError = Validators.name(form.name)
        val emailError = Validators.email(form.email)
        val passError = Validators.password(form.pass)

        // 2. Mostrar errores de campos
        _state.update {
            it.copy(form = form.copy(nameError = nameError?.message, emailError = emailError?.message, passError = passError?.message))
        }

        // 3. Detener si hay errores de validación
        if (nameError != null || emailError != null || passError != null) return

        // 4. Iniciar lógica de registro (en corutina)
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, generalError = null) } // Activa spinner

            // 5. Verificar si el email ya existe
            if (repo.findUserByEmail(form.email.trim()) != null) {
                // Error
                _state.update { it.copy(isLoading = false, generalError = "El correo ya está registrado") }
            } else {
                // Éxito
                val newUser = User(name = form.name.trim(), email = form.email.trim(), passwordHash = form.pass, role = form.role)
                repo.registerUser(newUser)
                _state.update { it.copy(isLoading = false, registrationSuccess = true) }
            }
        }
    }

    /** Evento: La UI lo llama DESPUÉS de navegar, para resetear las banderas de éxito. */
    fun onNavigationDone() {
        _state.update { it.copy(loginSuccess = false, registrationSuccess = false, generalError = null) }
    }
}