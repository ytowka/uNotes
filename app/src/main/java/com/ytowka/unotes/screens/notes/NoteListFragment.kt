package com.ytowka.unotes.screens.notes

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ytowka.unotes.MainActivity
import com.ytowka.unotes.R
import com.ytowka.unotes.databinding.ActivityMainBinding
import com.ytowka.unotes.databinding.FragmentNoteListBinding
import com.ytowka.unotes.screens.login.LoginViewModel


class NoteListFragment : Fragment() {

    lateinit var binding: FragmentNoteListBinding
    lateinit var viewModel: NoteListViewModel
    lateinit var loginViewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoteListBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(NoteListViewModel::class.java)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cm = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnected == true

        val listAdapter = ListAdapter {
            viewModel.openNote(it, isConnected)
        }

        binding.recyclerView.apply {
            adapter = listAdapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
        activity?.actionBar?.title = getString(R.string.updating)
        loginViewModel.firebaseUserLiveData.observe(viewLifecycleOwner) {
            viewModel.loadNotes(it)
        }
        viewModel.notesList.observe(viewLifecycleOwner) {
            listAdapter.setup(it)
            if (it.isEmpty()) {
                binding.emptyMassage.visibility = View.VISIBLE
            } else {
                binding.emptyMassage.visibility = View.GONE
            }
            activity?.actionBar?.title = getString(R.string.app_name)
        }

    }
}