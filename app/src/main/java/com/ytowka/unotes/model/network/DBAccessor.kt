package com.ytowka.unotes.model.network

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ytowka.unotes.model.Invitation
import com.ytowka.unotes.model.Note
import com.ytowka.unotes.model.Person

class DBAccessor(user: FirebaseUser) : DBApi{
    private val userDB: DatabaseReference = Firebase.database.reference.child("usres").child(user.uid)
    private val personListDB: DatabaseReference = Firebase.database.reference.child("persons")
    private val invitesDB = Firebase.database.reference.child("persons").child(user.uid).child("invites")
    private val notesDB get() = Firebase.database.reference.child("notes")

    private val notesMapDB = mutableMapOf<String, DatabaseReference>()
    private val notesMap = mutableMapOf<String, Note>()
    override val notes: Map<String, Note> get() = notesMap

    private val personList = mutableMapOf<String,Person>()
    override val persons get(): List<Person> = personList.values.toList()

    private val invitesList = mutableMapOf<String,Invitation>()
    override val invites:List<Invitation> get()  = invitesList.values.toList()

    private val noteObserversList = mutableMapOf<LifecycleOwner, NotesObserver>()
    override fun observeNotes(owner: LifecycleOwner, observer: NotesObserver) {
        noteObserversList[owner] = observer
    }
    override fun clearNoteObservers() {
        noteObserversList.clear()
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
        invitesDB.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val noteId = snapshot.child("noteId").value.toString()
                val inviteId = snapshot.key.toString()
                val invitation = MutableLiveData<Invitation>()
                if(noteId != "null"){
                    val note = MutableLiveData<Note>()
                    notesDB.child(noteId).addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val value = snapshot.getValue(Note::class.java)
                            if(value != null){
                                note.postValue(value)
                            }else{
                                onErrorAction("failed to load invites")
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
                    note.observeForever{
                        val note = it!!
                        val memberList = mutableListOf<Person>()
                        val invite = Invitation(note.name,memberList,it.uid)
                        invitation.value = invite
                    }
                    invitation.observeForever{
                        invitesList[inviteId] = it
                    }
                }else onErrorAction("failed to load invites")
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
                invitesList.remove(snapshot.key)
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
        personListDB.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val value = snapshot.getValue(Person::class.java)
                if(value != null){
                    personList[snapshot.key ?: ""] = value
                }else onErrorAction("failed to load persons")
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val value = snapshot.getValue(Person::class.java)
                if(value != null){
                    personList[snapshot.key ?: ""] = value
                }else onErrorAction("failed to update persons")
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
                val value = snapshot.getValue(Person::class.java)
                if(value != null){
                    personList.remove(snapshot.key ?: "")
                }else onErrorAction("failed to load persons")
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

        userDB.child("noteIds").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val key = snapshot.value.toString()
                val dbRef = notesDB.child(key)
                notesMapDB[key] = dbRef
                dbRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val note = snapshot.getValue(Note::class.java)
                        if (note != null) {
                            note.isHost = true
                            if (notesMap.containsKey(note.uid)) {
                                noteObserversList.forEach { (_, observer) ->
                                    observer.onNoteChanged(notesMap[snapshot.key] ?: Note(), note)
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
                        }else{
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
                val note = notesMap[key]!!

                noteObserversList.forEach { (_, observer) ->
                    observer.onNoteDeleted(note)
                }
                notesMapDB.remove(key)
                notesMap.remove(key)
                Log.i("debug", "note deleted ${snapshot.value.toString()}")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun addNote() {
        val key = notesDB.push().key!!
        userDB.child("noteIds").child(key).setValue(key)

        val newNoteChild = notesDB.child(key)
        val newNote = Note(key, "note: ${newNoteChild.key}", hostId = userDB.key!!)
        newNoteChild.setValue(newNote)
    }

    override fun reactInvitation(invitation: Invitation, join: Boolean) {
        if(join){
            val id = invitation.noteId
            val dbRef = notesDB.child(id)
            notesMapDB[id] = dbRef
            dbRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val note = snapshot.getValue(Note::class.java)
                    if (note != null) {
                        if (notesMap.containsKey(note.uid)) {
                            noteObserversList.forEach { (_, observer) ->
                                observer.onNoteChanged(notesMap[snapshot.key] ?: Note(), note)
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
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }else{
            val id = invitation.noteId
        }
    }
    override fun deleteNote(note: Note): Boolean{
        if(note.isHost){
            val key = note.uid
            userDB.child("noteIds").child(key).removeValue()
            notesMapDB[key]!!.removeValue()
            return true
        }
        return false
    }
    override fun invitePerson(id: String, note: Note){

    }
    fun testInvitePerson(note: Note){

    }
}