package com.ytowka.unotes.database.notes.editor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.ytowka.unotes.database.notes.general.DBAccessor
import com.ytowka.unotes.database.notes.general.DBAccessor.Companion.invitationsDB
import com.ytowka.unotes.model.Note
import com.ytowka.unotes.model.invites.Invite
import kotlinx.coroutines.*

class Editor(
    val noteRef: DatabaseReference,
    val delay: Long = 500
) : EditingApi {
    private val updater = NoteUpdater()

    override var getterUpdatesApi: NoteUpdatingCallback = object : NoteUpdatingCallback{
        override fun onTextChanged(text: String) {}
        override fun onNameChanged(name: String) {}
    }

    private val getterJob = CoroutineScope(Dispatchers.Default).launch {
        while (coroutineContext.isActive) {
            delay(delay)
            noteRef.child("name").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.value.toString()
                    if (updater.updates.containsKey("name")) {
                        if (updater.updates["name"] != value) {

                        }
                    } else {

                    }
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
            noteRef.child("text").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.value.toString()
                    if (updater.updates.containsKey("text")) {
                        if (updater.updates["text"] != value) {
                            getterUpdatesApi.onNameChanged(value)
                        }
                    } else {
                        getterUpdatesApi.onNameChanged(value)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }
    override val postUpdatesApi: NoteUpdatingCallback = object : NoteUpdatingCallback {
        override fun onTextChanged(text: String) {
            updater.postUpdate("text", text)
        }

        override fun onNameChanged(name: String) {
            updater.postUpdate("name", name)
        }
    }

    override fun getNote(): LiveData<Note?> {
        val note = MutableLiveData<Note?>()
        noteRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                note.postValue(snapshot.getValue(Note::class.java))
            }

            override fun onCancelled(error: DatabaseError) {
                note.postValue(null)
            }
        })
        return note
    }

    override fun close() {
        updater.job.cancel()
        getterJob.cancel()
        updater.forceUpdate()
    }

    override fun deleteNote() {
        updater.job.cancel()
        getterJob.cancel()
        noteRef.removeValue()
    }

    override fun createInvite(activeTime: Long, maxEnters: Int): String{
        val invite = Invite(noteRef.key.toString(),System.currentTimeMillis(), activeTime,maxEnters)
        val key = invitationsDB.push().toString()
        invitationsDB.child(key).setValue(invite)
        return DBAccessor.baseUrl +key
    }

    private inner class NoteUpdater {
        private val _updates = mutableMapOf<String, String>()
        val updates: Map<String, String> get() = _updates
        private val wasUpdates get() = _updates.isNotEmpty()

        fun forceUpdate() {
            _updates.forEach { (t, u) ->
                noteRef.child(t).setValue(u)
            }
        }

        val job: Job = CoroutineScope(Dispatchers.Default).launch {
            while (coroutineContext.isActive) {
                delay(delay)
                update()
            }
        }

        fun postUpdate(key: String, value: String) {
            _updates[key] = value
        }

        private fun update() {
            if (wasUpdates) {
                _updates.forEach { (t, u) ->
                    noteRef.child(t).setValue(u)
                }
            }
        }
    }

}