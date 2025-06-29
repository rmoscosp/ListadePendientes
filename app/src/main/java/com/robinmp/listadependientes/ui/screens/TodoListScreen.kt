package com.robinmp.listadependientes.ui.screens

/*
// TEMPORALMENTE COMENTADO PARA PRUEBAS DE SEGURIDAD
// Descomenta cuando quieras trabajar en la funcionalidad completa

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.robinmp.listadependientes.data.model.Task
import com.robinmp.listadependientes.ui.components.TaskItem
import com.robinmp.listadependientes.viewmodels.TaskViewModel
import com.robinmp.listadependientes.util.SecurityManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    onNavigateToAddTask: () -> Unit,
    onNavigateToEditTask: (String) -> Unit,
    onLogout: () -> Unit,
    taskViewModel: TaskViewModel = viewModel()
) {
    // Contenido comentado temporalmente
    Text("TodoListScreen temporalmente deshabilitado para pruebas de seguridad")
}

@Composable
fun SecureTaskItem(
    task: Task,
    onTaskClick: () -> Unit,
    onTaskToggle: () -> Unit,
    onTaskDelete: () -> Unit
) {
    // Contenido comentado temporalmente
    Text("SecureTaskItem temporalmente deshabilitado")
}
*/

// Versión temporal simplificada para que compile
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun TodoListScreen(
    onNavigateToAddTask: () -> Unit = {},
    onNavigateToEditTask: (String) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    Text("TodoListScreen - En desarrollo")
}

@Composable
fun SecureTaskItem(
    task: Any,
    onTaskClick: () -> Unit = {},
    onTaskToggle: () -> Unit = {},
    onTaskDelete: () -> Unit = {}
) {
    Text("SecureTaskItem - En desarrollo")
}