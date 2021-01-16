package com.ytowka.unotes.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import java.text.DateFormat
import java.util.*

@Entity(tableName = "Note_table")
data class Note(
    @PrimaryKey(autoGenerate = false) var uid: String = "0",
    var name: String = "",
    var text: String = "",
    @Ignore var hostMember: Member = Member("id error placeholder","name error placeholder",false, ""),
){
    var editTime = System.currentTimeMillis()

    @get:Exclude @get:Ignore val editDateText: String get() = DateFormat.getDateTimeInstance().format(Date(editTime))
    @set:Exclude @get:Exclude @Ignore var isHost: Boolean = false
    @Ignore var members = mapOf(hostMember.userId to hostMember)

    fun updateDate(){
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