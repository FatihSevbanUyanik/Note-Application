package com.example.fatihpc.notitceapplication.NoteActivity

import android.content.ContentValues
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.SimpleCursorAdapter
import android.widget.Spinner
import android.widget.Toast
import com.example.fatihpc.notitceapplication.CategoryActivity.Category
import com.example.fatihpc.notitceapplication.CategoryActivity.MyCategoryIntentService
import com.example.fatihpc.notitceapplication.DataEvent
import com.example.fatihpc.notitceapplication.MainActivity.MainActivity
import com.example.fatihpc.notitceapplication.R
import com.example.fatihpc.notitceapplication.SQLDatabase.DatabaseContract
import com.example.fatihpc.notitceapplication.SQLDatabase.MyAsyncQueryHandler
import kotlinx.android.synthetic.main.activity_note.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class NoteActivity : AppCompatActivity() {

    // properties
    lateinit var note: Note
    lateinit var categories: ArrayList<Category>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        val noteBundle = intent.getBundleExtra("noteInfo")
        val COLUMN_NOTE_ID = noteBundle.getString(DatabaseContract.NotesEntry.COLUMN_NOTE_ID)
        val COLUMN_NOTE_CONTENT = noteBundle.getString(DatabaseContract.NotesEntry.COLUMN_NOTE_CONTENT)
        val COLUMN_NOTE_CREATION_DATE = noteBundle.getLong(DatabaseContract.NotesEntry.COLUMN_NOTE_CREATION_DATE)
        val COLUMN_NOTE_IS_EXECUTED = noteBundle.getString(DatabaseContract.NotesEntry.COLUMN_NOTE_IS_EXECUTED)
        val COLUMN_NOTE_CATEGORY_ID = noteBundle.getString(DatabaseContract.NotesEntry.COLUMN_NOTE_CATEGORY_ID)
        val COLUMN_CATEGORY_CONTENT = noteBundle.getString(DatabaseContract.CategoryEntry.COLUMN_CATEGORY_CONTENT)


        note = Note(COLUMN_NOTE_ID, COLUMN_NOTE_CONTENT, COLUMN_NOTE_CREATION_DATE,
                COLUMN_NOTE_IS_EXECUTED, COLUMN_NOTE_CATEGORY_ID, COLUMN_CATEGORY_CONTENT)

        setCheckBox()
        etNote.setText(COLUMN_NOTE_CONTENT)
        sendCategoryUpdateRequest()
    }

    private fun setCheckBox() {

        if (note.COLUMN_NOTE_IS_EXECUTED.equals("0")) {
            checkBoxIsExecuted.text = "note is not executed"
            checkBoxIsExecuted.isChecked = false
        } else {
            checkBoxIsExecuted.text = "note is executed"
            checkBoxIsExecuted.isChecked = true
        }

    }

    fun saveNote(view: View) {

        val noteCategoryId = categories[spnCategories2.selectedItemPosition].COLUMN_CATEGORY_ID
        val newNote = etNote.text.toString()

        if (newNote.isNotEmpty()) {

            val contentValues = ContentValues()
            contentValues.put(DatabaseContract.NotesEntry.COLUMN_NOTE_CONTENT, newNote)
            contentValues.put(DatabaseContract.NotesEntry.COLUMN_NOTE_CATEGORY_ID, noteCategoryId)

            if (checkBoxIsExecuted.isChecked) {
                contentValues.put(DatabaseContract.NotesEntry.COLUMN_NOTE_IS_EXECUTED, "1")
            } else {
                contentValues.put(DatabaseContract.NotesEntry.COLUMN_NOTE_IS_EXECUTED, "0")
            }

            val myAsyncQueryHandler = MyAsyncQueryHandler(contentResolver)
            val selection = "${DatabaseContract.NotesEntry.COLUMN_NOTE_ID} = ${note.COLUMN_NOTE_ID}"

            myAsyncQueryHandler.startUpdate(1, null, DatabaseContract.NotesEntry.CONTENT_URI,
                    contentValues, selection, null)

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)

        } else {
            Toast.makeText(this, "Please Type Note.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * sends update request for the category data in the recycler view.
     */
    private fun sendCategoryUpdateRequest() {
        startService(Intent(this, MyCategoryIntentService::class.java))
    }

    /**
     * updates the coming data.
     */
    @Subscribe(sticky = true , threadMode = ThreadMode.MAIN)
    fun updateCategoryData( event: DataEvent.DataCategories) {
        val categories = event.categories
        this.categories = categories
        setCategoriesSpinner()
    }

    /**
     * sets the attributes of the spinner.
     */
    private fun setCategoriesSpinner() {

        val categoryNames = ArrayList<String>()
        var categoryPosition: Int = 0

        for (position in 0 until categories.size) {
            categoryNames.add(categories[position].COLUMN_CATEGORY_CONTENT)

            if (note.COLUMN_CATEGORY_CONTENT.equals(categories[position].COLUMN_CATEGORY_CONTENT))
                categoryPosition = position
        }

        val categoryAdapter = ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, categoryNames)

        spnCategories2.adapter = categoryAdapter
        spnCategories2.setSelection(categoryPosition)
    }

    /**
     * registers the activity to Event Bus
     */
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    /**
     * unregisters the activity to Event Bus
     */
    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

}