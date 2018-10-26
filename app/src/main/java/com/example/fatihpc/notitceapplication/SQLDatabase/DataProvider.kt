package com.example.fatihpc.notitceapplication.SQLDatabase

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.widget.Toast
import com.example.fatihpc.notitceapplication.SQLDatabase.DatabaseContract.*

class DataProvider: ContentProvider(){

    // properties
    lateinit var sqLiteDatabase: SQLiteDatabase

    companion object {

        // static properties
        val URI_MATCHER: UriMatcher
        val URI_CODE_CATEGORIES = 1
        val URI_CODE_NOTES = 2

        // static constructor
        init {
            URI_MATCHER = UriMatcher(UriMatcher.NO_MATCH)
            URI_MATCHER.addURI(CONTENT_AUTHORITY, PATH_CATEGORIES, URI_CODE_CATEGORIES)
            URI_MATCHER.addURI(CONTENT_AUTHORITY, PATH_NOTES, URI_CODE_NOTES)
        }
    }

    /**
     * creates the database and gets the writable database.
     */
    override fun onCreate(): Boolean {
        val databaseHelper = DatabaseHelper(context)
        sqLiteDatabase = databaseHelper.writableDatabase
        return true
    }

    /**
     * inserts the passed items to the database.
     * @param uri is the passed uri that determines the table of the inserted item.
     * @param contentValues are the values that will be inserted to the database.
     * @return the resulting uri of the recorded object.
     */
    override fun insert(uri: Uri?, contentValues: ContentValues?): Uri? {

        when (URI_MATCHER.match(uri)) {

            // inserting an object to Categories Table.
            URI_CODE_CATEGORIES -> {
                return insertDataToTable(uri, CategoryEntry.TABLE_NAME, contentValues)
            }

            // inserting an object to Notes Table.
            URI_CODE_NOTES -> {
                return insertDataToTable(uri, NotesEntry.TABLE_NAME, contentValues)
            }
        }

        return null
    }

    /**
     * inserts the passed items to the database.
     * @param uri is the passed uri that determines the table of the inserted item.
     * @param contentValues are the values that will be inserted to the database.
     * @return the resulting uri of the recorded object.
     */
    private fun insertDataToTable(uri: Uri?, tableName: String, contentValues: ContentValues?): Uri {
        val long = sqLiteDatabase.insert( tableName, null, contentValues)
        val recordUri = ContentUris.withAppendedId(uri, long)
        //Toast.makeText(context, recordUri.toString(), Toast.LENGTH_SHORT).show()
        return recordUri
    }

    /**
     * queries the demanded data from database.
     * @param uri is the passed uri that determines the table to be queried.
     * @param selection contains the constraints of the demanded data.
     * @param selectionArgs contains the arguments of the selection.
     * @param sortOrder is the data that indicates the ordering of the data.
     * @return the cursor of the required data.
     */
    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {

        when(URI_MATCHER.match(uri)) {

            // querying data from Categories Table.
            URI_CODE_CATEGORIES -> {
                return sqLiteDatabase.query(CategoryEntry.TABLE_NAME, projection, selection, selectionArgs,null, null, sortOrder)
            }

            // querying data from Notes Table.
            URI_CODE_NOTES -> {

                val combineTables="${NotesEntry.TABLE_NAME} inner join ${CategoryEntry.TABLE_NAME} on" +
                        " ${NotesEntry.TABLE_NAME}.${NotesEntry.COLUMN_NOTE_CATEGORY_ID} = " +
                        "${CategoryEntry.TABLE_NAME}.${CategoryEntry.COLUMN_CATEGORY_ID}";

                val builder = SQLiteQueryBuilder()
                builder.tables = combineTables

                return builder.query(sqLiteDatabase, projection, selection, selectionArgs, null, null, sortOrder)
            }
        }

        return null
    }

    /**
     * updates data in database.
     * @param uri is the passed uri that determines the table to be updated.
     * @param contentValues are the values that will be substituted.
     * @param selection contains the constraints of the demanded data.
     * @param selectionArgs contains the arguments of the selection.
     * @return the affected rows of the database.
     */
    override fun update(uri: Uri?, contentValues: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {

        when(URI_MATCHER.match(uri)) {

            // updates data in Notes Table.
            URI_CODE_NOTES -> {
                return updateData(NotesEntry.TABLE_NAME, contentValues, selection, selectionArgs)
            }

            // updates data in categories Table.
            URI_CODE_CATEGORIES -> {
                return updateData(CategoryEntry.TABLE_NAME, contentValues, selection, selectionArgs)
            }
        }

        return 0
    }

    /**
     * updates data in database.
     * @param tableName is the passed uri that determines the table to be updated.
     * @param contentValues are the values that will be substituted.
     * @param selection contains the constraints of the demanded data.
     * @param selectionArgs contains the arguments of the selection.
     * @return the affected rows of the database.
     */
    fun updateData(tableName: String, contentValues: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {

        val rowsAffected = sqLiteDatabase.update(tableName, contentValues, selection, selectionArgs)

        /*if (rowsAffected == 0) {
            Toast.makeText(context, "Error while deleting note.", Toast.LENGTH_SHORT).show()
        } else {
            return rowsAffected
        }*/

        return rowsAffected
    }

    /**
     * deletes data in database.
     * @param uri determines the table of the object to be deleted.
     * @param selection contains the constraints of the data to be deleted.
     * @param selectionArgs contains the arguments of the selection.
     * @return the affected rows of the database.
     */
    override fun delete(uri: Uri?, selection: String?, selectionArgs: Array<out String>?): Int {

        when (URI_MATCHER.match(uri)) {

            // deletes data from notes table
            URI_CODE_NOTES -> {
                return deleteData(NotesEntry.TABLE_NAME, selection, selectionArgs)
            }

            // deletes data from categories table
            URI_CODE_CATEGORIES -> {
                return deleteData(CategoryEntry.TABLE_NAME, selection, selectionArgs)
            }

        }
        return 0
    }

    /**
     * deletes data in database.
     * @param tableName determines the table of the object to be deleted.
     * @param selection contains the constraints of the data to be deleted.
     * @param selectionArgs contains the arguments of the selection.
     * @return the affected rows of the database.
     */
    fun deleteData(tableName: String, selection: String?, selectionArgs: Array<out String>?): Int {

        val rowsAffected = sqLiteDatabase.delete(tableName, selection, selectionArgs)

        /*if (rowsAffected == 0) {
            Toast.makeText(context, "Error while deleting data.", Toast.LENGTH_SHORT).show()
        } else {
            return rowsAffected
        }*/

        return rowsAffected
    }

    override fun getType(p0: Uri?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}