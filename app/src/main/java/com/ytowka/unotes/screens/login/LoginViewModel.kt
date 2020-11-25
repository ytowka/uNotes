package com.ytowka.unotes.screens.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso

class LoginViewModel : ViewModel(){

    private val _userData = MutableLiveData<FirebaseUser>()
    val firebaseUserLiveData
        get() = _userData

    /*private val _avatarIcon = MutableLiveData<>()
    val avatarIconLiveData
        get() = _avatarIcon*/

    fun login(firebaseUser: FirebaseUser){
        _userData.value = firebaseUser
    }
    fun loadAvatarIcon(){
        val firebaseUser = firebaseUserLiveData.value
        if(firebaseUser != null){

        }
    }
}