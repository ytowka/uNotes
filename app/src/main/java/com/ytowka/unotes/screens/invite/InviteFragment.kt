package com.ytowka.unotes.screens.invite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ytowka.unotes.NavGraphDirections
import com.ytowka.unotes.R
import com.ytowka.unotes.databinding.FragmentInviteBinding
import com.ytowka.unotes.model.invites.Invite
import com.ytowka.unotes.model.invites.InviteResponse
import com.ytowka.unotes.screens.MainViewModel
import com.ytowka.unotes.screens.MainViewModelFactory

class InviteFragment : Fragment() {
    lateinit var mainViewModel: MainViewModel
    lateinit var binding: FragmentInviteBinding

    val args: InviteFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentInviteBinding.inflate(inflater)
        mainViewModel = ViewModelProvider(requireActivity(),
            MainViewModelFactory(requireActivity().application)
        ).get(MainViewModel::class.java)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        mainViewModel.database.viewInvite(args.inviteId).observe(viewLifecycleOwner){
            when(it){
                is InviteResponse.Ok -> onOk(it.invite)
                is InviteResponse.AlreadyJoined -> onAlreadyJoined()
                is InviteResponse.Expired -> onExpired()
                is InviteResponse.NotExists -> onDoesntExists()
            }
            binding.iconLoading.visibility = View.GONE
        }
    }

    private fun onOk(invite: Invite){
        binding.errorLayout.visibility = View.GONE
        binding.inviteLayout.visibility = View.VISIBLE

        binding.buttonAccept.setOnClickListener {
            mainViewModel.database.joinNote(invite.note.uid)
        }
        binding.buttonReject.setOnClickListener {
            val action = NavGraphDirections.actionToList()
            findNavController().navigate(action)
        }
        val adapter = MembersListAdapter(invite.note.members.values.toList())
        binding.listMembers.layoutManager = LinearLayoutManager(context)
        binding.listMembers.adapter = adapter
    }
    private fun onExpired(){
        binding.errorLayout.visibility = View.VISIBLE
        binding.inviteLayout.visibility = View.GONE
        binding.textErrorMessage.text = getString(R.string.expired)
    }
    private fun onDoesntExists(){
        binding.errorLayout.visibility = View.VISIBLE
        binding.inviteLayout.visibility = View.GONE
        binding.textErrorMessage.text = getString(R.string.invite_not_exists)
    }
    private fun onAlreadyJoined(){
        binding.errorLayout.visibility = View.VISIBLE
        binding.inviteLayout.visibility = View.GONE
        binding.textErrorMessage.text = getString(R.string.already_joined)
    }
}