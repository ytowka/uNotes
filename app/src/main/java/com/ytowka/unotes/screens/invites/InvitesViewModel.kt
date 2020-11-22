package com.ytowka.unotes.screens.invites

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ytowka.unotes.model.Invitation

class InvitesViewModel : ViewModel(){

    private val _invitionsList = MutableLiveData<List<Invitation>>()
    val invitationsList
        get() = _invitionsList

    fun acceptInvite(invitation: Invitation){

    }
    fun rejectInvite(invitation: Invitation){

    }
}