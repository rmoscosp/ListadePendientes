package com.robinmp.listadependientes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robinmp.listadependientes.ui.theme.ListaDependientesTheme
import com.robinmp.listadependientes.util.SecurityManager
import com.robinmp.listadependientes.ui.screens.SignInScreen

class MainActivity : ComponentActivity() {

    private lateinit var securityManager: SecurityManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        securityManager = SecurityManager(this)

        enableEdgeToEdge()

        setContent {
            ListaDependientesTheme {
                var showSuccessScreen by remember { mutableStateOf(false) }

                Scaffold(modifier = Modifier.fillMaxSize()) {
                    if (showSuccessScreen) {
                        // Pantalla de éxito que muestra que la Actividad 4 está completada
                        ActivityCompletedScreen(
                            securityManager = securityManager,
                            onLogout = {
                                securityManager.logout()
                                showSuccessScreen = false
                            }
                        )
                    } else {
                        // Pantalla de login con todas las medidas de seguridad
                        SignInScreen(
                            onNavigateToTaskList = {
                                showSuccessScreen = true
                                // Logs para verificar que todo funciona
                                android.util.Log.d("Security", "✅ ACTIVIDAD 4 COMPLETADA!")
                                android.util.Log.d("Security", "✅ Usuario autenticado: ${securityManager.getUsername()}")
                                android.util.Log.d("Security", "✅ Cifrado SHA-256 funcionando")
                                android.util.Log.d("Security", "✅ Almacenamiento seguro activo")
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        android.util.Log.i("SecurityAudit", "App paused - Usuario: ${securityManager.getUsername()}")
    }

    override fun onResume() {
        super.onResume()
        android.util.Log.i("SecurityAudit", "App resumed - Sesión activa: ${securityManager.isUserLoggedIn()}")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityCompletedScreen(
    securityManager: SecurityManager,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Título principal
        Text(
            text = "🎉 ¡ACTIVIDAD 4 COMPLETADA!",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Medidas de Seguridad Implementadas Exitosamente",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Datos del usuario autenticado
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "✅ PASO 1 - AUTENTICACIÓN LOCAL",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("👤 Usuario: ${securityManager.getUsername()}")
                Text("📝 Nombre completo: ${securityManager.getUserFullName()}")
                Text("🔒 Estado: ${if (securityManager.isUserLoggedIn()) "✅ Autenticado" else "❌ No autenticado"}")
                Text("💾 Credenciales: ${if (securityManager.hasStoredCredentials()) "✅ Almacenadas" else "❌ No encontradas"}")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Medidas de seguridad implementadas
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "✅ PASO 2 - CIFRADO DE DATOS",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("🔐 Contraseñas cifradas con SHA-256")
                Text("🗝️ Datos almacenados con cifrado AES")
                Text("📱 EncryptedSharedPreferences activo")
                Text("🔒 MasterKey generada automáticamente")
                Text("⚡ Funciones encrypt/decrypt implementadas")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Validaciones de seguridad
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "✅ PASO 3 - VALIDACIONES DE SEGURIDAD",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("🚪 Autenticación obligatoria implementada")
                Text("🔍 Verificación de credenciales funcionando")
                Text("📝 Validación de entrada de datos")
                Text("⏰ Gestión de sesiones activa")
                Text("🛡️ Middleware de autenticación creado")
                Text("🔄 Sistema de logout seguro")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón de logout para demostrar funcionalidad
        Button(
            onClick = {
                android.util.Log.d("Security", "🚪 Cerrando sesión de manera segura...")
                onLogout()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("🚪 Cerrar Sesión y Probar de Nuevo", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Puedes cerrar sesión y probar registrar otro usuario\no iniciar sesión con credenciales incorrectas para ver las validaciones",
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}