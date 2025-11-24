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

    // ---- Cambios de campos ----

    fun onNameChange(text: String) {
        val error = Validators.name(text)?.message
        _state.update {
            it.copy(
                form = it.form.copy(name = text, nameError = error),
                generalError = null
            )
        }
    }

    fun onEmailChange(text: String) {
        val error = Validators.email(text)?.message
        _state.update {
            it.copy(
                form = it.form.copy(email = text, emailError = error),
                generalError = null
            )
        }
    }

    fun onPasswordChange(text: String) {
        val error = Validators.password(text)?.message
        _state.update {
            it.copy(
                form = it.form.copy(pass = text, passError = error),
                generalError = null
            )
        }
    }

    fun onRoleChange(role: String) {
        _state.update { it.copy(form = it.form.copy(role = role)) }
    }

    // ---- Acciones ----

    fun login() {
        val form = _state.value.form
        val emailError = Validators.email(form.email)?.message
        val passError = Validators.password(form.pass)?.message

        if (emailError != null || passError != null) {
            _state.update {
                it.copy(
                    form = form.copy(
                        emailError = emailError,
                        passError = passError
                    )
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, generalError = null) }

            val result = repo.login(form.email.trim(), form.pass)

            result
                .onSuccess { user ->
                    sessionVM.onLoginSuccess(user)
                    _state.update {
                        it.copy(isLoading = false, loginSuccess = true)
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            generalError = e.message ?: "Credenciales incorrectas o error de red"
                        )
                    }
                }
        }
    }



    fun register() {
        val form = _state.value.form
        val nameError = Validators.name(form.name)?.message
        val emailError = Validators.email(form.email)?.message
        val passError = Validators.password(form.pass)?.message

        if (nameError != null || emailError != null || passError != null) {
            _state.update {
                it.copy(
                    form = form.copy(
                        nameError = nameError,
                        emailError = emailError,
                        passError = passError
                    )
                )
            }
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

            val result = repo.register(newUser)

            result
                .onSuccess {
                    _state.update {
                        it.copy(isLoading = false, registrationSuccess = true)
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            generalError = e.message ?: "Error al registrar (correo duplicado o red)"
                        )
                    }
                }
        }
    }

    fun onNavigationDone() {
        _state.update {
            it.copy(loginSuccess = false, registrationSuccess = false)
        }
    }
}
