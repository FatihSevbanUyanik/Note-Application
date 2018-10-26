package com.example.fatihpc.notitceapplication.SQLDatabase;

import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseContract {

    // Authority
    public static final String CONTENT_AUTHORITY = "com.example.fatihpc.notitceapplication.SQLDatabase.DataProvider";

    // Base Content
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Paths
    public static final String PATH_CATEGORIES = CategoryEntry.TABLE_NAME;
    public static final String PATH_NOTES = NotesEntry.TABLE_NAME;


    /**
     * Contains the structure of the category object for database.
     */
    public static final class CategoryEntry implements BaseColumns {
        // Table Name
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CATEGORIES);

        // Content Uri
        public static final String TABLE_NAME = "categories";

        // attributes
        public static final String COLUMN_CATEGORY_ID = BaseColumns._ID;
        public static final String COLUMN_CATEGORY_CONTENT = "category";
    }

    /**
     * Contains the structure of the note object for database.
     */
    public static final class NotesEntry implements BaseColumns {
        // Table Name
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_NOTES);

        // Content Uri
        public static final String TABLE_NAME = "notes";

        // attributes
        public static final String COLUMN_NOTE_ID = BaseColumns._ID;
        public static final String COLUMN_NOTE_CONTENT = "noteContent";
        public static final String COLUMN_NOTE_CREATION_DATE = "creationDate";
        public static final String COLUMN_NOTE_IS_EXECUTED = "isExecuted";
        public static final String COLUMN_NOTE_CATEGORY_ID = "categoryId";
    }
}