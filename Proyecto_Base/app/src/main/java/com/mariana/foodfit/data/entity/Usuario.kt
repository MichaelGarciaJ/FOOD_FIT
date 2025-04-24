package com.mariana.foodfit.data.entity

/**
 * Modelo de datos para representar un usuario en Firebase.
 * Cada instancia de Usuario se corresponderá con un documento en la colección "usuarios".
 */
data class Usuario(
    val idUsuario: String = "", // ID único de Firebase Auth
    val nombre: String = "", // Nombre completo
    val correo: String = "", // Correo electrónico
    val photoUrl: String? = null, // URL de foto (opcional)
    val createdAt: Long = System.currentTimeMillis() // Fecha creación
)