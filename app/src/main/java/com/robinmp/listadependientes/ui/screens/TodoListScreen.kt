package com.robinmp.listadependientes.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.robinmp.listadependientes.data.model.Task
import com.robinmp.listadependientes.ui.components.MyTopAppBar
import com.robinmp.listadependientes.ui.components.TaskItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    navController: NavHostController,
    tasks: List<Task>,
    onTaskUpdate: (List<Task>) -> Unit,
    onAddTaskClick: () -> Unit,
    onSignOut: () -> Unit
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
                        onTaskUpdate(tasks.map {
                            if (it.id == task.id) it.copy(isDone = isChecked) else it
                        })
                    },
                    onDelete = {
                        onTaskUpdate(tasks.filter { it.id != task.id })
                    },
                    onEdit = {
                        navController.navigate("editTask/${task.id}")
                    }
                )
            }
        }
    }
}
