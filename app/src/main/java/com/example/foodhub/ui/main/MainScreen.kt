package com.example.foodhub.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.foodhub.core.nav.Route
import com.example.foodhub.data.repository.FoodRepository
import com.example.foodhub.ui.viewmodels.CartVM
import com.example.foodhub.ui.viewmodels.SessionVM
import com.example.foodhub.ui.viewmodels.ViewModelFactoryWithSession

// Estructura de un item de la barra de navegación inferior
data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    repo: FoodRepository,
    sessionVM: SessionVM, // VM de sesión para conocer al usuario y su rol
    onLogout: () -> Unit  // Callback de cierre de sesión
) {
    val navController = rememberNavController()

    // Factory que inyecta repo + sessionVM a los ViewModels
    val factoryWithSession = ViewModelFactoryWithSession(repo, sessionVM)

    // CartVM compartido entre MainScreen y MainNavGraph
    val cartVM: CartVM = viewModel(factory = factoryWithSession)

    // Estados observados
    val cartState by cartVM.cartState.collectAsState()
    val sessionState by sessionVM.state.collectAsState()
    val currentUser = sessionState.loggedInUser

    // Items de la barra inferior
    val bottomNavItems = listOf(
        BottomNavItem("Home", Route.Home.route, Icons.Filled.Home),
        BottomNavItem("Carrito", Route.Cart.route, Icons.Filled.ShoppingCart),
        BottomNavItem("Admin", Route.Admin.route, Icons.Filled.AdminPanelSettings)
    )

    Scaffold(
        // ----------------- TOP BAR -----------------
        topBar = {
            TopAppBar(
                title = { Text("Food Hub") },
                actions = {
                    // Rol actual (útil para debug / feedback visual)
                    currentUser?.let { user ->
                        Text(
                            text = "Rol: ${user.role}",
                            modifier = Modifier.padding(end = 8.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    // Historial de pedidos
                    IconButton(onClick = { navController.navigate("history") }) {
                        Icon(
                            imageVector = Icons.Filled.History,
                            contentDescription = "Historial de Pedidos"
                        )
                    }
                    // Cerrar sesión
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Filled.Logout,
                            contentDescription = "Cerrar Sesión"
                        )
                    }
                }
            )
        },

        // ----------------- BOTTOM BAR -----------------
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { item ->
                    val isAdminRoute = item.route == Route.Admin.route

                    // Si la pestaña es Admin y el usuario NO es ADMIN, no se muestra
                    if (isAdminRoute && currentUser?.role != "ADMIN") {
                        // no renderizamos el item
                    } else {
                        NavigationBarItem(
                            selected = currentDestination
                                ?.hierarchy
                                ?.any { it.route == item.route } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            label = { Text(item.label) },
                            icon = {
                                // Aquí podrías meter un BadgedBox para mostrar cantidad del carrito
                                // usando cartState.totalItems, por ejemplo.
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        // ----------------- CONTENIDO PRINCIPAL -----------------
        Box(modifier = Modifier.padding(innerPadding)) {
            MainNavGraph(
                navController = navController,
                repo = repo,
                modifier = Modifier,   // ya recibimos el padding en el Box
                sessionVM = sessionVM,
                cartVM = cartVM
            )
        }
    }
}
