package com.ytowka.unotes.model.network

import com.ytowka.unotes.model.Note

interface NotesObserver {
    fun onNoteAdded(note: Note)
    fun onNoteDeleted(note: Note)
    fun onNotesUpdated(list: List<Note>)
    fun onNoteChanged(old: Note, new: Note)
}