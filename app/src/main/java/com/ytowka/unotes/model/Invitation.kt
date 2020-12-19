package com.ytowka.unotes.model

import com.google.firebase.database.Exclude

data class Invitation(
    @get:Exclude @set:Exclude var head: String,
    @get:Exclude @set:Exclude var membersIds: List<Person>,
    val noteId: String
)