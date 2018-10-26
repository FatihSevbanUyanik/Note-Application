package com.example.fatihpc.notitceapplication.SQLDatabase

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.fatihpc.notitceapplication.SQLDatabase.DatabaseContract.*

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {

        // Database Attributes
        private val DATABASE_NAME = "MyDatabase.db"
        private val DATABASE_VERSION = 1

        // Database Tables
        private val TABLE_CATEGORIES_CREATE = "CREATE TABLE IF NOT EXISTS ${CategoryEntry.TABLE_NAME} (" +
                "${CategoryEntry.COLUMN_CATEGORY_ID} INTEGER PRIMARY KEY," +
                "${CategoryEntry.COLUMN_CATEGORY_CONTENT} TEXT)"

        private val TABLE_NOTES_CREATE = "CREATE TABLE IF NOT EXISTS ${NotesEntry.TABLE_NAME} (" +
                "${NotesEntry.COLUMN_NOTE_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "${NotesEntry.COLUMN_NOTE_CONTENT} TEXT, " +
                "${NotesEntry.COLUMN_NOTE_CREATION_DATE} INTEGER, " +
                "${NotesEntry.COLUMN_NOTE_IS_EXECUTED} INTEGER, " +
                "${NotesEntry.COLUMN_NOTE_CATEGORY_ID} INTEGER, " +
                " FOREIGN KEY (${NotesEntry.COLUMN_NOTE_CATEGORY_ID}) REFERENCES " +
                "${CategoryEntry.TABLE_NAME} (${CategoryEntry.COLUMN_CATEGORY_ID}))"
    }

    /**
     * creates the database and creates the required tables.
     * @param sqLiteDatabase is the created database object.
     */
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(TABLE_NOTES_CREATE)
        sqLiteDatabase.execSQL(TABLE_CATEGORIES_CREATE)
    }

    /**
     * enables to link database tables.
     * @param sqLiteDatabase is the configured database object.
     */
    override fun onConfigure(sqLiteDatabase: SQLiteDatabase?) {
        sqLiteDatabase?.setForeignKeyConstraintsEnabled(true)
        super.onConfigure(sqLiteDatabase)
    }

    /**
     * updates the database and removes the required tables.
     * @param sqLiteDatabase is the updated database object.
     */
    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CategoryEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ CategoryEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}