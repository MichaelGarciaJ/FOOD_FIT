package com.mariana.foodfit.data.entity

import com.google.firebase.Timestamp

/**
 * Modelo de datos que representa un comentario hecho por un usuario sobre un platillo.
 */
data class Comentario(
    val id: String = "",
    val uid: String = "",
    val texto: String = "",
    val fecha: Timestamp? = null,
    val nombreUsuario: String = "",
    val fotoUsuario: String = "",
    val platilloId: String = ""
)

