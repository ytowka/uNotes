package com.ytowka.unotes.screens.invites

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.ytowka.unotes.databinding.FragmentInvitesBinding


class InvitesFragment : Fragment() {

    lateinit var viewModel: InvitesViewModel
    lateinit var binding: FragmentInvitesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInvitesBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(InvitesViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PagerAdapter(){at, invitation ->
            when(at){
                PagerAdapter.ActionType.ACCEPT -> viewModel.acceptInvite(invitation)
                PagerAdapter.ActionType.REJECT -> viewModel.rejectInvite(invitation)
            }
        }
        binding.invitePages.adapter = adapter
        viewModel.invitationsList.observe(viewLifecycleOwner){
            if(it.isEmpty()){
                binding.emptyListText.visibility = View.VISIBLE
            }else{
                binding.emptyListText.visibility = View.GONE
                adapter.setup(it)
            }
        }


    }
}