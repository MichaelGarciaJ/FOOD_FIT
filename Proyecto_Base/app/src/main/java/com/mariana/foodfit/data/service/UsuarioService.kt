package com.mariana.foodfit.data.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.mariana.foodfit.data.entity.Usuario
import kotlinx.coroutines.tasks.await

/**
 * Servicio que gestiona la autenticación y el almacenamiento de usuarios
 * en Firebase Authentication y Firestore.
 */
class UsuarioService {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val usuariosCollection = db.collection("usuarios")

    // ====================================================
    // MÉTODOS DE AUTENTICACIÓN CON FIREBASE AUTH
    // ====================================================

    /**
     * Método que registra un nuevo usuario con correo y contraseña en Firebase Auth
     * y guarda sus datos en Firestore.
     *
     * @param nombre Nombre del usuario.
     * @param correo Correo electrónico.
     * @param contrasena Contraseña.
     * @return Objeto Usuario creado o null si falla el registro.
     */
    suspend fun register(nombre: String, correo: String, contrasena: String): Usuario? {
        val result = auth.createUserWithEmailAndPassword(correo, contrasena).await()
        val uid = result.user?.uid ?: return null

        val usuario = Usuario(idUsuario = uid, nombre = nombre, correo = correo)
        usuariosCollection.document(uid).set(usuario).await()

        return usuario
    }

    /**
     * Método que inicia sesión con las credenciales del usuario y recupera sus datos desde Firestore.
     *
     * @param correo Correo electrónico.
     * @param contrasena Contraseña.
     * @return Usuario autenticado o null si falla.
     */
    suspend fun login(correo: String, contrasena: String): Usuario? {
        val result = auth.signInWithEmailAndPassword(correo, contrasena).await()
        val uid = result.user?.uid ?: return null

        val snapshot = usuariosCollection.document(uid).get().await()
        return snapshot.toObject(Usuario::class.java)
    }

    /**
     * Método que cierra la sesión del usuario actual.
     */
    fun logout() {
        auth.signOut()
    }

    /**
     * Método que obtiene los datos del usuario autenticado actualmente.
     *
     * @return Usuario actual o null si no hay sesión iniciada.
     */
    suspend fun getCurrentUser(): Usuario? {
        val uid = auth.currentUser?.uid ?: return null
        val snapshot = usuariosCollection.document(uid).get().await()
        return snapshot.toObject(Usuario::class.java)
    }

    // ====================================================
    // MÉTODOS DE CRUD EN FIRESTORE
    // ====================================================

    /**
     * Método que recupera todos los usuarios de la colección.
     * @return Lista de usuarios.
     */
    suspend fun getAll(): List<Usuario> {
        val snapshot: QuerySnapshot = usuariosCollection.get().await()
        return snapshot.documents.mapNotNull { it.toObject(Usuario::class.java) }
    }

    /**
     * Método que recupera un usuario específico desde Firestore usando su ID.
     *
     * @param id ID del usuario (UID de Firebase Auth).
     * @return Usuario correspondiente o null si no existe.
     */
    suspend fun getById(id: String): Usuario? {
        val snapshot = usuariosCollection.document(id).get().await()
        return snapshot.toObject(Usuario::class.java)
    }

    /**
     * Método que actualiza los datos de un usuario existente en Firestore.
     *
     * @param usuario Objeto Usuario con los nuevos datos.
     */
    suspend fun update(usuario: Usuario) {
        val uid = usuario.idUsuario
            .takeIf { it.isNotBlank() }
            ?: throw IllegalArgumentException("ID de usuario inválido")

        // Actualizar solo los campos del mapa
        val data = mapOf(
            "nombre" to usuario.nombre,
            "correo" to usuario.correo,
            "photoUrl" to usuario.photoUrl,
            "createdAt" to usuario.createdAt
        )
        usuariosCollection.document(uid).update(data).await()
    }

    /**
     * Método que elimina un usuario de Firestore por su ID.
     *
     * @param id ID del usuario (UID de Firebase Auth).
     */
    suspend fun delete(id: String) {
        usuariosCollection.document(id).delete().await()
    }

}