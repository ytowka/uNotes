package com.ytowka.unotes.model

import android.net.Uri
import com.google.firebase.auth.FirebaseUser

data class Member(
    val name: String,
    val avatarURI: Uri,
    var isHost: Boolean
) {
    companion object{
        fun fromFirebaseUser(firebaseUser: FirebaseUser) : Member{
            return Member(firebaseUser.displayName ?: "null",firebaseUser.photoUrl ?: Uri.EMPTY,false)
        }
    }
}