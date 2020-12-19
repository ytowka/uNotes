package com.ytowka.unotes.model.network

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.ytowka.unotes.model.Invitation
import com.ytowka.unotes.model.Note
import com.ytowka.unotes.model.Person

interface DBApi {

    fun deleteNote(note: Note): Boolean
    fun invitePerson(id: String, note: Note)
    fun reactInvitation(invitation: Invitation, join: Boolean)
    fun addNote()

    fun observeNotes(owner: LifecycleOwner, observer: NotesObserver)
    fun clearNoteObservers()

    val isLoaded: LiveData<Boolean>

    var onErrorAction: (error: String)->Unit

    val notes: Map<String, Note>
    val persons: List<Person>
    val invites:List<Invitation>

}