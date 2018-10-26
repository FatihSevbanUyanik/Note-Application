package com.example.fatihpc.notitceapplication.MainActivity

import android.app.IntentService
import android.content.Intent
import android.database.Cursor
import android.widget.Toast
import com.example.fatihpc.notitceapplication.DataEvent
import com.example.fatihpc.notitceapplication.NoteActivity.Note
import com.example.fatihpc.notitceapplication.SQLDatabase.DatabaseContract
import org.greenrobot.eventbus.EventBus

class MyNoteIntentService: IntentService("MyNoteIntentService") {

    /**
     * gets data from database and posts it to MainActivity.
     * @param intent is the passed intent containing constraints
     */
    override fun onHandleIntent(intent: Intent?) {

        // putting the required constraints to query.
        val sortOrder = "${DatabaseContract.NotesEntry.COLUMN_NOTE_CREATION_DATE} DESC"
        val categoryName = intent?.getStringExtra("categoryName")

        var selection: String? = null
        var selectionArgs: Array<String>? = null

        if (categoryName != null) {
            selection = "${DatabaseContract.CategoryEntry.COLUMN_CATEGORY_CONTENT} = ?"
            selectionArgs = arrayOf(categoryName)
        }

        // getting the required data from database.
        val cursor = contentResolver.query(DatabaseContract.NotesEntry.CONTENT_URI,
                null, selection, selectionArgs, sortOrder) as Cursor

        val notes = getNotes(cursor)

        // sending the data to MainActivity.
        cursor.close()
        EventBus.getDefault().post(DataEvent.DataNotes(notes))
    }

    /**
     * gets the required data through the created cursor.
     * @param cursor is the created cursor object.
     * @return the collection of the desired notes.
     */
    fun getNotes(cursor: Cursor): ArrayList<Note> {

        val notes = ArrayList<Note>()

        while (cursor.moveToNext()) {

            // getting corresponding indexes.
            val noteContentIndex = cursor.getColumnIndex(DatabaseContract.NotesEntry.COLUMN_NOTE_CONTENT)
            val creationDateIndex = cursor.getColumnIndex(DatabaseContract.NotesEntry.COLUMN_NOTE_CREATION_DATE)
            val isExecutedIndex = cursor.getColumnIndex(DatabaseContract.NotesEntry.COLUMN_NOTE_IS_EXECUTED)
            val categoryIdIndex = cursor.getColumnIndex(DatabaseContract.NotesEntry.COLUMN_NOTE_CATEGORY_ID)
            val categoryNameIndex = cursor.getColumnIndex(DatabaseContract.CategoryEntry.COLUMN_CATEGORY_CONTENT)

            // getting each item data.
            val noteId = cursor.getString(0)
            val noteContent = cursor.getString(noteContentIndex)
            val creationDate = cursor.getLong(creationDateIndex)
            val isExecuted = cursor.getString(isExecutedIndex)
            val categoryId = cursor.getString(categoryIdIndex)
            val categoryName = cursor.getString(categoryNameIndex)

            notes.add(Note(noteId, noteContent, creationDate, isExecuted, categoryId, categoryName))
        }

        return notes
    }


}