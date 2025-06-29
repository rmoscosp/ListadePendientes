package com.robinmp.listadependientes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.robinmp.listadependientes.ui.theme.*
import com.robinmp.listadependientes.util.SecurityManager
import com.robinmp.listadependientes.navigation.AppNavigation

class MainActivity : ComponentActivity() {

    private lateinit var securityManager: SecurityManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        securityManager = SecurityManager(this)

        enableEdgeToEdge()

        setContent {
            ListaDePendientesTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) {
                    // Navegación completa con seguridad
                    AppNavigation(
                        navController = navController,
                        securityManager = securityManager
                    )
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        android.util.Log.i("SecurityAudit", "App paused - Usuario: ${securityManager.getUsername()}")
    }

    override fun onResume() {
        super.onResume()
        android.util.Log.i("SecurityAudit", "App resumed - Sesión activa: ${securityManager.isUserLoggedIn()}")
    }
}