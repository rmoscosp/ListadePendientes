package com.robinmp.listadependientes.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robinmp.listadependientes.data.model.Task
import com.robinmp.listadependientes.data.repository.FirebaseTaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class TaskViewModel : ViewModel() {

    private val repository = FirebaseTaskRepository()

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Para compatibilidad con diferentes pantallas
    val errorMessage: StateFlow<String?> = _error

    fun loadTasks() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                // Verificar autenticación antes de cargar
                if (!repository.isUserAuthenticated()) {
                    _error.value = "Usuario no autenticado"
                    return@launch
                }

                _tasks.value = repository.getTasks()
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al cargar tareas"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveTask(task: Task) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                if (!repository.isUserAuthenticated()) {
                    _error.value = "Usuario no autenticado"
                    return@launch
                }

                // Asignar ID si no lo tiene (nueva tarea)
                val taskToSave = if (task.id.isEmpty()) {
                    task.copy(
                        id = UUID.randomUUID().toString(),
                        createdAt = System.currentTimeMillis()
                    )
                } else {
                    task.copy(updatedAt = System.currentTimeMillis())
                }

                repository.saveTask(taskToSave)
                loadTasks() // Recargar lista
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al guardar tarea"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                if (!repository.isUserAuthenticated()) {
                    _error.value = "Usuario no autenticado"
                    return@launch
                }

                repository.deleteTask(taskId)
                loadTasks() // Recargar lista
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al eliminar tarea"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Función para compatibilidad con TodoListScreen
     */
    fun toggleTaskCompletion(taskId: String) {
        viewModelScope.launch {
            try {
                val currentTasks = _tasks.value
                val taskToUpdate = currentTasks.find { it.id == taskId }

                if (taskToUpdate != null) {
                    val updatedTask = taskToUpdate.copy(
                        isCompleted = !taskToUpdate.isCompleted,
                        updatedAt = System.currentTimeMillis()
                    )
                    saveTask(updatedTask)
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al actualizar tarea"
            }
        }
    }

    /**
     * Función para refrescar las tareas
     */
    fun refreshTasks() {
        loadTasks()
    }

    fun saveAll(tasks: List<Task>) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                if (!repository.isUserAuthenticated()) {
                    _error.value = "Usuario no autenticado"
                    return@launch
                }

                tasks.forEach { task ->
                    val taskToSave = if (task.id.isEmpty()) {
                        task.copy(id = UUID.randomUUID().toString())
                    } else {
                        task
                    }
                    repository.saveTask(taskToSave)
                }
                loadTasks()
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al guardar tareas"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    /**
     * Verifica si el usuario está autenticado
     */
    fun isUserAuthenticated(): Boolean {
        return repository.isUserAuthenticated()
    }
}