package com.robinmp.listadependientes.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robinmp.listadependientes.data.model.Task
import java.text.SimpleDateFormat
import androidx.compose.ui.Alignment
import java.util.*

@Composable
fun TaskItem(
    task: Task,
    onCheckChange: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(2.dp, shape = MaterialTheme.shapes.medium),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = task.isDone,
                    onCheckedChange = onCheckChange
                )
                Text(
                    text = task.title,
                    modifier = Modifier.weight(1f),
                    style = TextStyle(
                        fontSize = 16.sp,
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

            Spacer(modifier = Modifier.height(8.dp))

            if (task.description.isNotBlank()) {
                Text(text = "📋 ${task.description}", fontSize = 14.sp)
            }

            Text(text = "📈 Avance: ${task.progress}%", fontSize = 12.sp)
            Text(text = "🕒 Creado: ${task.createdAt.toFormattedDate()}", fontSize = 12.sp)
            Text(
                text = "📍 Ubicación: ${task.location?.latitude ?: 0.0}, ${task.location?.longitude ?: 0.0}",
                fontSize = 12.sp
            )
        }
    }
}

private fun Long.toFormattedDate(): String {
    val format = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.getDefault())
    return format.format(Date(this))
}