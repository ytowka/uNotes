package com.ytowka.unotes.database.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ytowka.unotes.model.Note

@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class LocalDatabase: RoomDatabase() {

    abstract fun getNoteDao(): NoteDao

    companion object{
        @Volatile
        private var _instance: LocalDatabase? = null

        fun getInstance(context: Context): LocalDatabase{
            val instance = _instance
            if(instance != null){
                return instance
            }
            synchronized(this){
                _instance = Room.databaseBuilder(context,LocalDatabase::class.java,"note_database").build()
                return _instance!!
            }
        }
    }
}