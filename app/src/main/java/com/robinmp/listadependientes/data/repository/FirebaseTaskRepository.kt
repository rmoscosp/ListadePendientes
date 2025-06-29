package com.robinmp.listadependientes.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.robinmp.listadependientes.data.model.Task
import kotlinx.coroutines.tasks.await

class FirebaseTaskRepository {

    private val db = FirebaseFirestore.getInstance()

    // Método para verificar si el usuario está autenticado
    fun isUserAuthenticated(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    // 🔒 UID del usuario autenticado
    private val userId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalStateException("Usuario no autenticado")

    private val tasksRef
        get() = db.collection("users").document(userId).collection("tasks")

    suspend fun saveTask(task: Task) {
        tasksRef.document(task.id).set(task).await()
    }

    suspend fun deleteTask(taskId: String) {
        tasksRef.document(taskId).delete().await()
    }

    suspend fun getTasks(): List<Task> {
        return tasksRef.get().await().toObjects(Task::class.java)
    }

    // Métodos adicionales para compatibilidad con el ViewModel
    suspend fun addTask(task: Task) {
        // Si el task no tiene ID, generamos uno nuevo
        val taskToSave = if (task.id.isBlank()) {
            val newId = tasksRef.document().id
            task.copy(id = newId)
        } else {
            task
        }
        saveTask(taskToSave)
    }

    suspend fun updateTask(task: Task) {
        if (task.id.isBlank()) {
            throw IllegalArgumentException("Task ID no puede estar vacío")
        }
        saveTask(task)
    }
}