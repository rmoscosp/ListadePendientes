package com.robinmp.listadependientes.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.robinmp.listadependientes.ui.screens.*
import com.robinmp.listadependientes.util.SecurityManager
import com.robinmp.listadependientes.viewmodels.TaskViewModel
import com.robinmp.listadependientes.data.model.Task

@Composable
fun AppNavigation(
    navController: NavHostController,
    securityManager: SecurityManager
) {
    // Determinar pantalla inicial basada en autenticación
    val startDestination = if (securityManager.isUserLoggedIn()) "task_list" else "signin"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Pantalla de login (no requiere autenticación)
        composable("signin") {
            SignInScreen(
                onNavigateToTaskList = {
                    navController.navigate("task_list") {
                        popUpTo("signin") { inclusive = true }
                    }
                }
            )
        }

        // Lista de tareas (requiere autenticación)
        composable("task_list") {
            // Verificar autenticación
            LaunchedEffect(Unit) {
                if (!securityManager.isUserLoggedIn()) {
                    navController.navigate("signin") {
                        popUpTo("task_list") { inclusive = true }
                    }
                }
            }

            if (securityManager.isUserLoggedIn()) {
                val taskViewModel: TaskViewModel = viewModel()

                // Inicializar ViewModel cuando el usuario esté autenticado
                LaunchedEffect(Unit) {
                    taskViewModel.loadTasks()
                }

                CompleteTodoListScreen(
                    taskViewModel = taskViewModel,
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

        // Agregar tarea (requiere autenticación)
        composable("add_task") {
            LaunchedEffect(Unit) {
                if (!securityManager.isUserLoggedIn()) {
                    navController.navigate("signin")
                }
            }

            if (securityManager.isUserLoggedIn()) {
                val taskViewModel: TaskViewModel = viewModel()

                AddTaskScreen(
                    onSave = { task ->
                        taskViewModel.saveTask(task)
                        navController.popBackStack()
                    },
                    onCancel = {
                        navController.popBackStack()
                    },
                    taskViewModel = taskViewModel,
                    isLoading = false,
                    error = null,
                    onClearError = {}
                )
            }
        }

        // Editar tarea (requiere autenticación)
        composable("edit_task/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""

            LaunchedEffect(Unit) {
                if (!securityManager.isUserLoggedIn()) {
                    navController.navigate("signin")
                }
            }

            if (securityManager.isUserLoggedIn()) {
                val taskViewModel: TaskViewModel = viewModel()
                val tasks by taskViewModel.tasks.collectAsState()
                val taskToEdit = tasks.find { it.id == taskId }

                if (taskToEdit != null) {
                    EditTaskScreen(
                        task = taskToEdit,
                        onSave = { updatedTask ->
                            taskViewModel.saveTask(updatedTask)
                            navController.popBackStack()
                        },
                        onCancel = {
                            navController.popBackStack()
                        },
                        taskViewModel = taskViewModel,
                        isLoading = false,
                        error = null,
                        onClearError = {}
                    )
                } else {
                    // Si la tarea no existe, volver a la lista
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}