package com.robinmp.listadependientes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.robinmp.listadependientes.data.model.GeoLocation
import com.robinmp.listadependientes.data.model.Task
import com.robinmp.listadependientes.ui.theme.components.MyTopAppBar
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    onSave: (Task) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var progress by remember { mutableStateOf(0) }
    // Puedes usar valores reales si obtienes ubicación
    val dummyLocation = GeoLocation(latitude = 0.0, longitude = 0.0)

    Scaffold(
        topBar = { MyTopAppBar("Nueva Tarea") },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onCancel) { Text("Cancelar") }
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val task = Task(
                                title = title,
                                description = description,
                                progress = progress,
                                location = dummyLocation,
                                createdAt = System.currentTimeMillis(),
                                updatedAt = System.currentTimeMillis()
                            )
                            onSave(task)
                        }
                    },
                    enabled = title.isNotBlank()
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
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )
            Text("Progreso: $progress%")
            Slider(
                value = progress.toFloat(),
                onValueChange = { progress = it.toInt() },
                valueRange = 0f..100f,
                steps = 9
            )
        }
    }
}
