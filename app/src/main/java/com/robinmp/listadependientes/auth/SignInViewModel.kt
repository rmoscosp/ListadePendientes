package com.robinmp.listadependientes.auth

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.robinmp.listadependientes.util.await
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignInViewModel(app: Application) : AndroidViewModel(app) {

    private val _state = MutableStateFlow<SignInState>(SignInState())
    val state: StateFlow<SignInState> = _state

    private val auth = FirebaseAuth.getInstance()

    fun onGoogleSignInResult(data: Intent?) {
        viewModelScope.launch {
            try {
                val task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.result
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                val result = auth.signInWithCredential(credential).await()
                _state.value = SignInState(userEmail = result.user?.email)

            } catch (e: Exception) {
                _state.value = SignInState(error = e.localizedMessage)
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _state.value = SignInState()
    }
}

data class SignInState(
    val userEmail: String? = null,
    val error: String? = null
)