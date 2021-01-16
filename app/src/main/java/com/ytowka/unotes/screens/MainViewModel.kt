package com.ytowka.unotes.screens

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ytowka.unotes.database.auth.AuthApi
import com.ytowka.unotes.database.auth.Authentication
import com.ytowka.unotes.database.local.LocalDatabase
import com.ytowka.unotes.database.notes.general.DBAccessor
import com.ytowka.unotes.database.notes.general.DBApi

class MainViewModel(val app: Application) : ViewModel() {
    var authentication: AuthApi = Authentication(app)
    lateinit var database: DBApi
    val internetConnection = MutableLiveData(false)

    init {
        authentication.firebaseUserLiveData.observeForever { user ->
            if(user != null){
                database = DBAccessor(user, LocalDatabase.getInstance(app))
            }
        }
    }
}