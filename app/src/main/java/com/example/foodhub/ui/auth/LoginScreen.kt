package com.example.foodhub.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.foodhub.ui.viewmodels.AuthScreenState
import com.example.foodhub.ui.viewmodels.AuthVM

@Composable
fun LoginScreen(
    vm: AuthVM,
    state: AuthScreenState,
    onNavigateToRegister: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Iniciar Sesión",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // EMAIL
        OutlinedTextField(
            value = state.form.email,
            onValueChange = { text -> vm.onEmailChange(text) },
            label = { Text("Email") },
            isError = state.form.emailError != null,
            modifier = Modifier.fillMaxWidth()
        )
        state.form.emailError?.let { errorMsg ->
            Text(text = errorMsg, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // PASSWORD con icono de ojo
        OutlinedTextField(
            value = state.form.pass,
            onValueChange = { text -> vm.onPasswordChange(text) },
            label = { Text("Contraseña") },
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            },
            isError = state.form.passError != null,
            modifier = Modifier.fillMaxWidth()
        )
        state.form.passError?.let { errorMsg ->
            Text(text = errorMsg, color = MaterialTheme.colorScheme.error)
        }

        // ERROR GENERAL
        state.generalError?.let { errorMsg ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMsg, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { vm.login() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            Text(if (state.isLoading) "Cargando..." else "Entrar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onNavigateToRegister) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}
