package com.ytowka.unotes.screens.invite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.ytowka.unotes.R
import com.ytowka.unotes.databinding.ItemListMembersOfInviteBinding
import com.ytowka.unotes.utils.CircularTransformation
import com.ytowka.unotes.model.Member


class MembersListAdapter(val list: List<Member>) : RecyclerView.Adapter<MembersListAdapter.MemberViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        return MemberViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list_members_of_invite,parent,false))
    }
    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(list[position])
    }
    override fun getItemCount(): Int {
        return list.size
    }
    class MemberViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private var binding =  ItemListMembersOfInviteBinding.bind(view)

        fun bind(member: Member){
            binding.textMemberName.text = member.name
            binding.textIsHost.visibility = if(member.isHost) View.VISIBLE else View.GONE
            Picasso.get()
                .load(member.avatarURI)
                .transform(CircularTransformation(binding.iconAvatar.maxHeight/2))
                .into(binding.iconAvatar)
        }
    }

}