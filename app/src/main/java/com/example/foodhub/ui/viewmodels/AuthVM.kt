package com.example.foodhub.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhubtest.core.utils.Validators // Importa las reglas de validación
import com.example.foodhubtest.data.local.entities.User
import com.example.foodhubtest.data.repository.FoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// --- DEFINICIÓN DE ESTADOS ---

/**
 * Estado 1: Define SOLAMENTE los datos del formulario y sus errores específicos.
 */
data class AuthFormState(
    val name: String = "",
    val email: String = "",
    val pass: String = "",
    val role: String = "CLIENT", // Rol seleccionado (CLIENT o ADMIN)
    val nameError: String? = null, // Mensaje de error para el nombre
    val emailError: String? = null,
    val passError: String? = null
)

/**
 * Estado 2: Define el ESTADO COMPLETO de la pantalla.
 * Contiene el formulario y también el estado de la UI (carga, errores, éxito).
 */
data class AuthScreenState(
    val form: AuthFormState = AuthFormState(), // El estado del formulario
    val isLoading: Boolean = false,            // true para mostrar un spinner de carga
    val generalError: String? = null,          // Errores globales (ej. "Credenciales incorrectas")
    val loginSuccess: Boolean = false,         // Bandera para navegar tras login
    val registrationSuccess: Boolean = false   // Bandera para navegar tras registro
)

// --- VIEWMODEL ---

/**
 * ViewModel para las pantallas de Login y Registro.
 * Recibe el 'repo' (para acceder a la BD) y 'sessionVM' (para actualizar la sesión global).
 */
class AuthVM(
    private val repo: FoodRepository,
    private val sessionVM: SessionVM
) : ViewModel() {

    // --- ESTADO PRINCIPAL (Observado por la UI) ---
    private val _state = MutableStateFlow(AuthScreenState())
    val state = _state.asStateFlow() // La UI observa 'state'

    /**
     * Evento: Se llama cada vez que el usuario teclea algo en el formulario.
     * Actualiza el 'form' y limpia el error general.
     */
    fun onFormChange(form: AuthFormState) {
        _state.update { it.copy(form = form, generalError = null) }
    }

    // --- LÓGICA DE LOGIN ---
    fun login() {
        val form = _state.value.form

        // 1. Validar campos
        val emailError = Validators.email(form.email)
        val passError = Validators.password(form.pass)

        // 2. Mostrar errores de campo (si los hay)
        _state.update { it.copy(form = form.copy(emailError = emailError?.message, passError = passError?.message)) }

        // 3. Detener si hay errores
        if (emailError != null || passError != null) return

        // 4. Iniciar Corutina para Login
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, generalError = null) } // Mostrar spinner

            // 5. Consultar BD
            val user = repo.findUserByEmail(form.email.trim())

            // 6. Verificar credenciales (Usuario existe, contraseña y rol coinciden)
            if (user == null || user.passwordHash != form.pass || user.role != form.role) {
                // Error
                _state.update { it.copy(isLoading = false, generalError = "Credenciales incorrectas para el rol seleccionado.") }
            } else {
                // Éxito
                sessionVM.onLoginSuccess(user) // <-- Notifica al SessionVM
                _state.update { it.copy(isLoading = false, loginSuccess = true) } // <-- Activa bandera de navegación
            }
        }
    }

    // --- LÓGICA DE REGISTRO ---
    fun register() {
        val form = _state.value.form

        // 1. Validar TODOS los campos
        val nameError = Validators.name(form.name)
        val emailError = Validators.email(form.email)
        val passError = Validators.password(form.pass)

        // 2. Mostrar errores de campo
        _state.update {
            it.copy(
                form = form.copy(
                    nameError = nameError?.message,
                    emailError = emailError?.message,
                    passError = passError?.message
                )
            )
        }

        // 3. Detener si hay errores
        if (nameError != null || emailError != null || passError != null) return

        // 4. Iniciar Corutina para Registro
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, generalError = null) } // Mostrar spinner

            // 5. Verificar si el correo ya existe
            if (repo.findUserByEmail(form.email.trim()) != null) {
                // Error
                _state.update { it.copy(isLoading = false, generalError = "El correo ya está registrado") }
            } else {
                // Éxito: Crear usuario
                val newUser = User(name = form.name.trim(), email = form.email.trim(), passwordHash = form.pass, role = form.role)
                repo.registerUser(newUser)
                _state.update { it.copy(isLoading = false, registrationSuccess = true) } // <-- Activa bandera de navegación
            }
        }
    }

    /**
     * Evento: La UI llama a esto DESPUÉS de navegar.
     * Resetea las banderas de éxito para evitar que la app navegue de nuevo
     * si el usuario rota la pantalla.
     */
    fun onNavigationDone() {
        _state.update { it.copy(loginSuccess = false, registrationSuccess = false, generalError = null) }
    }
}