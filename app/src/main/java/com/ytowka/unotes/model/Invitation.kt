package com.ytowka.unotes.model

data class Invitation(
    val head: String,
    val members: List<Member>,
    val roomId: String
)