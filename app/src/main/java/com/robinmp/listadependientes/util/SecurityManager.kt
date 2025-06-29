package com.robinmp.listadependientes.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import android.util.Base64

class SecurityManager(private val context: Context) {

    companion object {
        private const val SHARED_PREFS_NAME = "secure_prefs"
        private const val ALGORITHM = "AES"
        private const val TRANSFORMATION = "AES/ECB/PKCS5Padding"
    }

    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val encryptedSharedPreferences: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            SHARED_PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Cifra una contraseña usando SHA-256
     */
    fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray(StandardCharsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Guarda credenciales de usuario de forma segura
     */
    fun saveUserCredentials(username: String, password: String, fullName: String) {
        val hashedPassword = hashPassword(password)

        encryptedSharedPreferences.edit().apply {
            putString("username", username)
            putString("password", hashedPassword)
            putString("fullName", fullName)
            putBoolean("isLoggedIn", false)
            apply()
        }
    }

    /**
     * Verifica las credenciales del usuario
     */
    fun verifyCredentials(username: String, password: String): Boolean {
        val storedUsername = encryptedSharedPreferences.getString("username", null)
        val storedPassword = encryptedSharedPreferences.getString("password", null)
        val hashedInputPassword = hashPassword(password)

        return storedUsername == username && storedPassword == hashedInputPassword
    }

    /**
     * Marca al usuario como autenticado
     */
    fun setUserLoggedIn(isLoggedIn: Boolean) {
        encryptedSharedPreferences.edit().putBoolean("isLoggedIn", isLoggedIn).apply()
    }

    /**
     * Verifica si el usuario está autenticado
     */
    fun isUserLoggedIn(): Boolean {
        return encryptedSharedPreferences.getBoolean("isLoggedIn", false)
    }

    /**
     * Obtiene el nombre completo del usuario
     */
    fun getUserFullName(): String? {
        return encryptedSharedPreferences.getString("fullName", null)
    }

    /**
     * Obtiene el nombre de usuario
     */
    fun getUsername(): String? {
        return encryptedSharedPreferences.getString("username", null)
    }

    // ✨ FUNCIONES FALTANTES - AGREGAR ESTAS ✨

    /**
     * Cifra datos sensibles de tareas
     */
    fun encryptTaskData(data: String): String {
        return try {
            if (data.isEmpty()) return data

            val cipher = Cipher.getInstance(TRANSFORMATION)
            val secretKey = generateSecretKey()
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val encryptedBytes = cipher.doFinal(data.toByteArray(StandardCharsets.UTF_8))
            Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        } catch (e: Exception) {
            android.util.Log.e("SecurityManager", "Error encrypting data", e)
            data // En caso de error, devolver datos sin cifrar
        }
    }

    /**
     * Descifra datos sensibles de tareas
     */
    fun decryptTaskData(encryptedData: String): String {
        return try {
            if (encryptedData.isEmpty()) return encryptedData

            val cipher = Cipher.getInstance(TRANSFORMATION)
            val secretKey = generateSecretKey()
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            val decodedBytes = Base64.decode(encryptedData, Base64.DEFAULT)
            val decryptedBytes = cipher.doFinal(decodedBytes)
            String(decryptedBytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            android.util.Log.e("SecurityManager", "Error decrypting data", e)
            encryptedData // En caso de error, devolver datos como están
        }
    }

    /**
     * Genera una clave secreta para cifrado AES
     */
    private fun generateSecretKey(): SecretKeySpec {
        // En producción, esta clave debería ser generada y almacenada de forma segura
        val keyString = "MySecretKey12345" // 16 bytes para AES-128
        return SecretKeySpec(keyString.toByteArray(StandardCharsets.UTF_8), ALGORITHM)
    }

    /**
     * Limpia los datos de sesión
     */
    fun logout() {
        encryptedSharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
    }

    /**
     * Verifica si existen credenciales almacenadas
     */
    fun hasStoredCredentials(): Boolean {
        val username = encryptedSharedPreferences.getString("username", null)
        val password = encryptedSharedPreferences.getString("password", null)
        return !username.isNullOrEmpty() && !password.isNullOrEmpty()
    }

    /**
     * Limpia completamente todos los datos almacenados
     */
    fun clearAllData() {
        encryptedSharedPreferences.edit().clear().apply()
    }
}