package com.ytowka.unotes.screens

import android.app.Application
import androidx.lifecycle.ViewModel
import com.ytowka.unotes.model.network.Authentication
import com.ytowka.unotes.model.network.DBAccessor
import com.ytowka.unotes.model.network.DBApi

class MainViewModel(val app: Application) : ViewModel() {
    lateinit var database: DBApi
    var authentication: Authentication = Authentication(app)

    init {
        authentication.firebaseUserLiveData.observeForever { user ->
            database = DBAccessor(user)
        }

    }
}