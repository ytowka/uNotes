package com.ytowka.unotes.model

import android.net.Uri
import com.google.firebase.auth.FirebaseUser

data class Person(
    val userId: String,
    val name: String,
    val isHost: Boolean,
    val avatarURI: Uri,
)