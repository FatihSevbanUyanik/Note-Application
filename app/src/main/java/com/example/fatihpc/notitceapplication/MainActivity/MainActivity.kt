package com.example.fatihpc.notitceapplication.MainActivity

import android.app.AlertDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SimpleCursorAdapter
import android.widget.Spinner
import com.example.fatihpc.notitceapplication.CategoryActivity.Category
import com.example.fatihpc.notitceapplication.CategoryActivity.CategoryActivity
import com.example.fatihpc.notitceapplication.CategoryActivity.MyCategoryIntentService
import com.example.fatihpc.notitceapplication.DataEvent
import com.example.fatihpc.notitceapplication.NoteActivity.Note
import com.example.fatihpc.notitceapplication.R
import com.example.fatihpc.notitceapplication.SQLDatabase.DatabaseContract
import kotlinx.android.synthetic.main.activity_main.*
import com.example.fatihpc.notitceapplication.SQLDatabase.DatabaseContract.*
import com.example.fatihpc.notitceapplication.SQLDatabase.MyAsyncQueryHandler
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity() {

    // properties
    lateinit var notesRecViewAdapter: NotesRecViewAdapter

    /**
     * creates the initial layout.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setToolbar()
        setRecyclerView()
        sendNoteUpdateRequest(null)
        sendUpdateRequest()
    }

    /**
     * sets the toolbar.
     */
    private fun setToolbar() {
        setSupportActionBar(toolbar)
    }

    /**
     * sets the recycler view and layout manager.
     */
    private fun setRecyclerView() {
        // setting recycler view adapter.
        notesRecViewAdapter = NotesRecViewAdapter(this, ArrayList<Note>())
        notesRecViewAdapter.setHasStableIds(true)
        rcvNotes.adapter = notesRecViewAdapter

        // seting toch helper.
        val callback = NoteTouchCallback(this)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(rcvNotes)

        // setting layout manager.
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rcvNotes.layoutManager = linearLayoutManager
    }

    /**
     * creates menu on the layout.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * adds functionality to each menu item.
     * @param menu is the selected menu item.
     * @return the boolean depending on the state.
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item?.itemId == R.id.menu_item_categories) {
            val intent = Intent(this, CategoryActivity::class.java)
            startActivity(intent)
        }

        else if (item?.itemId == R.id.menu_item_remove_all_notes) {
            removeAllNotes()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun removeAllNotes() {

        AlertDialog.Builder(this).
                setIcon(android.R.drawable.stat_notify_error).
                setTitle("Warning!!!").
                setMessage("Are You Sure That You Want To Delete All Your Notes?").
                setPositiveButton("Yes") { p0, p1 ->

                    // removing from database.
                    val myAsyncQueryHandler = MyAsyncQueryHandler(contentResolver)
                    myAsyncQueryHandler.startDelete(0,null, DatabaseContract.NotesEntry.CONTENT_URI, null, null)

                    // removing from the list.
                    notesRecViewAdapter.notes.clear()
                    notesRecViewAdapter.notifyDataSetChanged()

                }.setNegativeButton("No", null).show()
    }

    /**
     * opens add note fragment.
     * @param view is the floating button on the layout.
     */
    fun addNewNote(view: View) {
        AddNoteFragment().show(supportFragmentManager, "ADD NEW NOTE FRAGMENT")
    }

    /**
     * sends update request for the category data in the recycler view.
     */
     fun sendNoteUpdateRequest(categoryName: String?) {
        val intent = Intent(this, MyNoteIntentService::class.java)
        intent.putExtra("categoryName", categoryName)
        startService(intent)
    }

    /**
     * updates the coming data.
     */
    @Subscribe(sticky = true , threadMode = ThreadMode.MAIN)
    fun updateNoteData( event: DataEvent.DataNotes) {
        val notes = event.notes
        notesRecViewAdapter.notes = notes
        notesRecViewAdapter.notifyDataSetChanged()
    }

    /**
     * sends update request for the category data in the recycler view.
     */
    private fun sendUpdateRequest() {
        startService(Intent(this, MyCategoryIntentService::class.java))
    }

    /**
     * updates the coming data.
     */
    @Subscribe(sticky = true , threadMode = ThreadMode.MAIN)
    fun updateCategoryData( event: DataEvent.DataCategories) {
        val categories = event.categories

        val categoryNames = ArrayList<String>()
        categoryNames.add("All Categories")

        for (category in categories!!)
            categoryNames.add(category.COLUMN_CATEGORY_CONTENT)

        setCategoriesSpinner(categoryNames)
    }

    /**
     * sets the attributes of the spinner.
     * @param categoryNames is the names of the categories.
     */
    private fun setCategoriesSpinner(categoryNames: ArrayList<String>) {

        val categoryAdapter = ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, categoryNames)

        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.adapter = categoryAdapter

        // setting listener
        if (spinner.onItemSelectedListener == null) {

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{

                override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, long: Long) {

                    if (position != 0)
                        sendNoteUpdateRequest(spinner.getItemAtPosition(position).toString())
                    else
                        sendNoteUpdateRequest(null)
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }
        }
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

    /*fun updateNote() {
        val contentValues = ContentValues()
        contentValues.put(DatabaseContract.NotesEntry.COLUMN_NOTE_CONTENT, "UPDATED CONTENT")

        val selection = "${DatabaseContract.NotesEntry.ID} = ?"

        val selectionArgs = arrayOf("3")

        contentResolver.update(DatabaseContract.NotesEntry.CONTENT_URI, contentValues, selection, selectionArgs )
    }

    fun addCategory() {

        val contentValues = ContentValues()
        contentValues.put(DatabaseContract.CategoryEntry.COLUMN_CATEGORY, "Math")
        contentResolver.insert(DatabaseContract.CategoryEntry.CONTENT_URI,  contentValues)
    }

    fun addNote() {

        val contentValues = ContentValues()
        contentValues.put(DatabaseContract.NotesEntry.COLUMN_NOTE_CONTENT, "CONTENTTTTTTTT")
        contentValues.put(DatabaseContract.NotesEntry.COLUMN_CREATION_DATE, "4444444454556")
        contentValues.put(DatabaseContract.NotesEntry.COLUMN_IS_EXECUTED, "YESSSSS")
        contentValues.put(DatabaseContract.NotesEntry.COLUMN_END_DATE, "ENDDDDDDDD")
        contentValues.put(DatabaseContract.NotesEntry.COLUMN_CATEGORY_ID, "1")
        contentResolver.insert(DatabaseContract.NotesEntry.CONTENT_URI,  contentValues)
    }

    fun deleteNote() {

        val selection = "${DatabaseContract.NotesEntry.ID} = ?"
        val selectionArgs = arrayOf("1")
        contentResolver.delete(DatabaseContract.NotesEntry.CONTENT_URI, selection, selectionArgs)
    }

    fun readNotes() {

        val cursor = contentResolver.query(DatabaseContract.NotesEntry.CONTENT_URI, null, null, null, null)
        var result = ""

        while (cursor.moveToNext()) {
            for (i in 0 until cursor.columnCount) {
                result += "${cursor.getColumnName(i)}: ${cursor.getString(i)}  "
            }

            result += "\n"
        }

        cursor?.close()
        println(result)
    }*/

    /*fun addNote() {
        val databaseHelper = DatabaseHelper(this)
        val db = databaseHelper.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(NotesEntry.COLUMN_NOTE_CONTENT, "My Content")
        contentValues.put(NotesEntry.COLUMN_CREATION_DATE, "1212121")
        contentValues.put(NotesEntry.COLUMN_END_DATE, "65446465")
        db.insert(NotesEntry.TABLE_NAME, null, contentValues)

        db.close()
    }

    fun readNote() {
        val databaseHelper = DatabaseHelper(this)
        val db = databaseHelper.readableDatabase

        val projection = arrayOf(
                NotesEntry.ID,
                NotesEntry.COLUMN_NOTE_CONTENT,
                NotesEntry.COLUMN_IS_EXECUTED,
                NotesEntry.COLUMN_CREATION_DATE,
                NotesEntry.COLUMN_END_DATE,
                NotesEntry.COLUMN_CATEGORY_ID)

        val selection = NotesEntry.ID + " = ?";
        val selectionArgs = arrayOf("3")

        val cursor = db.query(NotesEntry.TABLE_NAME, null, selection, selectionArgs, null, null, null)
        var result = ""

        while (cursor.moveToNext()) {
            for (i in 0 until cursor.columnCount)
                result += "${cursor.getColumnName(i)}: ${cursor.getString(i)}  "

            result += "\n"
        }

        cursor?.close()
        db.close()

        println(result)

    }

    fun updateNote() {

        val databaseHelper = DatabaseHelper(this)
        val db = databaseHelper.readableDatabase

        val contentValues = ContentValues()
        contentValues.put(NotesEntry.COLUMN_NOTE_CONTENT, "UPDATED")
        db.update(NotesEntry.TABLE_NAME, contentValues, "${NotesEntry.ID} = 3 AND ${NotesEntry.COLUMN_END_DATE} = 65446465", null)

        db.close()
    }


    fun deleteNote() {
        val databaseHelper = DatabaseHelper(this)
        val db = databaseHelper.readableDatabase

        db.delete(NotesEntry.TABLE_NAME, "${NotesEntry.ID} = ?", arrayOf("2"))
        db.close()
    }*/

    /*
   private fun setCategoriesSpinner(spnCategories: Spinner?) {

       val projection = arrayOf(DatabaseContract.CategoryEntry.COLUMN_CATEGORY_CONTENT)

       val categoryCursor = contentResolver?.query(DatabaseContract.CategoryEntry.CONTENT_URI,
               projection, null, null, null)

       val arrayList = ArrayList<String>()
       arrayList.add("All Categories")

       while (categoryCursor?.moveToNext() as Boolean) {

           val index = categoryCursor.getColumnIndex(DatabaseContract.CategoryEntry.COLUMN_CATEGORY_CONTENT)
           val COLUMN_CATEGORY_CONTENT = categoryCursor.getString(index)
           arrayList.add(COLUMN_CATEGORY_CONTENT)
       }

       val categoryAdapter = ArrayAdapter<String>(this,
               android.R.layout.simple_spinner_dropdown_item,arrayList)

       categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

       spnCategories?.adapter = categoryAdapter

       spnCategories?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
           override fun onNothingSelected(p0: AdapterView<*>?) {
           }

           override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
               if (p2 != 0)
                   sendNoteUpdateRequest(spnCategories?.getItemAtPosition(p2).toString())
               else
                   sendNoteUpdateRequest(null)

           }

       }

   }
    */

}
