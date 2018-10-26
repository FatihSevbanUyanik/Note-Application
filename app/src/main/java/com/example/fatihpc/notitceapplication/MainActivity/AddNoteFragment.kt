package com.example.fatihpc.notitceapplication.MainActivity

import android.content.ContentResolver
import android.content.ContentValues
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleCursorAdapter
import android.widget.Spinner
import android.widget.Toast
import com.example.fatihpc.notitceapplication.NoteActivity.Note
import com.example.fatihpc.notitceapplication.R
import com.example.fatihpc.notitceapplication.SQLDatabase.DatabaseContract
import com.example.fatihpc.notitceapplication.SQLDatabase.MyAsyncQueryHandler
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_add_note.*
import kotlinx.android.synthetic.main.fragment_add_note.view.*
import java.text.SimpleDateFormat
import java.util.*

class AddNoteFragment : DialogFragment() {

    /**
     * creates the required fragment.
     * @param inflater is the layout inflater that inflates the corresponding layout.
     * @param container is the parent layout of the fragment that will be added.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // inflating the required layout
        val view = inflater.inflate(R.layout.fragment_add_note, container, false)

        // initialising properties and setting listeners
        setCategoriesSpinner(view.spnCategories)
        view.btnAddNote.setOnClickListener{addNote(view.etNote.text.toString())}

        return view
    }

    /**
     * sets spinner functionality and properties.
     * @param spnCategories is the spinner on the fragment layout.
     */
    private fun setCategoriesSpinner(spnCategories: Spinner?) {

        val projection = arrayOf(
                DatabaseContract.CategoryEntry.COLUMN_CATEGORY_ID,
                DatabaseContract.CategoryEntry.COLUMN_CATEGORY_CONTENT)

        val categoryCursor = context?.contentResolver?.query(DatabaseContract.CategoryEntry.CONTENT_URI,
                projection, null, null, null)

        val categoryAdapter = SimpleCursorAdapter(context, android.R.layout.simple_spinner_item, categoryCursor,
                arrayOf(DatabaseContract.CategoryEntry.COLUMN_CATEGORY_CONTENT), intArrayOf(android.R.id.text1), 0)

        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spnCategories?.adapter = categoryAdapter
    }


    fun addNote(note: String) {

        if (note.isNotEmpty()) {

            // inserting note item to database.
            val contentValues = ContentValues()
            contentValues.put(DatabaseContract.NotesEntry.COLUMN_NOTE_CONTENT, note)
            contentValues.put(DatabaseContract.NotesEntry.COLUMN_NOTE_CREATION_DATE, System.currentTimeMillis())
            contentValues.put(DatabaseContract.NotesEntry.COLUMN_NOTE_IS_EXECUTED, "0")
            contentValues.put(DatabaseContract.NotesEntry.COLUMN_NOTE_CATEGORY_ID, spnCategories.selectedItemId)

            val myAsyncQueryHandler = MyAsyncQueryHandler(context?.contentResolver as ContentResolver)
            myAsyncQueryHandler.startInsert(1, null, DatabaseContract.NotesEntry.CONTENT_URI, contentValues)

            // updating notes.
            val selectedItem = (context as MainActivity).spinner.selectedItem.toString()
            dismiss()

            // updating main activity layout.
            if(selectedItem.equals("All Categories")) {
                (context as MainActivity).sendNoteUpdateRequest(null)
            } else {
                (context as MainActivity).sendNoteUpdateRequest(selectedItem)
            }

        } else {
            Toast.makeText(context, "Please Type New Note.", Toast.LENGTH_SHORT).show()
        }


    }

}