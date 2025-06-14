package com.robinmp.listadependientes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration

import java.util.*

data class Task(val id: UUID = UUID.randomUUID(), val title: String, var isDone: Boolean = false)

class MainActivity : ComponentActivity() {
    private lateinit var taskStorage: TaskStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskStorage = TaskStorage(this)

        var tasks by mutableStateOf(taskStorage.loadTasks())

        setContent {
            ListaDePendientesTheme {
            MyTodoApp(
                tasks = tasks,
                onTasksChanged = {
                    tasks = it
                    taskStorage.saveTasks(it)
                }
            )
            }

        }
    }
}

@Composable
fun MyTodoApp(
    tasks: List<Task>,
    onTasksChanged: (List<Task>) -> Unit
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "taskList") {
        composable("taskList") {
            TodoListScreen(
                tasks = tasks,
                onTaskUpdate = onTasksChanged,
                onAddTaskClick = { navController.navigate("addTask") }
            )
        }
        composable("addTask") {
            AddTaskScreen(
                onSave = { title ->
                    val newList = tasks + Task(title = title)
                    onTasksChanged(newList)
                    navController.popBackStack()
                },
                onCancel = { navController.popBackStack() }
            )
        }
    }
}
@Preview(showSystemUi =  true)
@Composable
fun PreviewMyTodoApp( ){
    val sampleTasks = listOf(
        Task(title = "Comprar leche"),
        Task(title = "Hacer ejercicio", isDone = true),
        Task(title = "Leer un libro"),
        Task(title = "Comprar leche"),
        Task(title = "Hacer ejercicio", isDone = true),
        Task(title = "Leer un libro"),
        Task(title = "Comprar leche"),
        Task(title = "Hacer ejercicio", isDone = true),
        Task(title = "Leer un libro"),
        Task(title = "Comprar leche"),
        Task(title = "Hacer ejercicio", isDone = true),
        Task(title = "Leer un libro"),
        Task(title = "Comprar leche"),
        Task(title = "Hacer ejercicio", isDone = true),
        Task(title = "Leer un libro")
    )
    ListaDePendientesTheme {
        MyTodoApp(
            tasks = sampleTasks,
            onTasksChanged = {}
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    TopAppBar(
        title = { Text(text = title) },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    )
}


@Composable
fun TaskItem(task: Task, onCheckChange: (Boolean) -> Unit, onDelete: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Checkbox(
            checked = task.isDone,
            onCheckedChange = onCheckChange
        )
        Text(
            text = task.title,
            modifier = Modifier.weight(1f),
            style = TextStyle(
                textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None
            )
        )
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Eliminar tarea")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    tasks: List<Task>,
    onTaskUpdate: (List<Task>) -> Unit,
    onAddTaskClick: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            MyTopAppBar(
                title = "Lista de Pendientes",
                scrollBehavior = scrollBehavior
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
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    onSave: (String) -> Unit,
    onCancel: () -> Unit
) {
    var taskTitle by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            MyTopAppBar(
                title = "Lista de Pendientes",
                scrollBehavior = null
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onCancel) {
                    Text("Cancelar")
                }
                Button(
                    onClick = { if (taskTitle.isNotBlank()) onSave(taskTitle) },
                    enabled = taskTitle.isNotBlank()
                ) {
                    Text("Guardar")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = taskTitle,
                onValueChange = { taskTitle = it },
                label = { Text("Título de la tarea") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


