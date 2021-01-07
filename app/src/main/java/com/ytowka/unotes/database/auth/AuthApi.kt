package com.ytowka.unotes.database.auth

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser

interface AuthApi {
    val signInIntent: Intent
    val firebaseUserLiveData: MutableLiveData<FirebaseUser?>
    var isLogged: Boolean

    fun postIntentResult(data: Intent?)
    fun logout()

}