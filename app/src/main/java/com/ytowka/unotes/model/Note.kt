package com.ytowka.unotes.model

import java.text.DateFormat
import java.util.*

data class Note(
    var uid: Int = 0,
    var name: String = "",
    var text: String = "",
){
    var editDateText: String = DateFormat.getDateTimeInstance().format(Date())
    var editTime = System.currentTimeMillis()

    fun updateDate(){
        editDateText = DateFormat.getDateTimeInstance().format(Date())
        editTime = System.currentTimeMillis()
    }
}