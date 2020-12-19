package com.ytowka.unotes.screens.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ytowka.unotes.R
import com.ytowka.unotes.databinding.ListItemNoteBinding
import com.ytowka.unotes.model.Note
import com.ytowka.unotes.model.network.NotesObserver

class ListAdapter(val onOpen: (Note) -> Unit) : RecyclerView.Adapter<ListAdapter.NoteViewHolder>(), NotesObserver{
    private var list = mutableListOf<Note>()

    fun setup(list: List<Note>){
        this.list = list.toMutableList()
        notifyDataSetChanged()
    }
    override fun onNoteAdded(note: Note) {
        list.add(note)
        notifyItemInserted(list.indexOf(note))
    }
    override fun onNoteDeleted(note: Note) {
        val index = list.indexOf(note)
        list.remove(note)
        notifyItemRemoved(index)
    }
    override fun onNotesUpdated(list: List<Note>) {
        this.list = list.toMutableList()
        notifyDataSetChanged()
    }
    override fun onNoteChanged(old: Note, new: Note) {
        val index = list.indexOf(old)
        list[index] = new
        notifyItemChanged(index)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding =  ListItemNoteBinding.inflate(LayoutInflater.from(parent.context))
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class NoteViewHolder(val binding: ListItemNoteBinding) : RecyclerView.ViewHolder(binding.root){
        private var note = Note()

        init {
            binding.root.setOnClickListener {
                onOpen(note)
            }
        }
        fun bind(note: Note){
            this.note = note
            binding.noteText.text = note.name
            binding.editedTimeText.text = note.editDateText
        }

    }
}