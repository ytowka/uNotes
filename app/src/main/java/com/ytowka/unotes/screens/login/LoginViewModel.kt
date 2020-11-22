package com.ytowka.unotes.screens.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser

class LoginViewModel : ViewModel(){
    private val _userData = MutableLiveData<FirebaseUser>()

    val firebaseUserLiveData
    get() = _userData

    fun login(firebaseUser: FirebaseUser){
        _userData.value = firebaseUser
    }

}