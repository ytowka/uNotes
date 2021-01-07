package com.ytowka.unotes.model

import android.net.Uri
import com.google.firebase.auth.FirebaseUser

data class Member(
    val userId: String = "",
    val name: String = "",
    val isHost: Boolean = false,
    val avatarURI: String = "",
){
    companion object{
        fun fromFirebaseUser(firebaseUser: FirebaseUser): Member{
            return Member(firebaseUser.uid,firebaseUser.displayName.toString(),false,firebaseUser.photoUrl.toString())
        }
    }
}