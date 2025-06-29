package com.robinmp.listadependientes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
    taskViewModel: TaskViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    isLoading: Boolean = false,
    error: String? = null,
    onClearError: () -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    // Mostrar error si existe
    error?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            // El error se mostrará en la UI y se limpiará automáticamente
            kotlinx.coroutines.delay(3000)
            onClearError()
        }
    }

    Scaffold(
        topBar = {
            MyTopAppBar(
                title = "Lista de Pendientes",
                scrollBehavior = scrollBehavior,
                onSignOut = onSignOut
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTaskClick,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar tarea")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Mostrar error si existe
            error?.let { errorMessage ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = onClearError) {
                            Text("Cerrar")
                        }
                    }
                }
            }

            // Mostrar indicador de carga
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // Lista de tareas
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (tasks.isEmpty() && !isLoading && error == null) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "¡No hay tareas!",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Agrega tu primera tarea tocando el botón +",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
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
                                // No necesitas loadTasks() aquí porque saveTask() ya lo hace
                            },
                            onDelete = {
                                taskViewModel.deleteTask(task.id)
                                // No necesitas loadTasks() aquí porque deleteTask() ya lo hace
                            },
                            onEdit = {
                                navController.navigate("editTask/${task.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}