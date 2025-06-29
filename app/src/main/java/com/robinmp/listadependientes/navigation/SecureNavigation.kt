package com.robinmp.listadependientes.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.robinmp.listadependientes.ui.screens.*
import com.robinmp.listadependientes.util.RequireAuth
import com.robinmp.listadependientes.util.SecurityManager

@Composable
fun SecureNavigation(navController: NavHostController) {
    val context = LocalContext.current
    val securityManager = remember { SecurityManager(context) }

    // Determinar la pantalla inicial basada en el estado de autenticación
    val startDestination = if (securityManager.isUserLoggedIn()) "task_list" else "signin"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Pantalla de inicio de sesión (no requiere autenticación)
        composable("signin") {
            SignInScreen(
                onNavigateToTaskList = {
                    navController.navigate("task_list") {
                        popUpTo("signin") { inclusive = true }
                    }
                }
            )
        }

        // Pantalla de lista de tareas (requiere autenticación)
        composable("task_list") {
            RequireAuth(navController = navController) {
                TodoListScreen(
                    onNavigateToAddTask = {
                        navController.navigate("add_task")
                    },
                    onNavigateToEditTask = { taskId ->
                        navController.navigate("edit_task/$taskId")
                    },
                    onLogout = {
                        securityManager.logout()
                        navController.navigate("signin") {
                            popUpTo("task_list") { inclusive = true }
                        }
                    }
                )
            }
        }

        // Pantalla para agregar tarea (requiere autenticación)
        composable("add_task") {
            RequireAuth(navController = navController) {
                AddTaskScreen(
                    onSave = { task ->
                        // Aquí deberías llamar al ViewModel para guardar
                        navController.popBackStack()
                    },
                    onCancel = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // Pantalla para editar tarea (requiere autenticación)
        composable("edit_task/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            RequireAuth(navController = navController) {
                // Para EditTaskScreen necesitamos obtener la tarea por ID
                // Por ahora usamos una tarea vacía, pero deberías obtenerla del ViewModel
                val emptyTask = com.robinmp.listadependientes.data.model.Task(id = taskId)

                EditTaskScreen(
                    task = emptyTask,
                    onSave = { task ->
                        // Aquí deberías llamar al ViewModel para actualizar
                        navController.popBackStack()
                    },
                    onCancel = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}