package com.example.foodhub.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.foodhub.ui.viewmodels.AuthScreenState
import com.example.foodhub.ui.viewmodels.AuthVM

@Composable
fun RegisterScreen(
    vm: AuthVM,
    state: AuthScreenState,
    onNavigateToLogin: () -> Unit
) {
    val roles = listOf("CLIENT", "ADMIN")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Registro", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // SELECCIÓN DE ROL
        Text("Selecciona un Rol:")
        LazyRow {
            items(roles) { role ->
                FilterChip(
                    selected = state.form.role == role,
                    onClick = { vm.onRoleChange(role) },
                    label = { Text(role) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // NOMBRE
        OutlinedTextField(
            value = state.form.name,
            onValueChange = { text -> vm.onNameChange(text) },
            label = { Text("Nombre") },
            isError = state.form.nameError != null,
            modifier = Modifier.fillMaxWidth()
        )
        state.form.nameError?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // EMAIL
        OutlinedTextField(
            value = state.form.email,
            onValueChange = { text -> vm.onEmailChange(text) },
            label = { Text("Email") },
            isError = state.form.emailError != null,
            modifier = Modifier.fillMaxWidth()
        )
        state.form.emailError?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // PASSWORD
        OutlinedTextField(
            value = state.form.pass,
            onValueChange = { text -> vm.onPasswordChange(text) },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            isError = state.form.passError != null,
            modifier = Modifier.fillMaxWidth()
        )
        state.form.passError?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        // ERROR GENERAL
        state.generalError?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { vm.register() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            Text(if (state.isLoading) "Registrando..." else "Crear Cuenta")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onNavigateToLogin) {
            Text("Ya tengo cuenta. Iniciar Sesión")
        }
    }
}