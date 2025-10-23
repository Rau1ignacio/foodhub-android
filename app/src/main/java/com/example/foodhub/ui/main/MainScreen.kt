package com.example.foodhub.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

// Define la estructura de un item de la barra de navegación inferior
data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    repo: FoodRepository,
    sessionVM: SessionVM, // Recibe el VM de Sesión (para saber el rol)
    onLogout: () -> Unit // Callback para cerrar sesión
) {
    // Controlador de navegación para la barra inferior (Home, Carrito, Admin)
    val navController = rememberNavController()
    val factoryWithSession = ViewModelFactoryWithSession(repo, sessionVM)

    // Instancia el CartVM aquí. Será compartido por MainNavGraph.
    val cartVM: CartVM = viewModel(factory = factoryWithSession)

    // Observa estados clave
    val cartState by cartVM.cartState.collectAsState() // Para el badge del carrito
    val sessionState by sessionVM.state.collectAsState() // Para el rol del usuario
    val currentUser = sessionState.loggedInUser

    // Lista de items de la barra inferior
    val bottomNavItems = listOf(
        BottomNavItem("Home", Route.Home.route, Icons.Default.Home),
        BottomNavItem("Carrito", Route.Cart.route, Icons.Default.ShoppingCart),
        BottomNavItem(
            "Admin",
            Route.Admin.route,
            Icons.Default.AdminPanelSettings
        ) // Pestaña de Admin
    )

    Scaffold(
        // --- BARRA SUPERIOR ---
        topBar = {
            TopAppBar(
                title = { Text("Food Hub") },
                actions = {
                    // Muestra el rol actual (para debug)
                    currentUser?.let { user ->
                        Text(
                            text = "Rol: ${user.role}",
                            modifier = Modifier.padding(end = 8.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    // Botón Historial
                    IconButton(onClick = { navController.navigate("history") }) {
                        Icon(Icons.Default.History, contentDescription = "Historial de Pedidos")
                    }
                    // Botón Cerrar Sesión
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Cerrar Sesión")
                    }
                }
            )
        },
        // --- BARRA INFERIOR ---
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { item ->
                    val isAdminRoute = item.route == Route.Admin.route

                    // --- LÓGICA DE ROLES ---
                    // Si el item es "Admin" Y el usuario NO es "ADMIN",
                    // simplemente no se renderiza el item (se omite).
                    if (isAdminRoute && currentUser?.role != "ADMIN") {
                        // No renderiza nada
                    } else {
                        // Renderiza el item normal
                        NavigationBarItem(
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                // Lógica de navegación de la barra inferior
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
                                // (Aquí debería estar tu BadgedBox para el ícono del carrito)
                                // ej: if (item.route == Route.Cart.route) { ... }
                                Icon(item.icon, contentDescription = item.label)
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        // --- CONTENIDO PRINCIPAL ---
        // Aquí se "dibuja" el NavHost (MainNavGraph) en el espacio
        // que deja el Scaffold (entre la TopBar y la BottomBar).
        MainNavGraph(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            repo = repo,
            sessionVM = sessionVM,
            cartVM = cartVM // Pasa el CartVM compartido
        )
    }
}