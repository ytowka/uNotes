package com.ytowka.unotes.screens.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ytowka.unotes.R
import com.ytowka.unotes.databinding.ListItemNoteBinding
import com.ytowka.unotes.model.Note

class ListAdapter(val onOpen: (Note) -> Unit) : RecyclerView.Adapter<ListAdapter.NoteViewHolder>(){
    private var list = emptyList<Note>()

    fun setup(list: List<Note>){
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return NoteViewHolder(inflater.inflate(R.layout.list_item_note,parent))
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
    inner class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val binding = ListItemNoteBinding.bind(view)
        private var note = Note()

        init {
            binding.root.setOnClickListener {
                onOpen(note)
            }
        }
        fun bind(note: Note){
            this.note = note
            binding.noteText
        }
    }
}