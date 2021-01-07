package com.ytowka.unotes.database.notes.general

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ytowka.unotes.database.notes.editor.EditingApi
import com.ytowka.unotes.database.notes.editor.Editor
import com.ytowka.unotes.model.invites.Invite
import com.ytowka.unotes.model.Member
import com.ytowka.unotes.model.Note
import com.ytowka.unotes.model.invites.InviteResponse

class DBAccessor(val user: FirebaseUser) : DBApi {
    companion object {
        const val baseUrl = "https://ytowka.unotes.com/"
        const val delay = 5000L

        val invitationsDB = Firebase.database.reference.child("invites")
        val notesDB = Firebase.database.reference.child("notes")
    }
    fun <T> LiveData<T>.observeOnce( observer: Observer<T>) {
        observeForever(object : Observer<T> {
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }
    private val userDB: DatabaseReference =
        Firebase.database.reference.child("usres").child(user.uid)


    private val userNotesDB = mutableMapOf<String, DatabaseReference>()
    private val notesMap = mutableMapOf<String, Note>()
    override val notes: Map<String, Note> get() = notesMap

    private val noteObserversList = mutableMapOf<LifecycleOwner, NotesObserver>()
    override fun observeNotes(owner: LifecycleOwner, observer: NotesObserver) {
        noteObserversList[owner] = observer
    }

    override fun clearNoteObservers() {
        noteObserversList.clear()
    }


    private var observingNoteCallback: (Note) -> Unit = {}
    private var observingNoteId: String = ""
    private fun onObservingNoteEdited(note: Note) {
        observingNoteCallback(note)
    }

    override var onErrorAction: (String) -> Unit = {}

    override val isLoaded = MutableLiveData(false)

    init {
        userDB.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.value
                if (value == null) {
                    userDB.child("name").setValue(user.displayName)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        userDB.child("noteIds").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val key = snapshot.value.toString()
                val dbRef = notesDB.child(key)
                userNotesDB[key] = dbRef
                dbRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!snapshot.exists()) {
                            Log.i("debug", "note not exists")
                        }
                        val note = snapshot.getValue(Note::class.java)
                        if (note != null) {
                            if (note.hostMember.userId == user.uid) {
                                note.isHost = true
                            }
                            if (notesMap.containsKey(note.uid)) {
                                noteObserversList.forEach { (_, observer) ->
                                    observer.onNoteChanged(notesMap[snapshot.key] ?: Note(), note)
                                    if (note.uid == observingNoteId && !notesMap[note.uid]!!.textEquals(
                                            note
                                        )
                                    ) {
                                        onObservingNoteEdited(note)
                                    }
                                }
                            } else {
                                noteObserversList.forEach { (_, observer) ->
                                    observer.onNoteAdded(note)
                                }
                                if (!isLoaded.value!!) {
                                    isLoaded.postValue(true)
                                }
                            }
                            notesMap[snapshot.key!!] = note
                        } else {
                            userDB.child("noteIds").child(key).removeValue()
                            onErrorAction("note is broken")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
                Log.i("debug", "note added ${snapshot.value.toString()}")
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.i("debug", "note changed ${snapshot.value.toString()}")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val key = snapshot.value.toString()
                if (notesMap.containsKey(key)) {
                    val note = notesMap[key]!!

                    noteObserversList.forEach { (_, observer) ->
                        observer.onNoteDeleted(note)
                    }
                    userNotesDB.remove(key)
                    notesMap.remove(key)
                    Log.i("debug", "note deleted ${snapshot.value.toString()}")
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun createNote(): Note {
        val key = notesDB.push().key!!
        userDB.child("noteIds").child(key).setValue(key)

        val newNoteChild = notesDB.child(key)
        val newNote = Note(
            key,
            "note: ${newNoteChild.key}",
            hostMember = Member(
                userDB.key!!,
                user.displayName.toString(),
                true,
                user.photoUrl.toString()
            )
        )
        newNoteChild.setValue(newNote)
        return newNote
    }
    override fun joinNote(noteId: String) {
        userDB.child("noteIds").child(noteId).setValue(noteId)
    }
    override fun viewInvite(inviteId: String): LiveData<InviteResponse> {
        val inviteLiveData = MutableLiveData<InviteResponse>()
        val firstStepInviteLD = loadInvite(inviteId)
        firstStepInviteLD.observeOnce{ inviteResponse ->
            if (inviteResponse != null) {
                if (inviteResponse is InviteResponse.Ok) {
                    val noteLD = loadNote(inviteResponse.invite.noteId)
                    noteLD.observeOnce{ note ->
                        inviteResponse.invite.note = note
                        inviteLiveData.postValue(inviteResponse)
                    }
                }else{
                    inviteLiveData.postValue(inviteResponse)
                }
            }
        }
        return inviteLiveData
    }

    private fun loadNote(noteId: String): LiveData<Note> {
        val liveData = MutableLiveData<Note>()
        notesDB.child(noteId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                liveData.postValue(snapshot.getValue(Note::class.java))
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        return liveData
    }

    private fun loadInvite(inviteId: String): LiveData<InviteResponse> {
        val inviteLiveData = MutableLiveData<InviteResponse>()
        val inviteDB = invitationsDB.child(inviteId)
        inviteDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val invite = snapshot.getValue(Invite::class.java)!!
                    when {
                        invite.maxEnterTime() < System.currentTimeMillis() -> {
                            inviteDB.removeValue()
                            inviteLiveData.postValue(InviteResponse.Expired)
                        }
                        notesMap.containsKey(invite.note.uid) -> {
                            inviteLiveData.postValue(InviteResponse.AlreadyJoined)
                        }
                        else -> {
                            val maxUsers = invite.maxUsers - 1
                            if (maxUsers <= 0) {
                                inviteDB.removeValue()
                            } else {
                                inviteDB.child("maxUsers").setValue(maxUsers)
                            }
                            inviteLiveData.postValue(InviteResponse.Ok(invite))
                        }
                    }
                } else {
                    inviteLiveData.postValue(InviteResponse.NotExists)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        return inviteLiveData
    }

    private var editorPair = Pair<String, Editor?>("", null)

    override fun getNoteEditor(id: String): EditingApi {
        val editor = editorPair.second
        return if (id == editorPair.first) {
            editor!!
        } else {
            editor?.close()
            editorPair = Pair(id, Editor(notesDB.child(id), delay))
            editorPair.second!!
        }
    }

    override fun closeEditor() {
        editorPair.second?.close()
        editorPair = Pair<String, Editor?>("", null)
    }
}