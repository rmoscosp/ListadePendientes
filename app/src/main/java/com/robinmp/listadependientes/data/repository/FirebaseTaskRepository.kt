package com.robinmp.listadependientes.data.repository


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.robinmp.listadependientes.data.model.Task
import kotlinx.coroutines.tasks.await

class FirebaseTaskRepository {

    private val db = FirebaseFirestore.getInstance()

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
}