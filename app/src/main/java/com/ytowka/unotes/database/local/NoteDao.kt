package com.ytowka.unotes.database.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ytowka.unotes.model.Note

@Dao
interface NoteDao {
    @Query("SELECT * FROM Note_table ORDER BY uid DESC")
    fun getNotes(): LiveData<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun postNotes(list: List<Note>)

    @Query("DELETE FROM Note_table")
    suspend fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNote(note: Note)

    @Delete
    suspend fun delete(note: Note)
}