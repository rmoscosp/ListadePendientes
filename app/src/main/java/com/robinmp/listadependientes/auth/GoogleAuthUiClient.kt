package com.robinmp.listadependientes.auth

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class GoogleAuthUiClient(
    private val oneTapClient: SignInClient,
    private val signInRequest: BeginSignInRequest
) {
    suspend fun signIn(activity: Activity): IntentSenderRequestResult {
        return try {
            val result = oneTapClient.beginSignIn(signInRequest).await()
            IntentSenderRequestResult.Success(result.pendingIntent.intentSender)
        } catch (e: Exception) {
            Log.e("GoogleSignIn", "Sign-in failed", e)
            IntentSenderRequestResult.Error(e.message)
        }
    }

    suspend fun signInWithIntent(activity: Activity, intent: Intent): FirebaseUserResult {
        return try {
            val credential: SignInCredential =
                Identity.getSignInClient(activity).getSignInCredentialFromIntent(intent)

            val googleCredential = GoogleAuthProvider.getCredential(credential.googleIdToken, null)

            val user = FirebaseAuth.getInstance().signInWithCredential(googleCredential).await().user

            FirebaseUserResult.Success(user)
        } catch (e: Exception) {
            FirebaseUserResult.Error(e.message)
        }
    }
}

sealed class IntentSenderRequestResult {
    data class Success(val intentSender: android.content.IntentSender) : IntentSenderRequestResult()
    data class Error(val message: String?) : IntentSenderRequestResult()
}

sealed class FirebaseUserResult {
    data class Success(val user: com.google.firebase.auth.FirebaseUser?) : FirebaseUserResult()
    data class Error(val message: String?) : FirebaseUserResult()
}
