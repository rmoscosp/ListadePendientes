package com.robinmp.listadependientes.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robinmp.listadependientes.data.model.Task
import com.robinmp.listadependientes.data.repository.FirebaseTaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {

    private val repository = FirebaseTaskRepository()

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // ❌ REMOVIDO: No cargar tareas automáticamente en init
    // init {
    //     loadTasks()
    // }

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

                repository.saveTask(task)
                loadTasks()
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
                loadTasks()
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al eliminar tarea"
            } finally {
                _isLoading.value = false
            }
        }
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

                tasks.forEach { repository.saveTask(it) }
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
}