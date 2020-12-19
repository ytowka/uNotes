package com.ytowka.unotes.model.network

import android.app.Application
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.ytowka.unotes.R
import com.ytowka.unotes.screens.login.LoginFragment

class Authentication(val app: Application) {

    private var auth: FirebaseAuth
    private var mGoogleSignInClient: GoogleSignInClient
    val signInIntent get() = mGoogleSignInClient.signInIntent
    private var userData = MutableLiveData<FirebaseUser>()
    val firebaseUserLiveData get() = userData

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(app.getString(R.string.default_web_client_id))
            .requestProfile()
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(app, gso);
        val account = GoogleSignIn.getLastSignedInAccount(app)

        auth = FirebaseAuth.getInstance()
        val firebaseAuthUser = auth.currentUser

        if (firebaseAuthUser != null) {
            onUserLogined(firebaseAuthUser)
            userData.value = firebaseAuthUser
        } else if (account != null) {
            firebaseAuthWithGoogle(account.idToken!!)
        } else {
            userData.value = null
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(app.mainExecutor) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    userData.value = user
                    onUserLogined(user!!)
                } else {
                    Toast.makeText(app, "failed to login", Toast.LENGTH_LONG).show()
                }
            }
    }
    fun postResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)

        try {
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account?.idToken!!)
        } catch (e: ApiException) {
            if (e.statusCode == 12501) {
                Toast.makeText(app, R.string.login_canceled, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(app, "failed to login: ${e.statusCode}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun onUserLogined(user: FirebaseUser) {

    }
    fun logout(){
        userData.value = null
        FirebaseAuth.getInstance().signOut()
        mGoogleSignInClient.signOut()
    }
}