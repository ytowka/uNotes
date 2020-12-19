package com.ytowka.unotes.model

import com.google.firebase.database.Exclude
import java.text.DateFormat
import java.util.*

data class Note(
    var uid: String = "0",
    var name: String = "name",
    var text: String = "text",
    var hostId:String = "",
    var editDateText: String = DateFormat.getDateTimeInstance().format(Date())
){
    @set:Exclude @get:Exclude var isHost: Boolean = false
    var editTime = System.currentTimeMillis()
    var memberIds = listOf(hostId)
    fun updateDate(){
        editDateText = DateFormat.getDateTimeInstance().format(Date())
        editTime = System.currentTimeMillis()
    }
}