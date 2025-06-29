package com.robinmp.listadependientes.util

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController

/**
 * Middleware de autenticación que protege las rutas de la aplicación
 */
@Composable
fun AuthMiddleware(
    navController: NavController,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val securityManager = remember { SecurityManager(context) }
    var isCheckingAuth by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        // Verificar autenticación al iniciar
        if (!securityManager.isUserLoggedIn()) {
            navController.navigate("signin") {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
            }
        }
        isCheckingAuth = false
    }

    if (!isCheckingAuth) {
        content()
    }
}

/**
 * Hook personalizado para manejar la autenticación
 */
@Composable
fun rememberAuthState(): AuthState {
    val context = LocalContext.current
    val securityManager = remember { SecurityManager(context) }

    return remember {
        AuthState(
            isAuthenticated = securityManager.isUserLoggedIn(),
            username = securityManager.getUsername(),
            fullName = securityManager.getUserFullName(),
            logout = { securityManager.logout() }
        )
    }
}

data class AuthState(
    val isAuthenticated: Boolean,
    val username: String?,
    val fullName: String?,
    val logout: () -> Unit
)

/**
 * Composable que requiere autenticación
 */
@Composable
fun RequireAuth(
    navController: NavController,
    content: @Composable () -> Unit
) {
    val authState = rememberAuthState()

    LaunchedEffect(authState.isAuthenticated) {
        if (!authState.isAuthenticated) {
            navController.navigate("signin") {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
            }
        }
    }

    if (authState.isAuthenticated) {
        content()
    }
}