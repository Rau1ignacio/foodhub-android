package com.example.foodhub.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
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
import com.example.foodhub.ui.cart.CartScreen
import com.example.foodhub.ui.detail.DetailScreen
import com.example.foodhub.ui.history.OrderHistoryScreen
import com.example.foodhub.ui.home.HomeScreen
import com.example.foodhub.ui.viewmodels.AdminVM
import com.example.foodhub.ui.viewmodels.CartVM
import com.example.foodhub.ui.viewmodels.DetailVM
import com.example.foodhub.ui.viewmodels.HomeVM
import com.example.foodhub.ui.viewmodels.OrderHistoryVM
import com.example.foodhub.ui.viewmodels.SessionVM
import com.example.foodhub.ui.viewmodels.ViewModelFactory
import com.example.foodhub.ui.viewmodels.ViewModelFactoryWithSession

@Composable
fun MainNavGraph(
    navController: NavHostController,
    repo: FoodRepository,
    modifier: Modifier = Modifier,
    sessionVM: SessionVM,
    cartVM: CartVM
) {
    // Fábrica para VMs que necesitan repo + session
    val factoryWithSession = ViewModelFactoryWithSession(repo, sessionVM)

    // VM para administración de productos (solo necesita repo)
    val adminVM: AdminVM = viewModel(factory = ViewModelFactory(repo))

    val homeVM: HomeVM = viewModel(factory = ViewModelFactory(repo))

    NavHost(
        navController = navController,
        // 👇 Ya estamos logueados, empezamos en Home
        startDestination = Route.Home.route,
        modifier = modifier
    ) {
        // ----------------- HOME -----------------
        composable(Route.Home.route) {
            HomeScreen(
                vm = homeVM,
                cartVM = cartVM,
                onProductClick = { productId ->
                    navController.navigate(Route.Detail.build(productId))
                }
            )
        }

        // ----------------- DETAIL -----------------
        composable(
            route = Route.Detail.route, // "detail/{id}"
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("id") ?: 0L

            // VM específico para la pantalla de detalle
            val detailVM: DetailVM = viewModel(factory = ViewModelFactory(repo))

            // Cargamos el producto al entrar
            LaunchedEffect(productId) {
                detailVM.loadProduct(productId)
            }

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
                    // De momento volvemos al Home (más adelante puedes ir a OrderSummary)
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

        // ----------------- HISTORY -----------------
        composable("history") {
            val orderHistoryVM: OrderHistoryVM =
                viewModel(factory = factoryWithSession)

            OrderHistoryScreen(
                vm = orderHistoryVM,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
