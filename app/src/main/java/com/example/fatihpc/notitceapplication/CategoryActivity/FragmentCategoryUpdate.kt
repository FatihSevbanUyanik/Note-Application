package com.example.fatihpc.notitceapplication.CategoryActivity

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.example.fatihpc.notitceapplication.R
import com.example.fatihpc.notitceapplication.SQLDatabase.DatabaseContract
import kotlinx.android.synthetic.main.fragment_update_category.view.*

class FragmentCategoryUpdate: DialogFragment() {

    // properties
    lateinit var etCategoryName: EditText
    lateinit var categoryActivity: CategoryActivity

    /**
     * creates the required fragment.
     * @param inflater is the layout inflater that inflates the corresponding layout.
     * @param container is the parent layout of the fragment that will be added.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // inflating the required layout
        val view = inflater.inflate(R.layout.fragment_update_category, container, false)

        // initialising properties
        categoryActivity = context as CategoryActivity

        val listPosition = arguments?.getInt("position") as Int
        val categoryName = (context as CategoryActivity).categoriesRecViewAdapter.
                categories[listPosition].COLUMN_CATEGORY_CONTENT

        etCategoryName = view.etCategoryName
        etCategoryName.setText(categoryName)

        // setting on click listeners.
        view.btnUpdateCategory.setOnClickListener { updateCategory() }

        return view
    }

    /**
     * updates category in database and recycler view
     * by directing to Categories Recycler View Adapter.
     */
    private fun updateCategory() {

        val listPosition = arguments?.getInt("position") as Int
        val categoryContent = etCategoryName.text.toString()

        if (categoryContent.isNotEmpty()) {
            categoryActivity.categoriesRecViewAdapter.updateCategory(listPosition, categoryContent)
            dismiss()
        } else {
            Toast.makeText(context, "Category Name Can't be Empty.", Toast.LENGTH_SHORT).show()
        }
    }

}