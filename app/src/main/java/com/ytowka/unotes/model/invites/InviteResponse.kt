package com.ytowka.unotes.model.invites

sealed class InviteResponse{
    class Ok(val invite: Invite) : InviteResponse()
    object Expired : InviteResponse()
    object NotExists: InviteResponse()
    object AlreadyJoined: InviteResponse()
}
