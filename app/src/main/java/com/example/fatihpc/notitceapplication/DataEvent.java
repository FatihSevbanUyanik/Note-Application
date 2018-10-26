package com.example.fatihpc.notitceapplication;

import com.example.fatihpc.notitceapplication.CategoryActivity.Category;
import com.example.fatihpc.notitceapplication.NoteActivity.Note;

import java.util.ArrayList;

public class DataEvent {

    /**
     * provides the transportation of the uploaded
     * categories from service to category activity.
     */
    public static class DataCategories {

        // properties
        private ArrayList<Category> categories;

        // constructor
        public DataCategories(ArrayList<Category> categories) {
            this.categories = categories;
        }

        public DataCategories() {}

        // methods
        public ArrayList<Category> getCategories() {
            return categories;
        }

        public void setCategories(ArrayList<Category> categories) {
            this.categories = categories;
        }

    }

    /**
     * provides the transportation of the uploaded
     * notes from service to main activity.
     */
    public static class DataNotes {

        // properties
        private ArrayList<Note> notes;

        // constructor
        public DataNotes(ArrayList<Note> notes) {
            this.notes = notes;
        }

        public DataNotes() {
        }

        // methods
        public ArrayList<Note> getNotes() {
            return notes;
        }

        public void setNotes(ArrayList<Note> notes) {
            this.notes = notes;
        }
    }

}