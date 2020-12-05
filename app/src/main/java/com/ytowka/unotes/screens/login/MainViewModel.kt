package com.ytowka.unotes.screens.login

import android.app.Application
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.ytowka.unotes.R

class MainViewModel(val app: Application) : ViewModel() {

    private var auth: FirebaseAuth
    var mGoogleSignInClient: GoogleSignInClient

    private val _userData = MutableLiveData<FirebaseUser?>()
    val firebaseUserLiveData
        get() = _userData

    //var lockDrawer: (Boolean) -> Unit = {}

    fun logout() {
        _userData.value = null
        FirebaseAuth.getInstance().signOut()
        mGoogleSignInClient.signOut()
        //lockDrawer(true)
    }

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
            _userData.value = firebaseAuthUser
        } else if (account != null) {
            firebaseAuthWithGoogle(account.idToken!!)
        }else{
            _userData.value = null
        }
    }

    fun postResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        handleSignInResult(task)
    }

    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account?.idToken!!)
        } catch (e: ApiException) {
            if (e.statusCode == 12501) {
                Toast.makeText(app, R.string.login_canceled, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(app, "failed to login: ${e.statusCode}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(app.mainExecutor) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    _userData.value = user

                } else {
                    Toast.makeText(app, "failed to login", Toast.LENGTH_LONG).show()
                }
            }
    }

}