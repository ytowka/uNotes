package com.ytowka.unotes.model

import android.net.Uri
import com.google.firebase.database.Exclude
import java.text.DateFormat
import java.util.*

data class Note(
    var uid: String = "0",
    var name: String = "name",
    var text: String = "text",
    var hostMember: Member = Member("id error placeholder","name error placeholder",false, ""),
    var editDateText: String = DateFormat.getDateTimeInstance().format(Date())
){
    @set:Exclude @get:Exclude var isHost: Boolean = false
    var editTime = System.currentTimeMillis()
    var members = mapOf(hostMember.userId to hostMember)

    fun updateDate(){
        editDateText = DateFormat.getDateTimeInstance().format(Date())
        editTime = System.currentTimeMillis()
    }
    fun textEquals(other: Any?): Boolean{
        var similarity = true
        if(other is Note) {
            if (this.uid != other.uid) similarity = false
            if (this.name != other.name) similarity = false
            if (this.text != other.text) similarity = false
        }else{
            similarity = false
        }
        return similarity
    }
}