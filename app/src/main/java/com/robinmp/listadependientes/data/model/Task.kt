package com.robinmp.listadependientes.data.model

import java.util.*

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val progress: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val location: GeoLocation? = null,
    val isDone: Boolean = false
)

data class GeoLocation(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)