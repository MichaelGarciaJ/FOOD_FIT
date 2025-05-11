package com.mariana.foodfit.data.entity

/**
 * Modelo de datos para representar un usuario en Firebase.
 * Cada instancia de Usuario se corresponderá con un documento en la colección "usuarios".
 */
data class Usuario(
    val idUsuario: String = "",
    val nombre: String = "",
    val correo: String = "",
    val photoUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)