package com.robinmp.listadependientes.ui.screens

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
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robinmp.listadependientes.viewmodels.TaskViewModel
import com.robinmp.listadependientes.data.model.Task
import com.robinmp.listadependientes.util.SecurityManager
import com.robinmp.listadependientes.ui.components.TaskItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteTodoListScreen(
    taskViewModel: TaskViewModel,
    onNavigateToAddTask: () -> Unit,
    onNavigateToEditTask: (String) -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val securityManager = remember { SecurityManager(context) }

    // Estados del ViewModel
    val tasks by taskViewModel.tasks.collectAsState()
    val isLoading by taskViewModel.isLoading.collectAsState()
    val error by taskViewModel.error.collectAsState()

    // Estados locales
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showSecurityInfo by remember { mutableStateOf(false) }

    // Información del usuario
    val username = securityManager.getUsername() ?: "Usuario"
    val fullName = securityManager.getUserFullName() ?: username

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // TopAppBar con información del usuario y controles
        TopAppBar(
            title = {
                Column {
                    Text("Lista de Tareas")
                    Text(
                        text = "👤 $fullName",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            actions = {
                // Botón de información de seguridad
                IconButton(onClick = { showSecurityInfo = true }) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Información de Seguridad",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Botón de cerrar sesión
                IconButton(onClick = { showLogoutDialog = true }) {
                    Icon(
                        Icons.Default.ExitToApp,
                        contentDescription = "Cerrar Sesión",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        // Indicador de seguridad
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = "Seguro",
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "🔒 Datos protegidos con cifrado AES + SHA-256",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }

        // Contenido principal
        Box(modifier = Modifier.weight(1f)) {
            when {
                isLoading -> {
                    // Indicador de carga
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Cargando tareas...")
                        }
                    }
                }

                error != null -> {
                    // Pantalla de error
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "Error al cargar tareas",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        Text(
                            text = "error",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Button(
                            onClick = {
                                taskViewModel.clearError()
                                taskViewModel.loadTasks()
                            },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Reintentar")
                        }
                    }
                }

                tasks.isEmpty() -> {
                    // Pantalla vacía
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Sin tareas",
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = "¡Comienza a organizarte!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        Text(
                            text = "No tienes tareas aún. Agrega tu primera tarea para comenzar.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 32.dp, top = 8.dp, end = 32.dp)
                        )
                        Button(
                            onClick = onNavigateToAddTask,
                            modifier = Modifier.padding(top = 24.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Agregar Primera Tarea")
                        }
                    }
                }

                else -> {
                    // Lista de tareas
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(tasks) { task ->
                            TaskItem(
                                task = task,
                                onCheckChange = { _ ->
                                    // Crear tarea actualizada con estado cambiado
                                    val updatedTask = task.copy(
                                        isCompleted = !task.isCompleted,
                                        updatedAt = System.currentTimeMillis()
                                    )
                                    taskViewModel.saveTask(updatedTask)
                                },
                                onEdit = {
                                    onNavigateToEditTask(task.id)
                                },
                                onDelete = {
                                    taskViewModel.deleteTask(task.id)
                                }
                            )
                        }

                        // Espaciado adicional al final para el FAB
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }

        // Botón flotante para agregar tarea
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAddTask,
                icon = { Icon(Icons.Default.Add, contentDescription = "Agregar") },
                text = { Text("Nueva Tarea") },
                containerColor = MaterialTheme.colorScheme.primary
            )
        }
    }

    // Diálogo de confirmación de logout
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro de que quieres cerrar sesión?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text("Sí, Cerrar Sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo de información de seguridad
    if (showSecurityInfo) {
        AlertDialog(
            onDismissRequest = { showSecurityInfo = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Seguridad Activa")
                }
            },
            text = {
                Column {
                    Text("✅ Autenticación local obligatoria")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("✅ Contraseñas cifradas con SHA-256")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("✅ Datos de tareas cifrados con AES")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("✅ Acceso controlado por usuario")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("✅ Almacenamiento seguro local")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Usuario activo: $username",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showSecurityInfo = false }) {
                    Text("Entendido")
                }
            }
        )
    }
}