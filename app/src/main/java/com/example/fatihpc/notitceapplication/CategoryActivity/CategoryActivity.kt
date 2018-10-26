package com.example.fatihpc.notitceapplication.CategoryActivity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.example.fatihpc.notitceapplication.DataEvent
import com.example.fatihpc.notitceapplication.MainActivity.MainActivity
import com.example.fatihpc.notitceapplication.R
import kotlinx.android.synthetic.main.activity_category.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class CategoryActivity : AppCompatActivity() {

    // properties
    lateinit var categoriesRecViewAdapter : CategoriesRecViewAdapter

    /**
     * creates the initial layout.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        setToolbar()
        setRecyclerView()
        sendUpdateRequest()
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    /**
     * sets the toolbar.
     */
    private fun setToolbar(){ setSupportActionBar(toolbar) }

    /**
     * sets the recycler view and layout manager.
     */
    private fun setRecyclerView() {
        // setting recycler view adapter.
        val categories = ArrayList<Category>()
        categoriesRecViewAdapter = CategoriesRecViewAdapter(this, categories)
        rcvCategories.adapter = categoriesRecViewAdapter

        // setting layout manager.
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rcvCategories.layoutManager = linearLayoutManager
    }

    /**
     * adds the category to the database by directing to adapter.
     * @param view is the button on the layout.
     */
    fun addCategory(view: View) {
        val newCategoryContent = etNewCategory.text.toString()

        if (newCategoryContent.isNotEmpty())
            categoriesRecViewAdapter.addCategory(newCategoryContent)
        else
            Toast.makeText(this, "Please Type a Category Name.", Toast.LENGTH_SHORT).show()
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
    fun updateCategoriesData( event: DataEvent.DataCategories) {
        val categories = event.categories
        categoriesRecViewAdapter.categories = categories
        categoriesRecViewAdapter.notifyDataSetChanged()
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