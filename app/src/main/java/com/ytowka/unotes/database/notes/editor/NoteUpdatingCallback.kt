package com.ytowka.unotes.database.notes.editor

interface NoteUpdatingCallback {
    fun onTextChanged(text: String)
    fun onNameChanged(name: String)
}