package com.ytowka.unotes.screens.invites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ytowka.unotes.R
import com.ytowka.unotes.databinding.ItemPagerInviteBinding
import com.ytowka.unotes.model.Invitation

class PagerAdapter(val onInteract: (ActionType,Invitation) -> Unit) : RecyclerView.Adapter<PagerAdapter.PageViewHolder>(){

    private var list = emptyList<Invitation>()

    fun setup(list: List<Invitation>){
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PageViewHolder(inflater.inflate(R.layout.item_pager_invite,parent,false))
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class PageViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val binding = ItemPagerInviteBinding.bind(view)
        private lateinit var currentInvitation: Invitation

        init {
            binding.buttonAccept.setOnClickListener {
                onInteract(ActionType.ACCEPT, currentInvitation)
            }
            binding.buttonReject.setOnClickListener {
                onInteract(ActionType.REJECT, currentInvitation)
            }
        }

        fun bind(invitation: Invitation){
            currentInvitation = invitation

            binding.inviteHead.text = invitation.head

            val adapter = MembersAdapter()
            binding.listMembers.adapter = adapter
            binding.listMembers.layoutManager = LinearLayoutManager(binding.root.context)
            adapter.setup(invitation.members)
        }
    }

    enum class ActionType{
        ACCEPT,
        REJECT
    }
}