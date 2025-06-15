package com.robinmp.listadependientes.auth

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

fun getGoogleSignInClient(context: Context): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("700968602589-e8uqnbp3qjdausm32n16ldsiie9f0ata.apps.googleusercontent.com")
        .requestEmail()
        .build()
    return GoogleSignIn.getClient(context, gso)
}