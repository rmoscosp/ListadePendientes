package com.robinmp.listadependientes.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.robinmp.listadependientes.data.model.Task
import com.robinmp.listadependientes.ui.screens.AddTaskScreen
import com.robinmp.listadependientes.ui.screens.EditTaskScreen
import com.robinmp.listadependientes.ui.screens.TodoListScreen
import java.util.*

@Composable
fun AppNavigation(
    tasks: List<Task>,
    onTasksChanged: (List<Task>) -> Unit
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "taskList") {
        composable("taskList") {
            TodoListScreen(
                tasks = tasks,
                onTaskUpdate = onTasksChanged,
                onAddTaskClick = { navController.navigate("addTask") },
                navController = navController,onSignOut = {
                    FirebaseAuth.getInstance().signOut()
                }
            )
        }
        composable("addTask") {
            AddTaskScreen(
                onSave = { task ->
                    val newList = tasks + task
                    onTasksChanged(newList)
                    navController.popBackStack()
                },
                onCancel = { navController.popBackStack() }
            )
        }
        composable("editTask/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")?.let { UUID.fromString(it) }
            val task = tasks.find { it.id == taskId }

            if (task != null) {
                EditTaskScreen(
                    task = task,
                    onSave = { updatedTask ->
                        val updatedList = tasks.map {
                            if (it.id == updatedTask.id) updatedTask else it
                        }
                        onTasksChanged(updatedList)
                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() }
                )
            }
        }
    }
}