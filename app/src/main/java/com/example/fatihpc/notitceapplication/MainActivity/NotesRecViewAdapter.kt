package com.example.fatihpc.notitceapplication.MainActivity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fatihpc.notitceapplication.NoteActivity.Note
import com.example.fatihpc.notitceapplication.NoteActivity.NoteActivity
import com.example.fatihpc.notitceapplication.R
import com.example.fatihpc.notitceapplication.SQLDatabase.DatabaseContract
import com.example.fatihpc.notitceapplication.SQLDatabase.MyAsyncQueryHandler
import kotlinx.android.synthetic.main.notes_list_item.view.*

class NotesRecViewAdapter(val context: Context, var notes: ArrayList<Note>): RecyclerView.Adapter<NotesRecViewAdapter.MyViewHolder>() {

    val myAsyncQueryHandler = MyAsyncQueryHandler(context.contentResolver)

    /**
     * determines which layout will be inflated.
     * @param parent is the parent of the each item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesRecViewAdapter.MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.notes_list_item, parent,false)
        return MyViewHolder(view)
    }

    /**
     * gets the item count.
     */
    override fun getItemCount(): Int { return notes.size }

    /**
     * adds functionality and sets each list item.
     * @param holder holds the all required views.
     * @param position is the position of the list item.
     */
    override fun onBindViewHolder(holder: NotesRecViewAdapter.MyViewHolder, position: Int) {

        holder.tvNoteContent.text = notes[position].COLUMN_NOTE_CONTENT
        val noteDate = notes[position].COLUMN_NOTE_CREATION_DATE

        val convertedDate = DateUtils.getRelativeDateTimeString( context, noteDate,
                System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS, 0)

        holder.tvNoteCreationDate.text = convertedDate
        holder.imgDeleteNote.setOnClickListener {deleteNoteItem(position)}
        holder.listItem.setOnClickListener{goToNoteActivity(position)}
    }

    /**
     * gets each items id in order to set animations.
     */
    override fun getItemId(position: Int): Long {
        return notes[position].COLUMN_NOTE_ID.toLong()
    }

    /**
     * enables to go to NoteActivity by passing the list items attributes.
     * @param position is the position of the list item.
     */
    private fun goToNoteActivity(position: Int) {

        // preparing bundle
        val bundle = Bundle()
        bundle.putString(DatabaseContract.NotesEntry.COLUMN_NOTE_ID, notes[position].COLUMN_NOTE_ID)
        bundle.putString(DatabaseContract.NotesEntry.COLUMN_NOTE_CONTENT, notes[position].COLUMN_NOTE_CONTENT)
        bundle.putLong(DatabaseContract.NotesEntry.COLUMN_NOTE_CREATION_DATE, notes[position].COLUMN_NOTE_CREATION_DATE)
        bundle.putString(DatabaseContract.NotesEntry.COLUMN_NOTE_IS_EXECUTED, notes[position].COLUMN_NOTE_IS_EXECUTED)
        bundle.putString(DatabaseContract.NotesEntry.COLUMN_NOTE_CATEGORY_ID, notes[position].COLUMN_NOTE_CATEGORY_ID)
        bundle.putString(DatabaseContract.CategoryEntry.COLUMN_CATEGORY_CONTENT, notes[position].COLUMN_CATEGORY_CONTENT)

        // preparing intent.
        val intent = Intent(context, NoteActivity::class.java)
        intent.putExtra("noteInfo", bundle)
        context.startActivity(intent)
    }

    /**
     * directs to deleteNoteItem() method by
     * passing the corresponding position value.
     */
    fun onSwipe(position: Int?) {
        deleteNoteItem(position as Int)
    }

    /**
     * deletes the note item in the specified
     * position from the database and the list.
     * @param position is the specified position.
     */
    fun deleteNoteItem(position: Int) {

        // removing from database.
        val COLUMN_NOTE_ID = notes[position].COLUMN_NOTE_ID

        myAsyncQueryHandler.startDelete(1, null, DatabaseContract.NotesEntry.CONTENT_URI,
                "${DatabaseContract.NotesEntry.TABLE_NAME}.${DatabaseContract.NotesEntry.COLUMN_NOTE_ID} = $COLUMN_NOTE_ID", null)

        // removing from the list.
        notes.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, notes.size)
    }

    /**
     * holds all the required views of each list item.
     */
    inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val listItem = itemView as CardView
        val tvNoteContent = listItem.tvNoteContent
        val imgDeleteNote = listItem.imgDeleteNote
        val tvNoteCreationDate = listItem.tvNoteCreationDate
    }

}