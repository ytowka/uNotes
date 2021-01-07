package com.ytowka.unotes.screens

import android.app.Application
import androidx.lifecycle.ViewModel
import com.ytowka.unotes.database.auth.AuthApi
import com.ytowka.unotes.database.auth.Authentication
import com.ytowka.unotes.database.notes.general.DBAccessor
import com.ytowka.unotes.database.notes.general.DBApi

class MainViewModel(val app: Application) : ViewModel() {
    lateinit var database: DBApi
    var authentication: AuthApi = Authentication(app)

    init {
        authentication.firebaseUserLiveData.observeForever { user ->
            if(user != null){
                database = DBAccessor(user)
            }
        }
    }
}