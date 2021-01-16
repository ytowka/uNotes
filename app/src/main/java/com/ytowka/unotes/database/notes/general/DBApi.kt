package com.ytowka.unotes.database.notes.general

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ytowka.unotes.database.notes.editor.EditingApi
import com.ytowka.unotes.model.Note
import com.ytowka.unotes.model.invites.InviteResponse

interface DBApi {
    fun createNote(): Note

    fun joinNote(noteId: String)
    fun viewInvite(inviteId: String) : LiveData<InviteResponse>

    val notesLD: MutableLiveData<List<Note>>
    val isLoaded: LiveData<LoadingStatus>

    fun getNoteEditor(id: String): EditingApi
    fun closeEditor()

    var onErrorAction: (error: String)->Unit
}