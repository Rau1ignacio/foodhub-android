package com.example.foodhub.core.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.foodhub.data.repository.FoodRepository
import com.example.foodhub.ui.auth.LoginScreen
import com.example.foodhub.ui.auth.RegisterScreen
import com.example.foodhub.ui.main.MainScreen
import com.example.foodhub.ui.viewmodels.AuthVM
import com.example.foodhub.ui.viewmodels.SessionVM
import com.example.foodhub.ui.viewmodels.ViewModelFactoryWithSession

// Definición de rutas
sealed class Route(val route: String) {
    data object Home : Route("home")
    data object Cart : Route("cart")

    data object Detail : Route("detail/{id}") {
        fun build(id: Long) = "detail/$id"
    }

    data object OrderSummary : Route("order_summary/{orderId}") {
        fun build(orderId: Long) = "order_summary/$orderId"
    }
    data object Admin : Route("admin_list")
    data object AdminForm : Route("admin_form?productId={productId}") {
        fun build(productId: Long? = null): String {
            return if (productId != null) "admin_form?productId=$productId" else "admin_form"
        }
    }
}

@Composable
fun AppNav(repo: FoodRepository) {

    val navController = rememberNavController()
    val sessionVM: SessionVM = viewModel()

    // Usamos el Factory especial que creamos para inyectar Repo + Session
    val viewModelFactory = ViewModelFactoryWithSession(repo, sessionVM)
    val authVM: AuthVM = viewModel(factory = viewModelFactory)

    val authState by authVM.state.collectAsState()

    // Redirección automática si el login es exitoso
    LaunchedEffect(authState.loginSuccess) {
        if (authState.loginSuccess) {
            navController.navigate("main_flow") {
                popUpTo("auth_flow") { inclusive = true }
            }
            authVM.onNavigationDone()
        }
    }

    NavHost(navController = navController, startDestination = "auth_flow") {

        // --- FLUJO DE AUTENTICACIÓN ---
        navigation(startDestination = "login", route = "auth_flow") {

            composable("login") {
                LoginScreen(
                    vm = authVM,
                    state = authState,
                    onNavigateToRegister = { navController.navigate("register") }
                )
            }

            composable("register") {
                RegisterScreen(
                    vm = authVM,
                    state = authState,
                    onNavigateToLogin = {
                        navController.popBackStack()
                        authVM.onNavigationDone() // Limpia errores al cambiar de pantalla
                    }
                )

                // Redirección automática si el registro es exitoso (vuelve al login o entra directo)
                LaunchedEffect(authState.registrationSuccess) {
                    if (authState.registrationSuccess) {
                        // Opción A: Volver al Login para que ingrese sus datos
                        navController.popBackStack()

                        // Opción B: Si tu backend loguea automático, irías a main_flow
                        // Pero por seguridad usualmente se pide loguear tras registro.

                        authVM.onNavigationDone()
                    }
                }
            }
        }

        // --- FLUJO PRINCIPAL ---
        composable("main_flow") {
            MainScreen(
                repo = repo,
                sessionVM = sessionVM,
                onLogout = {
                    sessionVM.onLogout()
                    // Regresa al login y borra todo el historial de navegacion

                    navController.navigate("auth_flow") {
                        popUpTo("main_flow") { inclusive = true }
                    }
                }
            )
        }
    }
}