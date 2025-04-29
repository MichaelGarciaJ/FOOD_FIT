package com.mariana.foodfit.data.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
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
     * @return Usuario creado o null si falla (por ejemplo, email duplicado).
     * @throws FirebaseAuthUserCollisionException si el correo ya existe.
     */
    suspend fun register(nombre: String, correo: String, contrasena: String): Usuario? {
        try {
            val result = auth.createUserWithEmailAndPassword(correo, contrasena).await()
            val uid = result.user?.uid ?: return null

            val usuario = Usuario(idUsuario = uid, nombre = nombre, correo = correo)
            usuariosCollection.document(uid).set(usuario).await()

            return usuario

        } catch (e: FirebaseAuthUserCollisionException) {
            // Este error ocurre si el correo ya está en uso
            throw e
        }
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
     * Método que obtiene los datos del usuario autenticado actualmente.
     *
     * @return Usuario actual o null si no hay sesión iniciada.
     */
    suspend fun getCurrentUser(): Usuario? {
        val uid = auth.currentUser?.uid ?: return null
        val snapshot = usuariosCollection.document(uid).get().await()
        return snapshot.toObject(Usuario::class.java)
    }

    /**
     * Método que cierra la sesión del usuario actual.
     */
    fun logout() {
        auth.signOut()
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

    // ====================================================
    // MÉTODOS DE AUTENTICACIÓN CON GOOGLE
    // ====================================================

    /**
     * Método que permite el inicio de sesión mediante una cuenta de Google.
     *
     * @param idToken Token de autenticación proporcionado por Google Sign-In.
     * @return Objeto Usuario con los datos del usuario o null si falla la autenticación.
     */
    suspend fun signInWithGoogle(idToken: String): Usuario? {
        // Construir credencial de Firebase
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        // Autenticar en FirebaseAuth
        val result = auth.signInWithCredential(credential).await()
        val firebaseUser = result.user ?: return null

        // Si es la primera vez que entra, crea su documento en Firestore
        if (result.additionalUserInfo?.isNewUser == true) {
            val photoUrl = firebaseUser.photoUrl?.toString() // puede ser null

            val usuario = Usuario(
                idUsuario = firebaseUser.uid,
                nombre = firebaseUser.displayName ?: "",
                correo = firebaseUser.email ?: "",
                photoUrl = photoUrl
            )
            usuariosCollection.document(firebaseUser.uid).set(usuario).await()
            return usuario
        }

        // Si ya existía, simplemente lo recuperas
        val snapshot = usuariosCollection.document(firebaseUser.uid).get().await()
        return snapshot.toObject(Usuario::class.java)
    }

}