package com.robinmp.listadependientes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.robinmp.listadependientes.auth.SignInViewModel
import com.robinmp.listadependientes.navigation.AppNavigation
import com.robinmp.listadependientes.ui.screens.SignInScreen
import com.robinmp.listadependientes.viewmodels.TaskViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ListaDePendientesTheme {
                val taskViewModel: TaskViewModel = viewModel()

                // Estado reactivo para la autenticación
                var isUserAuthenticated by remember { mutableStateOf(false) }

                // Verificar autenticación inicial
                LaunchedEffect(Unit) {
                    isUserAuthenticated = FirebaseAuth.getInstance().currentUser != null
                }

                // Listener para cambios en el estado de autenticación
                DisposableEffect(Unit) {
                    val authStateListener = FirebaseAuth.AuthStateListener { auth ->
                        val wasAuthenticated = isUserAuthenticated
                        isUserAuthenticated = auth.currentUser != null

                        // Si el usuario acaba de autenticarse, cargar las tareas
                        if (!wasAuthenticated && isUserAuthenticated) {
                            taskViewModel.loadTasks()
                        }
                    }

                    FirebaseAuth.getInstance().addAuthStateListener(authStateListener)

                    onDispose {
                        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)
                    }
                }

                if (!isUserAuthenticated) {
                    // No autenticado: mostrar pantalla de login
                    val signInViewModel: SignInViewModel = viewModel()

                    SignInScreen(
                        viewModel = signInViewModel,
                        onSignedIn = {
                            // El AuthStateListener se encargará de cargar las tareas
                            // cuando detecte el cambio de autenticación
                        }
                    )
                } else {
                    // Autenticado: mostrar navegación principal
                    val tasks by taskViewModel.tasks.collectAsState()
                    val isLoading by taskViewModel.isLoading.collectAsState()
                    val error by taskViewModel.error.collectAsState()

                    // Cargar tareas si aún no se han cargado
                    LaunchedEffect(isUserAuthenticated) {
                        if (tasks.isEmpty() && !isLoading && error == null) {
                            taskViewModel.loadTasks()
                        }
                    }

                    AppNavigation(
                        tasks = tasks,
                        onTasksChanged = { updatedList ->
                            taskViewModel.saveAll(updatedList)
                        },
                        taskViewModel = taskViewModel
                    )
                }
            }
        }
    }
}