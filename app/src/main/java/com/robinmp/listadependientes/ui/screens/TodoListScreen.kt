package com.robinmp.listadependientes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.robinmp.listadependientes.data.model.Task
import com.robinmp.listadependientes.ui.components.MyTopAppBar
import com.robinmp.listadependientes.ui.components.TaskItem
import com.robinmp.listadependientes.viewmodels.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    navController: NavHostController,
    tasks: List<Task>,
    onTaskUpdate: (List<Task>) -> Unit,
    onAddTaskClick: () -> Unit,
    onSignOut: () -> Unit,
    taskViewModel: TaskViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            MyTopAppBar(
                title = "Lista de Pendientes",
                scrollBehavior = scrollBehavior,
                onSignOut = onSignOut
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTaskClick) {
                Icon(Icons.Default.Add, contentDescription = "Agregar tarea")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            items(tasks, key = { it.id }) { task ->
                TaskItem(
                    task = task,
                    onCheckChange = { isChecked ->
                        val updated = task.copy(
                            isDone = isChecked,
                            progress = if (isChecked) 100 else task.progress,
                            updatedAt = System.currentTimeMillis()
                        )
                        taskViewModel.saveTask(updated)
                        //taskViewModel.loadTasks()
                    },
                    onDelete = {
                        taskViewModel.deleteTask(task.id)
                        taskViewModel.loadTasks()
                    },
                    onEdit = {
                        navController.navigate("editTask/${task.id}")
                    }
                )
            }
        }
    }
}