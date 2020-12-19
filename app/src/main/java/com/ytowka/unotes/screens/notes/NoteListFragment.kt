package com.ytowka.unotes.screens.notes

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ytowka.unotes.MainActivity
import com.ytowka.unotes.R
import com.ytowka.unotes.databinding.FragmentNoteListBinding
import com.ytowka.unotes.model.Note
import com.ytowka.unotes.model.network.NotesObserver
import com.ytowka.unotes.screens.MainViewModel
import com.ytowka.unotes.screens.MainViewModelFactory


class NoteListFragment : Fragment() {

    lateinit var binding: FragmentNoteListBinding
    lateinit var viewModel: NoteListViewModel
    lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteListBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(NoteListViewModel::class.java)
        mainViewModel = ViewModelProvider(requireActivity(),
            MainViewModelFactory(requireActivity().application)
        ).get(MainViewModel::class.java)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i("debug","current destination: uNotes")
        (requireActivity() as MainActivity).toolbar.title = getString(R.string.updating)

        val cm = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnected == true

        val listAdapter = ListAdapter {
            viewModel.openNote(it, isConnected)
        }
        val noteList = mutableListOf<Note>()
        mainViewModel.database.notes.forEach{ (_, note) ->
            noteList.add(note)
        }
        listAdapter.setup(noteList)
        binding.recyclerView.apply {
            adapter = listAdapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
        activity?.actionBar?.title = getString(R.string.updating)
        mainViewModel.authentication.firebaseUserLiveData.observe(viewLifecycleOwner) {
            if(it != null){
                viewModel.loadNotes(it)
            }
        }
        mainViewModel.database.observeNotes(viewLifecycleOwner,listAdapter)
        binding.fabAdd.setOnClickListener {
            mainViewModel.database.addNote()
        }
        mainViewModel.database.isLoaded.observe(viewLifecycleOwner,{
            (requireActivity() as MainActivity).toolbar.title = if(it) "${R.string.app_name}" else getString(R.string.updating)
        })
    }
}