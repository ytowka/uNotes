package com.ytowka.unotes.database.notes.editor

import androidx.lifecycle.LiveData
import com.ytowka.unotes.model.Note

interface EditingApi{

    val postUpdatesApi: NoteUpdatingCallback
    var getterUpdatesApi: NoteUpdatingCallback

    fun getNote(): LiveData<Note?>
    fun deleteNote()
    fun createInvite(activeTime: Long,maxEnters: Int): String

    fun close()
}