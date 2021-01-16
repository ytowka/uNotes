package com.ytowka.unotes.screens.edit.overview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.ytowka.unotes.R
import com.ytowka.unotes.databinding.FragmentOverviewBinding
import com.ytowka.unotes.model.Note
import com.ytowka.unotes.screens.MainViewModel
import com.ytowka.unotes.screens.MainViewModelFactory


class OverviewFragment : Fragment() {
    private val args: OverviewFragmentArgs by navArgs()
    lateinit var binding: FragmentOverviewBinding
    lateinit var mainViewModel: MainViewModel
    lateinit var note: Note;


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOverviewBinding.inflate(inflater)
        mainViewModel = ViewModelProvider(requireActivity(),
            MainViewModelFactory(requireActivity().application)
        ).get(MainViewModel::class.java)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        note = mainViewModel.database.notesLD.value?.find {
            it.uid == args.noteId
        } ?: Note()
        binding.dateText.text = note.editDateText
        binding.nameText.text = note.name
        binding.textText.text = note.text
    }
}