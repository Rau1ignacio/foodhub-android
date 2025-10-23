package com.example.foodhub.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.foodhubtest.ui.viewmodels.AuthFormState
import com.example.foodhubtest.ui.viewmodels.AuthScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    state: AuthScreenState, // El estado completo (form, errores, isLoading)
    onFormChange: (AuthFormState) -> Unit, // Notifica al VM que el formulario cambió
    onLoginClick: () -> Unit, // Notifica al VM que se presionó "Login"
    onNavigateToRegister: () -> Unit // Callback de navegación
) {
    val form = state.form
    val roles = listOf("CLIENT", "ADMIN")

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Iniciar Sesión", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(24.dp))

        // --- SELECTOR DE ROL ---
        Text("Iniciar sesión como:", style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)) {
            roles.forEach { role ->
                FilterChip(
                    selected = form.role == role,
                    onClick = { onFormChange(form.copy(role = role)) }, // Actualiza el rol en el VM
                    label = { Text(if (role == "CLIENT") "Cliente" else "Admin") }
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        // --- CAMPO EMAIL ---
        OutlinedTextField(
            value = form.email,
            onValueChange = { onFormChange(form.copy(email = it)) }, // Notifica al VM
            label = { Text("Email") },
            isError = form.emailError != null, // Muestra error si existe
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        form.emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) } // Mensaje de error
        Spacer(Modifier.height(8.dp))

        // --- CAMPO CONTRASEÑA ---
        OutlinedTextField(
            value = form.pass,
            onValueChange = { onFormChange(form.copy(pass = it)) }, // Notifica al VM
            label = { Text("Contraseña") },
            isError = form.passError != null,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(), // Oculta el texto
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )
        form.passError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Spacer(Modifier.height(16.dp))

        // --- INDICADOR DE CARGA Y ERROR GENERAL ---
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(vertical = 8.dp))
        } else {
            // Muestra el error de "Credenciales incorrectas"
            state.generalError?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp))
            }
        }

        // --- BOTONES ---
        Button(
            onClick = onLoginClick, // Llama al VM
            enabled = !state.isLoading, // Se deshabilita mientras carga
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Login")
        }
        TextButton(onClick = onNavigateToRegister, enabled = !state.isLoading) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}