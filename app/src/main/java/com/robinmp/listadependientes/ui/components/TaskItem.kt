package com.robinmp.listadependientes.ui.components

import com.robinmp.listadependientes.data.model.Task
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import java.util.Date

@Composable
fun TaskItem(task: Task,
             onCheckChange: (Boolean) -> Unit,
             onDelete: () -> Unit,
             onEdit: () -> Unit,
             ) {
    Column(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = task.isDone, onCheckedChange = onCheckChange)
            Text(
                text = task.title,
                modifier = Modifier.weight(1f),
                style = TextStyle(
                    textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None
                )
            )
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Editar tarea")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar tarea")
            }
        }

        if (task.description.isNotBlank()) {
            Text(text = "Descripción: ${task.description}", fontSize = 12.sp)
        }

        Text(text = "Avance: ${task.progress}%", fontSize = 12.sp)
        Text(text = "Creado: ${Date(task.createdAt)}", fontSize = 10.sp)
        Text(text = "Ubicación: ${task.location?.latitude}, ${task.location?.longitude}", fontSize = 10.sp)
    }
}

