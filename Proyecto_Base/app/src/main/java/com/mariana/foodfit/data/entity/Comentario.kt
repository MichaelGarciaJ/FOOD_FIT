package com.mariana.foodfit.data.entity

import com.google.firebase.Timestamp

data class Comentario(
    val id: String = "",
    val uid: String = "",
    val texto: String = "",
    val fecha: Timestamp? = null,
    val nombreUsuario: String = "",
    val fotoUsuario: String = "",
    val platilloId: String = ""
)

