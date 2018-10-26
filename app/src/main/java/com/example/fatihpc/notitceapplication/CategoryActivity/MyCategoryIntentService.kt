package com.example.fatihpc.notitceapplication.CategoryActivity

import android.app.IntentService
import android.content.Intent
import com.example.fatihpc.notitceapplication.DataEvent
import com.example.fatihpc.notitceapplication.SQLDatabase.DatabaseContract
import org.greenrobot.eventbus.EventBus

class MyCategoryIntentService: IntentService("MyCategoryIntentService"){

    override fun onHandleIntent(p0: Intent?) {

        val categories = ArrayList<Category>()

        val cursor = contentResolver.query(DatabaseContract.CategoryEntry.CONTENT_URI,
                null, null, null, null)

        if (cursor != null) {

            while (cursor.moveToNext()) {

                // getting the corresponding indexes.
                val categoryIdIndex = cursor.getColumnIndex(DatabaseContract.CategoryEntry.COLUMN_CATEGORY_ID)
                val categoryNameIndex = cursor.getColumnIndex(DatabaseContract.CategoryEntry.COLUMN_CATEGORY_CONTENT)

                // querying through indexes
                val COLUMN_CATEGORY_ID = cursor.getString(categoryIdIndex)
                val COLUMN_CATEGORY_CONTENT = cursor.getString(categoryNameIndex)

                // adding note
                categories.add(Category(COLUMN_CATEGORY_ID, COLUMN_CATEGORY_CONTENT))
            }
            cursor.close()
        }

        // sending to main activity through EventBus.
        EventBus.getDefault().post(DataEvent.DataCategories(categories))

    }
}