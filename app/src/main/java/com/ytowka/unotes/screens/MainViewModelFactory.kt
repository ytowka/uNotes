package com.ytowka.unotes.screens

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModelFactory(val app: Application) : ViewModelProvider.AndroidViewModelFactory(app){
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(app) as T
    }
}