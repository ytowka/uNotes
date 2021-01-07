package com.ytowka.unotes.screens.edit.editor

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.ytowka.unotes.MainActivity
import com.ytowka.unotes.database.notes.editor.NoteUpdatingCallback
import com.ytowka.unotes.database.notes.editor.EditingApi
import com.ytowka.unotes.databinding.FragmentEditBinding
import com.ytowka.unotes.model.Note
import com.ytowka.unotes.screens.MainViewModel
import com.ytowka.unotes.screens.MainViewModelFactory


class EditFragment : Fragment() {
    lateinit var binding: FragmentEditBinding
    lateinit var mainViewModel: MainViewModel
    private val args: EditFragmentArgs by navArgs()
    private lateinit var noteId: String
    lateinit var editor: EditingApi

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditBinding.inflate(inflater)
        mainViewModel = ViewModelProvider(requireActivity(), MainViewModelFactory(requireActivity().application)).get(MainViewModel::class.java)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        noteId = args.noteId
        setupFields(mainViewModel.database.notes[noteId] ?: Note()) //TODO: handle error


        editor = mainViewModel.database.getNoteEditor(args.noteId,object : NoteUpdatingCallback {
            override fun onTextChanged(text: String) {
                binding.textEdittext.setText(text)
            }
            override fun onNameChanged(name: String) {
                binding.nameEdittext.setText(name)
            }
        })
        editor.getNote().observe(viewLifecycleOwner){
            if(it != null) setupFields(it)
        }
        (requireActivity() as MainActivity).setSupportActionBar(binding.toolbar2)
        setupCallbacks()
    }
    private fun setupFields(note: Note){
        binding.dateText.text = note.editDateText
        binding.nameEdittext.setText(note.name)
        binding.textEdittext.setText(note.text)
    }
    private fun setupCallbacks(){
        binding.textEdittext.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable?) {
                editor.postUpdatesApi.onTextChanged(s.toString())
            }
        })
        binding.nameEdittext.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable?) {
                editor.postUpdatesApi.onNameChanged(s.toString())
            }
        })
    }

    override fun onDetach() {
        super.onDetach()
        mainViewModel.database.closeEditor()
    }
}