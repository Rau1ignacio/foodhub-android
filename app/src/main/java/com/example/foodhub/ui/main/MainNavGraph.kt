package com.example.foodhub.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.foodhub.core.nav.Route
import com.example.foodhub.data.repository.FoodRepository
import com.example.foodhub.ui.admin.AdminListScreen
import com.example.foodhub.ui.admin.AdminProductFormScreen
import com.example.foodhub.ui.auth.LoginScreen
import com.example.foodhub.ui.auth.RegisterScreen
import com.example.foodhub.ui.cart.CartScreen
import com.example.foodhub.ui.detail.DetailScreen
import com.example.foodhub.ui.home.HomeScreen
import com.example.foodhub.ui.viewmodels.AdminVM
import com.example.foodhub.ui.viewmodels.AuthVM
import com.example.foodhub.ui.viewmodels.CartVM
import com.example.foodhub.ui.viewmodels.DetailVM
import com.example.foodhub.ui.viewmodels.SessionVM
import com.example.foodhub.ui.viewmodels.ViewModelFactory

@Composable
fun MainNavGraph(
    navController: NavHostController,
    repo: FoodRepository,
    modifier: Modifier = Modifier,
    sessionVM: SessionVM,
    cartVM: CartVM
) {
    // AuthVM que conoce el repositorio y la sesión
    val authVM: AuthVM = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthVM(repo, sessionVM) as T
            }
        }
    )

    // VM para la parte de administración de productos
    val adminVM: AdminVM = viewModel(factory = ViewModelFactory(repo))

    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier
    ) {

        // ----------------- LOGIN -----------------
        composable("login") {
            val state by authVM.state.collectAsState()

            // Si el login fue exitoso, vamos al Home
            LaunchedEffect(state.loginSuccess) {
                if (state.loginSuccess) {
                    authVM.onNavigationDone()
                    navController.navigate(Route.Home.route) {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }

            LoginScreen(
                vm = authVM,
                state = state,
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        // ----------------- REGISTER -----------------
        composable("register") {
            val state by authVM.state.collectAsState()

            LaunchedEffect(state.registrationSuccess) {
                if (state.registrationSuccess) {
                    authVM.onNavigationDone()
                    navController.popBackStack() // volvemos al login
                }
            }

            RegisterScreen(
                vm = authVM,
                state = state,
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        // ----------------- HOME -----------------
        composable(Route.Home.route) {
            HomeScreen(
                repo = repo,
                onProductClick = { productId ->
                    navController.navigate("detail/$productId")
                }
            )
        }

        // ----------------- DETAIL -----------------
        composable(
            route = Route.Detail.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("id") ?: 0L

            // 1. Creamos el ViewModel específico para Detalle aquí
            val detailVM: DetailVM = viewModel(factory = ViewModelFactory(repo))

            // 2. Cargamos el producto cuando entramos a la pantalla
            LaunchedEffect(productId) {
                detailVM.loadProduct(productId)
            }

            // 3. Pasamos los ViewModels a la pantalla
            DetailScreen(
                detailVM = detailVM,
                cartVM = cartVM,
                onBack = { navController.popBackStack() }
            )
        }

        // ----------------- CART -----------------
        composable(Route.Cart.route) {
            CartScreen(
                vm = cartVM,
                onOrderConfirmed = {
                    // Después de confirmar pedido, volvemos al Home
                    navController.navigate(Route.Home.route) {
                        popUpTo(Route.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // ----------------- ADMIN LIST -----------------
        composable(Route.Admin.route) {
            AdminListScreen(
                vm = adminVM,
                onEditProduct = { productId ->
                    navController.navigate("admin_form/$productId")
                },
                onAddProduct = {
                    navController.navigate("admin_form/0")
                }
            )
        }

        // ----------------- ADMIN FORM -----------------
        composable(
            route = "admin_form/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("productId") ?: 0L
            AdminProductFormScreen(
                vm = adminVM,
                productId = if (id == 0L) null else id,
                navBack = { navController.popBackStack() }
            )
        }

        // ----------------- HISTORY (placeholder) -----------------
        composable("history") {
            // Aquí iría tu OrderHistoryScreen cuando lo tengas listo
            // OrderHistoryScreen(...)
        }
    }
}
