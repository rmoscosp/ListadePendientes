package com.robinmp.listadependientes.data.model

import com.google.firebase.firestore.PropertyName

data class Task(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val category: String = "",
    val dueDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long? = null,

    // Campos para compatibilidad con versión anterior
    val progress: Int = 0,
    val location: GeoLocation? = null,

    // Campo de seguridad - ID del usuario propietario
    @PropertyName("userId")
    val userId: String = "",

    // Metadatos de seguridad
    @PropertyName("isEncrypted")
    val isEncrypted: Boolean = true,

    @PropertyName("lastAccessedAt")
    val lastAccessedAt: Long? = null
) {
    // Alias para compatibilidad con TaskItem.kt
    val isDone: Boolean
        get() = isCompleted

    // Constructor sin argumentos requerido por Firebase
    constructor() : this(
        id = "",
        title = "",
        description = "",
        isCompleted = false,
        priority = TaskPriority.MEDIUM,
        category = "",
        dueDate = null,
        createdAt = System.currentTimeMillis(),
        updatedAt = null,
        progress = 0,
        location = null,
        userId = "",
        isEncrypted = true,
        lastAccessedAt = null
    )

    /**
     * Verifica si la tarea tiene datos sensibles
     */
    fun hasSensitiveData(): Boolean {
        return title.isNotEmpty() || description.isNotEmpty()
    }

    /**
     * Verifica si la tarea está vencida
     */
    fun isOverdue(): Boolean {
        return dueDate != null && dueDate < System.currentTimeMillis() && !isCompleted
    }

    /**
     * Obtiene la edad de la tarea en días
     */
    fun getAgeInDays(): Long {
        return (System.currentTimeMillis() - createdAt) / (24 * 60 * 60 * 1000)
    }
}

enum class TaskPriority(val displayName: String, val value: Int) {
    LOW("Baja", 1),
    MEDIUM("Media", 2),
    HIGH("Alta", 3),
    URGENT("Urgente", 4);

    companion object {
        fun fromValue(value: Int): TaskPriority {
            return values().find { it.value == value } ?: MEDIUM
        }
    }
}

data class GeoLocation(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)