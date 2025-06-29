package com.robinmp.listadependientes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.robinmp.listadependientes.data.model.GeoLocation
import com.robinmp.listadependientes.data.model.Task
import com.robinmp.listadependientes.ui.components.MyTopAppBar
import com.robinmp.listadependientes.viewmodels.TaskViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    onSave: (Task) -> Unit,
    onCancel: () -> Unit,
    taskViewModel: TaskViewModel? = null,
    isLoading: Boolean = false,
    error: String? = null,
    onClearError: () -> Unit = {}
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var progress by remember { mutableStateOf(0) }
    // Puedes usar valores reales si obtienes ubicación
    val dummyLocation = GeoLocation(latitude = 0.0, longitude = 0.0)

    // Mostrar error si existe
    error?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            kotlinx.coroutines.delay(3000)
            onClearError()
        }
    }

    Scaffold(
        topBar = { MyTopAppBar("Nueva Tarea") },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onCancel,
                    enabled = !isLoading
                ) {
                    Text("Cancelar")
                }
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val task = Task(
                                title = title,
                                description = description,
                                progress = progress,
                                location = dummyLocation
                                // id, createdAt y updatedAt se generan automáticamente
                            )
                            onSave(task)
                        }
                    },
                    enabled = title.isNotBlank() && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Guardar")
                    }
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
            // Mostrar error si existe
            error?.let { errorMessage ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
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

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )
            Text("Progreso: $progress%")
            Slider(
                value = progress.toFloat(),
                onValueChange = { progress = it.toInt() },
                valueRange = 0f..100f,
                steps = 9,
                enabled = !isLoading
            )
        }
    }
}