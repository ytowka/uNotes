package com.ytowka.unotes.screens.notes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ytowka.unotes.MainActivity
import com.ytowka.unotes.R
import com.ytowka.unotes.databinding.FragmentNoteListBinding
import com.ytowka.unotes.utils.NetworkUtil
import com.ytowka.unotes.model.Note
import com.ytowka.unotes.screens.MainViewModel
import com.ytowka.unotes.screens.MainViewModelFactory


class NoteListFragment : Fragment() {

    lateinit var binding: FragmentNoteListBinding
    lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteListBinding.inflate(inflater)
        mainViewModel = ViewModelProvider(requireActivity(),
            MainViewModelFactory(requireActivity().application)
        ).get(MainViewModel::class.java)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i("debug","current destination: uNotes")
        (requireActivity() as MainActivity).toolbar.title = getString(R.string.updating)

        val isConnected = NetworkUtil.getConnectivityStatus(requireContext()) != NetworkUtil.TYPE_NOT_CONNECTED
        mainViewModel.internetConnection.value = isConnected

        val listAdapter = ListAdapter {
            val bundle = Bundle()
            bundle.putString("noteId",it.uid)
            if(isConnected){
                val action = NoteListFragmentDirections.editNote(it.uid)
                findNavController().navigate(action)
            }else{
                val action = NoteListFragmentDirections.overviewNote(it.uid)
                findNavController().navigate(action)
            }
        }
        mainViewModel.database.notesLD.observe(viewLifecycleOwner){
            listAdapter.setup(it)
        }
        binding.recyclerView.apply {
            adapter = listAdapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
        activity?.actionBar?.title = getString(R.string.updating)
        binding.fabAdd.visibility = if(isConnected) View.VISIBLE else View.GONE
        binding.fabAdd.setOnClickListener {
            if(isConnected){
                val newNote = mainViewModel.database.createNote()
                val bundle = Bundle()
                bundle.putString("noteId",newNote.uid)
                val action = NoteListFragmentDirections.editNote(newNote.uid)
                findNavController().navigate(action)
            }
        }
        mainViewModel.database.isLoaded.observe(viewLifecycleOwner,{
            (requireActivity() as MainActivity).toolbar.title = if(it.remoteStatus) getString(R.string.app_name) else getString(R.string.updating)
        })
    }
}