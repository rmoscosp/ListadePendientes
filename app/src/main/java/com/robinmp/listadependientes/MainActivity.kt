package com.robinmp.listadependientes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.robinmp.listadependientes.auth.SignInViewModel

import com.robinmp.listadependientes.navigation.AppNavigation
import com.robinmp.listadependientes.ui.screens.SignInScreen
import com.robinmp.listadependientes.viewmodels.TaskViewModel


class MainActivity : ComponentActivity() {
    private lateinit var taskStorage: TaskStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //taskStorage = TaskStorage(this)

        setContent {
            ListaDePendientesTheme {
                val taskViewModel: TaskViewModel = viewModel()
                val firebaseUser = FirebaseAuth.getInstance().currentUser

                if (firebaseUser == null) {

                    val signInViewModel: SignInViewModel = viewModel()

                    // No autenticado: mostrar pantalla de login
                    SignInScreen(
                        viewModel = signInViewModel,
                        onSignedIn = {
                            taskViewModel.loadTasks()
                        }
                    )
                } else {
                    // Autenticado: mostrar navegación principal
                    val tasks by taskViewModel.tasks.collectAsState()

                    AppNavigation(
                        tasks = tasks,
                        onTasksChanged = {
                            updatedList ->
                            taskViewModel.saveAll(updatedList)
                        }
                    )
                }
            }
        }
    }
}




