package com.example.foodhub.core.nav

// Importaciones necesarias para Jetpack Compose, navegación y ViewModels
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

//  Definición de rutas de navegación (Sellada para mantener controladas las rutas válidas)
sealed class Route(val route: String) {
    //  Ruta a la pantalla principal
    data object Home : Route("home")

    //  Ruta al carrito
    data object Cart : Route("cart")

    // Ruta al detalle de producto con parámetro dinámico 'id'
    data object Detail : Route("detail/{id}") {
        fun build(id: Long) = "detail/$id"
        val argName = "id"
    }

    // Ruta al resumen de orden con parámetro 'orderId'
    data object OrderSummary : Route("order_summary/{orderId}") {
        fun build(orderId: Long) = "order_summary/$orderId"
    }

    // Ruta al panel de administración
    data object Admin : Route("admin_list")

    // Ruta al formulario de administración con parámetro opcional 'productId'
    data object AdminForm : Route("admin_form?productId={productId}") {
        fun build(productId: Long? = null): String {
            return if (productId != null) "admin_form?productId=$productId" else "admin_form"
        }
    }
}

// Función principal de navegación de la app
@Composable
fun AppNav(repo: FoodRepository) {

    // Controlador de navegación principal
    val navController = rememberNavController()

    // ViewModel para manejar la sesión del usuario
    val sessionVM: SessionVM = viewModel()

    // Fábrica de ViewModels con dependencia de sesión
    val viewModelFactory = ViewModelFactoryWithSession(repo, sessionVM)

    // ViewModel para autenticación (login / registro)
    val authVM: AuthVM = viewModel(factory = viewModelFactory)

    // Observa el estado de autenticación en tiempo real
    val authState by authVM.state.collectAsState()

    // Efecto lanzado al detectar un login exitoso
    LaunchedEffect(authState.loginSuccess) {
        if (authState.loginSuccess) {
            navController.navigate("main_flow") {
                // Elimina el stack de autenticación al navegar al flujo principal
                popUpTo("auth_flow") { inclusive = true }
            }
            authVM.onNavigationDone()
        }
    }

    // Estructura principal de navegación
    NavHost(navController = navController, startDestination = "auth_flow") {

        // Flujo de autenticación (login / registro)
        navigation(startDestination = "login", route = "auth_flow") {

            // Pantalla de Login
            composable("login") {
                LoginScreen(
                    state = authState,
                    onFormChange = { authVM.onFormChange(it) },
                    onLoginClick = { authVM.login() },
                    onNavigateToRegister = { navController.navigate("register") }
                )
            }

            // Pantalla de Registro
            composable("register") {
                RegisterScreen(
                    state = authState,
                    onFormChange = { authVM.onFormChange(it) },
                    onRegisterClick = { authVM.register() },
                    onNavigateToLogin = {
                        navController.popBackStack()
                        authVM.onNavigationDone()
                    }
                )

                // Efecto lanzado cuando el registro es exitoso
                LaunchedEffect(authState.registrationSuccess) {
                    if (authState.registrationSuccess) {
                        navController.popBackStack()
                        authVM.onNavigationDone()
                    }
                }
            }
        }

        // Flujo principal de la aplicación (una vez autenticado)
        composable("main_flow") {
            MainScreen(
                repo = repo,
                sessionVM = sessionVM,
                onLogout = {
                    // Cierra sesión y vuelve al flujo de autenticación
                    sessionVM.onLogout()
                    navController.navigate("auth_flow") {
                        popUpTo("main_flow") { inclusive = true }
                    }
                }
            )
        }
    }
}
