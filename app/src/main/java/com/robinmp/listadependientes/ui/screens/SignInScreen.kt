package com.robinmp.listadependientes.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.robinmp.listadependientes.auth.SignInViewModel
import com.robinmp.listadependientes.auth.getGoogleSignInClient


@Composable
fun SignInScreen(
    viewModel: SignInViewModel,
    onSignedIn: () -> Unit
) {
    val state by viewModel.state.collectAsState()


    LaunchedEffect(state.userEmail) {
        if (state.userEmail != null) {
            onSignedIn()
        }
    }

    val context = LocalContext.current
    val activity = context as Activity

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        if (result.resultCode == Activity.RESULT_OK && data != null) {
            viewModel.onGoogleSignInResult(data)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Iniciar sesión con Google")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            val signInIntent = getGoogleSignInClient(context).signInIntent
            launcher.launch(signInIntent)
        }) {
            Text("Iniciar sesión")
        }

        state.error?.let {
            Text("Error: $it", color = Color.Red)
        }
    }
}

