package com.example.foodhub.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.foodhubtest.core.nav.Route
import com.example.foodhubtest.data.repository.FoodRepository
import com.example.foodhubtest.ui.admin.AdminListScreen
import com.example.foodhubtest.ui.admin.AdminProductFormScreen
import com.example.foodhubtest.ui.cart.CartScreen
import com.example.foodhubtest.ui.cart.OrderSummaryScreen
import com.example.foodhubtest.ui.detail.DetailScreen
import com.example.foodhubtest.ui.history.OrderHistoryScreen
import com.example.foodhubtest.ui.home.HomeScreen
import com.example.foodhubtest.ui.viewmodels.*

@Composable
fun MainNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController, // El controlador que maneja este grafo
    repo: FoodRepository,
    sessionVM: SessionVM, // VM de Sesión (para saber quién está logueado)
    cartVM: CartVM // VM de Carrito (compartido entre Home, Detail y Cart)
) {
    // Factories para crear ViewModels que necesitan dependencias
    val factoryWithSession = ViewModelFactoryWithSession(repo, sessionVM)
    val factorySimple = ViewModelFactory(repo)

    // NavHost define el contenedor para las pantallas de este grafo
    NavHost(
        navController = navController,
        startDestination = Route.Home.route, // La pantalla inicial es "Home"
        modifier = modifier
    ) {

        // --- RUTA: Home ---
        composable(Route.Home.route) {
            HomeScreen(
                repo = repo,
                onProductClick = { productId ->
                    // Navega a la pantalla de Detalle, pasando el ID
                    navController.navigate(Route.Detail.build(productId))
                }
            )
        }

        // --- RUTA: Carrito ---
        composable(Route.Cart.route) {
            CartScreen(
                vm = cartVM, // Usa el VM de carrito compartido
                onConfirmOrder = { orderId ->
                    // Navega a Resumen y limpia el backstack hasta Home
                    navController.navigate(Route.OrderSummary.build(orderId)) {
                        popUpTo(Route.Home.route)
                    }
                }
            )
        }

        // --- RUTA: Historial ---
        composable("history") { // Ruta simple (definida en TopAppBar)
            // Crea un VM solo para esta pantalla
            val orderHistoryVM: OrderHistoryVM = viewModel(factory = factoryWithSession)
            OrderHistoryScreen(vm = orderHistoryVM, onBack = { navController.popBackStack() })
        }

        // --- RUTA: Lista de Admin ---
        composable(Route.Admin.route) { // Ruta base de Admin
            val adminVM: AdminVM = viewModel(factory = factorySimple) // VM para el CRUD
            AdminListScreen(
                vm = adminVM,
                onAddProduct = {
                    // Navega al formulario (modo "Crear", sin ID o con ID=0)
                    navController.navigate(Route.AdminForm.build())
                },
                onEditProduct = { productId ->
                    // Navega al formulario (modo "Editar", pasando el ID)
                    navController.navigate(Route.AdminForm.build(productId))
                }
            )
        }

        // --- RUTA: Formulario de Admin (con argumento opcional) ---
        composable(
            route = Route.AdminForm.route, // "adminForm?productId={productId}"
            arguments = listOf(navArgument("productId") {
                type = NavType.LongType
                defaultValue = 0L // 0L = Modo "Crear"
            })
        ) { backStackEntry ->
            // Recibe el ID (0L si es nuevo)
            val productId = backStackEntry.arguments?.getLong("productId")
            // Reutiliza la instancia del VM de Admin
            val adminVM: AdminVM = viewModel(factory = factorySimple)
            AdminProductFormScreen(
                vm = adminVM,
                productId = if (productId == 0L) null else productId, // Pasa null si es 0L
                navBack = { navController.popBackStack() } // Vuelve a la lista
            )
        }

        // --- RUTA: Detalle de Producto (con argumento obligatorio) ---
        composable(
            route = Route.Detail.route, // "detail/{id}"
            arguments = listOf(navArgument(Route.Detail.argName) { type = NavType.LongType })
        ) { entry ->
            val id = entry.arguments?.getLong(Route.Detail.argName) ?: 0L
            DetailScreen(
                repo = repo,
                cartVM = cartVM, // Pasa el VM de carrito compartido
                id = id,
                onBack = { navController.popBackStack() }
            )
        }

        // --- RUTA: Resumen de Orden (con argumento obligatorio) ---
        composable(
            route = Route.OrderSummary.route, // "summary/{orderId}"
            arguments = listOf(navArgument("orderId") { type = NavType.LongType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getLong("orderId") ?: -1L
            OrderSummaryScreen(
                orderId = orderId,
                onBackToHome = {
                    // Vuelve a Home y limpia TODO el backstack
                    navController.navigate(Route.Home.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}