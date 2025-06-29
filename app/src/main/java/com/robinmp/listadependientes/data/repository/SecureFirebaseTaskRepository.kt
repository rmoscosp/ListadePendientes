package com.robinmp.listadependientes.data.repository

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.robinmp.listadependientes.data.model.Task
import com.robinmp.listadependientes.util.SecurityManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class SecureFirebaseTaskRepository(
    private val context: Context
) {
    private val firestore = FirebaseFirestore.getInstance()
    private val securityManager = SecurityManager(context)
    private val tasksCollection = firestore.collection("tasks")

    /**
     * Obtiene todas las tareas del usuario autenticado
     */
    fun getTasks(): Flow<List<Task>> = callbackFlow {
        val username = securityManager.getUsername()
        if (username == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = tasksCollection
            .whereEqualTo("userId", username)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val tasks = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val task = doc.toObject(Task::class.java)?.copy(id = doc.id)
                        task?.let {
                            // Descifrar datos sensibles usando las funciones correctas
                            it.copy(
                                title = securityManager.decryptTaskData(it.title),
                                description = securityManager.decryptTaskData(it.description)
                            )
                        }
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(tasks)
            }

        awaitClose { listener.remove() }
    }

    /**
     * Obtiene una tarea específica por ID
     */
    suspend fun getTaskById(taskId: String): Task? {
        return try {
            val username = securityManager.getUsername() ?: return null

            val document = tasksCollection.document(taskId).get().await()
            if (document.exists()) {
                val task = document.toObject(Task::class.java)?.copy(id = document.id)
                // Verificar que la tarea pertenece al usuario autenticado
                if (task?.userId == username) {
                    task.copy(
                        title = securityManager.decryptTaskData(task.title),
                        description = securityManager.decryptTaskData(task.description)
                    )
                } else {
                    null // No autorizado
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Agrega una nueva tarea
     */
    suspend fun addTask(task: Task): Result<String> {
        return try {
            val username = securityManager.getUsername()
            if (username == null) {
                return Result.failure(Exception("Usuario no autenticado"))
            }

            // Cifrar datos sensibles antes de guardar usando las funciones correctas
            val encryptedTask = task.copy(
                userId = username,
                title = securityManager.encryptTaskData(task.title),
                description = securityManager.encryptTaskData(task.description),
                createdAt = System.currentTimeMillis()
            )

            val documentRef = tasksCollection.add(encryptedTask).await()
            Result.success(documentRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza una tarea existente
     */
    suspend fun updateTask(task: Task): Result<Unit> {
        return try {
            val username = securityManager.getUsername()
            if (username == null) {
                return Result.failure(Exception("Usuario no autenticado"))
            }

            // Verificar que la tarea pertenece al usuario
            val existingTask = getTaskById(task.id)
            if (existingTask == null || existingTask.userId != username) {
                return Result.failure(Exception("No autorizado para modificar esta tarea"))
            }

            // Cifrar datos sensibles antes de actualizar usando las funciones correctas
            val encryptedTask = task.copy(
                title = securityManager.encryptTaskData(task.title),
                description = securityManager.encryptTaskData(task.description),
                updatedAt = System.currentTimeMillis()
            )

            tasksCollection.document(task.id).set(encryptedTask).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina una tarea
     */
    suspend fun deleteTask(taskId: String): Result<Unit> {
        return try {
            val username = securityManager.getUsername()
            if (username == null) {
                return Result.failure(Exception("Usuario no autenticado"))
            }

            // Verificar que la tarea pertenece al usuario
            val existingTask = getTaskById(taskId)
            if (existingTask == null || existingTask.userId != username) {
                return Result.failure(Exception("No autorizado para eliminar esta tarea"))
            }

            tasksCollection.document(taskId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Marca una tarea como completada/incompleta
     */
    suspend fun toggleTaskCompletion(taskId: String): Result<Unit> {
        return try {
            val username = securityManager.getUsername()
            if (username == null) {
                return Result.failure(Exception("Usuario no autenticado"))
            }

            val existingTask = getTaskById(taskId)
            if (existingTask == null || existingTask.userId != username) {
                return Result.failure(Exception("No autorizado para modificar esta tarea"))
            }

            val updatedTask = existingTask.copy(
                isCompleted = !existingTask.isCompleted,
                updatedAt = System.currentTimeMillis()
            )

            updateTask(updatedTask)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Verifica si el usuario está autenticado
     */
    fun isUserAuthenticated(): Boolean {
        return securityManager.isUserLoggedIn()
    }

    /**
     * Cierra sesión del usuario
     */
    fun logout(): Result<Unit> {
        return try {
            securityManager.logout()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}