package com.ytowka.unotes.screens.invites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ytowka.unotes.R
import com.ytowka.unotes.databinding.ItemListMembersOfInviteBinding
import com.ytowka.unotes.model.Member

class MembersAdapter : RecyclerView.Adapter<MembersAdapter.MemberViewHolder>(){
    private var list = emptyList<Member>()

    fun setup(list: List<Member>){
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MemberViewHolder(inflater.inflate(R.layout.item_list_members_of_invite,parent,false))
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MemberViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val binding = ItemListMembersOfInviteBinding.bind(view)

        fun bind(member: Member){
            binding.textIsHost.visibility = if(member.isHost) View.VISIBLE else View.GONE
            binding.textMemberName.text = member.name
        }
    }
}