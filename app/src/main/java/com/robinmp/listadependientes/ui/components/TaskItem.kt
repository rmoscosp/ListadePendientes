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
                    checked = task.isCompleted,
                    onCheckedChange = onCheckChange
                )
                Text(
                    text = task.title,
                    modifier = Modifier.weight(1f),
                    style = TextStyle(
                        fontSize = 16.sp,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
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

            // Descripción si existe
            if (task.description.isNotBlank()) {
                Text(text = "📋 ${task.description}", fontSize = 14.sp)
            }

            // Progreso - mostrar solo si es mayor a 0
            if (task.progress > 0) {
                Text(text = "📈 Avance: ${task.progress}%", fontSize = 12.sp)
            }

            // Fecha de creación
            Text(text = "🕒 Creado: ${task.createdAt.toFormattedDate()}", fontSize = 12.sp)

            // Ubicación - mostrar solo si existe y no es 0,0
            task.location?.let { location ->
                if (location.latitude != 0.0 || location.longitude != 0.0) {
                    Text(
                        text = "📍 Lat: ${String.format("%.4f", location.latitude)}, Lng: ${String.format("%.4f", location.longitude)}",
                        fontSize = 12.sp
                    )
                }
            }

            // Prioridad si no es MEDIUM (por defecto)
            if (task.priority.value > 2) {
                Text(
                    text = "⚡ Prioridad: ${task.priority.displayName}",
                    fontSize = 12.sp,
                    color = when (task.priority.value) {
                        3 -> MaterialTheme.colorScheme.primary
                        4 -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        }
    }
}

private fun Long.toFormattedDate(): String {
    return try {
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        format.format(Date(this))
    } catch (e: Exception) {
        "Fecha inválida"
    }
}