package com.robinmp.listadependientes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robinmp.listadependientes.util.SecurityManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    onNavigateToTaskList: () -> Unit
) {
    val context = LocalContext.current
    val securityManager = remember { SecurityManager(context) }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isSignUpMode by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Verificar si ya hay credenciales almacenadas
    LaunchedEffect(Unit) {
        if (securityManager.isUserLoggedIn()) {
            onNavigateToTaskList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isSignUpMode) "Registro de Usuario" else "Iniciar Sesión",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Campo de nombre completo (solo en modo registro)
        if (isSignUpMode) {
            OutlinedTextField(
                value = fullName,
                onValueChange = {
                    fullName = it
                    errorMessage = ""
                },
                label = { Text("Nombre Completo") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true
            )
        }

        // Campo de usuario
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                errorMessage = ""
            },
            label = { Text("Usuario") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

        // Campo de contraseña - SIN ICONOS para evitar errores
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = ""
            },
            label = { Text("Contraseña") },
            visualTransformation = if (isPasswordVisible)
                VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                // Botón simple de texto en lugar de iconos
                TextButton(
                    onClick = { isPasswordVisible = !isPasswordVisible }
                ) {
                    Text(
                        text = if (isPasswordVisible) "👁️" else "🔒",
                        fontSize = 16.sp
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

        // Mensaje de error
        if (errorMessage.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Botón principal
        Button(
            onClick = {
                if (validateInput(username, password, fullName, isSignUpMode)) {
                    isLoading = true

                    if (isSignUpMode) {
                        // Registro de usuario
                        try {
                            securityManager.saveUserCredentials(username, password, fullName)
                            securityManager.setUserLoggedIn(true)
                            onNavigateToTaskList()
                        } catch (e: Exception) {
                            errorMessage = "Error al registrar usuario: ${e.message}"
                        }
                    } else {
                        // Inicio de sesión
                        if (securityManager.hasStoredCredentials()) {
                            if (securityManager.verifyCredentials(username, password)) {
                                securityManager.setUserLoggedIn(true)
                                onNavigateToTaskList()
                            } else {
                                errorMessage = "Credenciales incorrectas"
                            }
                        } else {
                            errorMessage = "No hay usuarios registrados. Por favor, regístrese primero."
                        }
                    }
                    isLoading = false
                } else {
                    errorMessage = when {
                        username.isEmpty() -> "El usuario es requerido"
                        password.isEmpty() -> "La contraseña es requerida"
                        isSignUpMode && fullName.isEmpty() -> "El nombre completo es requerido"
                        password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
                        else -> "Por favor, complete todos los campos"
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(if (isSignUpMode) "Registrarse" else "Iniciar Sesión")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para cambiar entre login y registro
        TextButton(
            onClick = {
                isSignUpMode = !isSignUpMode
                errorMessage = ""
                username = ""
                password = ""
                fullName = ""
            }
        ) {
            Text(
                if (isSignUpMode) "¿Ya tienes cuenta? Iniciar Sesión"
                else "¿No tienes cuenta? Registrarse"
            )
        }

        // Información de seguridad
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "🔒 Seguridad Implementada:",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "• Contraseñas cifradas con SHA-256\n" +
                            "• Datos almacenados con cifrado AES\n" +
                            "• Autenticación local segura",
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

private fun validateInput(
    username: String,
    password: String,
    fullName: String,
    isSignUpMode: Boolean
): Boolean {
    return username.isNotEmpty() &&
            password.isNotEmpty() &&
            password.length >= 6 &&
            (!isSignUpMode || fullName.isNotEmpty())
}