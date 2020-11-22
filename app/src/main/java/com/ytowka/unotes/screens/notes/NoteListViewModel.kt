package com.ytowka.unotes.screens.notes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.ytowka.unotes.model.Note

class NoteListViewModel : ViewModel(){
    var user: FirebaseUser? = null
    private set

    private val _notesList = MutableLiveData<List<Note>>()
    val notesList get() = _notesList

    fun loadNotes(user: FirebaseUser){
        this.user = user
    }
    fun openNote(note: Note, online: Boolean){

    }
}