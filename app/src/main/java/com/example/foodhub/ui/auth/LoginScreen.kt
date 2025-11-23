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
import com.example.foodhub.ui.viewmodels.AuthVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    vm: AuthVM,
    state: com.example.foodhub.ui.viewmodels.AuthScreenState,
    onNavigateToRegister: () -> Unit
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
                    onClick = { vm.onRoleChange(role) },
                    label = { Text(if (role == "CLIENT") "Cliente" else "Admin") }
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        // --- CAMPO EMAIL ---
        OutlinedTextField(
            value = form.email,
            onValueChange = { vm.onEmailChange(it) },
            label = { Text("Email") },
            isError = form.emailError != null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = {
                if (form.emailError != null) Text(form.emailError, color = MaterialTheme.colorScheme.error)
            }
        )

        // --- CAMPO CONTRASEÑA ---
        OutlinedTextField(
            value = form.pass,
            onValueChange = { vm.onPasswordChange(it) },
            label = { Text("Contraseña") },
            isError = form.passError != null,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            supportingText = {
                if (form.passError != null) Text(form.passError, color = MaterialTheme.colorScheme.error)
            }
        )

        Spacer(Modifier.height(16.dp))

        // --- ESTADO DE CARGA ---
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(vertical = 8.dp))
        }

        // --- ERROR GENERAL (del Backend) ---
        state.generalError?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp))
        }

        Button(
            onClick = { vm.login() },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) { Text("Login") }

        TextButton(onClick = onNavigateToRegister, enabled = !state.isLoading) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}