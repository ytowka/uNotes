package com.ytowka.unotes.model.invites

import com.google.firebase.database.Exclude
import com.google.gson.annotations.Expose
import com.ytowka.unotes.model.Note

data class Invite(
    val noteId: String = "",
    val creationTime: Long = 0,
    val expiresIn: Long = 0,
    val maxUsers: Int = 0
) {
    @get:Exclude var note: Note = Note()
    fun maxEnterTime() = creationTime+expiresIn
}