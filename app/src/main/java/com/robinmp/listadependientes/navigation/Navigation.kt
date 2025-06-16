package com.robinmp.listadependientes.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.robinmp.listadependientes.data.model.Task
import com.robinmp.listadependientes.ui.screens.AddTaskScreen
import com.robinmp.listadependientes.ui.screens.EditTaskScreen
import com.robinmp.listadependientes.ui.screens.TodoListScreen
import com.robinmp.listadependientes.viewmodels.TaskViewModel

@Composable
fun AppNavigation(
    tasks: List<Task>,
    onTasksChanged: (List<Task>) -> Unit,
    taskViewModel: TaskViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "taskList") {

        composable("taskList") {
            TodoListScreen(
                navController = navController,
                tasks = tasks,
                onTaskUpdate = { updatedTasks ->
                    onTasksChanged(updatedTasks)
                },
                onAddTaskClick = {
                    navController.navigate("addTask")
                },
                onSignOut = {
                    com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                }
            )
        }

        composable("addTask") {
            AddTaskScreen(
                onSave = { task ->
                    taskViewModel.saveTask(task)
                    taskViewModel.loadTasks()
                    navController.popBackStack()
                },
                onCancel = { navController.popBackStack() }
            )
        }

        composable("editTask/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")
            val taskToEdit = tasks.find { it.id == taskId }

            if (taskToEdit != null) {
                EditTaskScreen(
                    task = taskToEdit,
                    onSave = { updatedTask ->
                        taskViewModel.saveTask(updatedTask)
                        taskViewModel.loadTasks()
                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() }
                )
            }
        }
    }
}
