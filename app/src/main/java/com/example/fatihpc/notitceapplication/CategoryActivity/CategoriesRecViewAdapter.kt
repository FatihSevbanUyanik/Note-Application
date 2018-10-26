package com.example.fatihpc.notitceapplication.CategoryActivity

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.content.res.AppCompatResources
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.example.fatihpc.notitceapplication.R
import com.example.fatihpc.notitceapplication.SQLDatabase.DatabaseContract
import com.example.fatihpc.notitceapplication.SQLDatabase.MyAsyncQueryHandler
import kotlinx.android.synthetic.main.category_list_item.view.*

class CategoriesRecViewAdapter(val context: Context, var categories: ArrayList<Category>):
        RecyclerView.Adapter<CategoriesRecViewAdapter.MyViewHolder>() {

    // properties
    private val myAsyncQueryHandler = MyAsyncQueryHandler(context.contentResolver)

    /**
     * determines which layout will be inflated.
     * @param parent is the parent of the each item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesRecViewAdapter.MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.category_list_item, parent,false)
        return MyViewHolder(view)
    }

    /**
     * gets the item count.
     */
    override fun getItemCount(): Int {
        return categories.size
    }

    /**
     * adds functionality and sets each list item.
     * @param holder holds the all required views.
     * @param position is the position of the list item.
     */
    override fun onBindViewHolder(holder: CategoriesRecViewAdapter.MyViewHolder, position: Int) {
        holder.tvCategory.text =categories[position].COLUMN_CATEGORY_CONTENT
        holder.imgDelete.setOnClickListener{ deleteCategory(position) }
        holder.imgUpdateCategory.setOnClickListener{openUpdateCategoryFragment(position)}
    }

    /**
     * opens update fragment and passes the corresponding position.
     */
    private fun openUpdateCategoryFragment(position: Int) {
        val bundle = Bundle()
        bundle.putInt("position", position)

        val fragment = FragmentCategoryUpdate()
        fragment.arguments = bundle

        fragment.show((context as CategoryActivity).supportFragmentManager, "FRAG CATEGORY UPDATE")
    }

    /**
     * deletes the note item in the specified
     * position from the database and the list.
     * @param position is the specified position.
     */
    private fun deleteCategory(position: Int) {

        AlertDialog.Builder(context).
                setTitle("Warning!!!").

                setMessage("After Deleting Category, all of your corresponding notes will be" +
                " deleted. Are You Sure That You Want To Delete Category?").

                setPositiveButton("Yes") { p0, p1 ->

                    // removing from database.
                    val COLUMN_CATEGORY_ID = categories[position].COLUMN_CATEGORY_ID
                    deleteNotesHavingSelectedCategory(COLUMN_CATEGORY_ID)

                    val selection = "${DatabaseContract.CategoryEntry.COLUMN_CATEGORY_ID} = ${COLUMN_CATEGORY_ID}"
                    context.contentResolver.delete(DatabaseContract.CategoryEntry.CONTENT_URI, selection, null)

                    // removing from the list.
                    categories.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, categories.size)

                }.setNegativeButton("No", null).show()
    }

    private fun deleteNotesHavingSelectedCategory(COLUMN_CATEGORY_ID: String) {
        val selection = "${DatabaseContract.NotesEntry.COLUMN_NOTE_CATEGORY_ID} = $COLUMN_CATEGORY_ID"
        context.contentResolver.delete(DatabaseContract.NotesEntry.CONTENT_URI,selection,null)

    }

    /**
     * adds new note to the database and updates the recycler view.
     * @param newCategoryContent is the new category name.
     */
    fun addCategory(newCategoryContent: String) {

        // inserting category to database.
        val contentValues = ContentValues()
        contentValues.put(DatabaseContract.CategoryEntry.COLUMN_CATEGORY_CONTENT, newCategoryContent)
        context.contentResolver.insert(DatabaseContract.CategoryEntry.CONTENT_URI, contentValues)

        // inserting category to the list.
        val lastInsertedCategory = getLastInsertedCategory() as Category
        categories.add(0, lastInsertedCategory)
        notifyItemInserted(0)
        notifyItemRangeChanged(0, categories.size)
    }

    /**
     * gets the last inserted category item from the database.
     * @return the last category object from the database.
     */
    private fun getLastInsertedCategory(): Category? {

        val selection = "${DatabaseContract.CategoryEntry.COLUMN_CATEGORY_ID} = " +
                "(SELECT MAX(${DatabaseContract.CategoryEntry.COLUMN_CATEGORY_ID}) " +
                "FROM ${DatabaseContract.CategoryEntry.TABLE_NAME})"

        val cursor = context.contentResolver.query(DatabaseContract.CategoryEntry.CONTENT_URI,
                null, selection, null, null)

        while (cursor?.moveToNext() as Boolean) {

            val categoryIdIndex = cursor.getColumnIndex(DatabaseContract.CategoryEntry.COLUMN_CATEGORY_ID)
            val categoryNameIndex = cursor.getColumnIndex(DatabaseContract.CategoryEntry.COLUMN_CATEGORY_CONTENT)

            val COLUMN_CATEGORY_ID = cursor.getString(categoryIdIndex)
            val COLUMN_CATEGORY_CONTENT = cursor.getString(categoryNameIndex)

            cursor.close()
            return Category(COLUMN_CATEGORY_ID, COLUMN_CATEGORY_CONTENT)
        }

        cursor.close()
        return null
    }

    /**
     * updates category in database and recycler view.
     * @param listPosition is the position of the object to be updated.
     * @param categoryContent is the new category name.
     */
    fun updateCategory(listPosition: Int, categoryContent: String) {

        val contentValues = ContentValues()
        contentValues.put(DatabaseContract.CategoryEntry.COLUMN_CATEGORY_CONTENT, categoryContent)

        val selection = "${DatabaseContract.CategoryEntry.COLUMN_CATEGORY_ID} = ${categories[listPosition].COLUMN_CATEGORY_ID}"
        myAsyncQueryHandler.startUpdate(1, null, DatabaseContract.CategoryEntry.CONTENT_URI,
                contentValues, selection, null)

        categories[listPosition].COLUMN_CATEGORY_CONTENT = categoryContent
        notifyDataSetChanged()
    }

    /**
     * holds all the required views of each list item.
     */
    inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val listItem = itemView as CardView
        val tvCategory = listItem.tvCategory
        val imgDelete = listItem.imgDelete
        val imgUpdateCategory = listItem.imgUpdateCategory
    }

}