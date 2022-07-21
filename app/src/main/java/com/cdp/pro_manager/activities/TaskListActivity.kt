package com.cdp.pro_manager.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.cdp.pro_manager.R
import com.cdp.pro_manager.firebase.FirestoreClass
import com.cdp.pro_manager.models.Board
import com.cdp.pro_manager.utils.Constants

class TaskListActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        var toolbartaskactivity : Toolbar?=null

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        toolbartaskactivity = findViewById(R.id.toolbar_my_profile_activity)
        var boardDocumentId = ""

        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }


        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this,boardDocumentId)
    }

     fun setupActionBar(title: String){
        val toolbartaskactivity:Toolbar = findViewById(R.id.toolbar_task_list_activity)
        setSupportActionBar(toolbartaskactivity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_white_24dp)
            actionBar.title = title
        }
         toolbartaskactivity.setNavigationOnClickListener{
            onBackPressed()
        }
    }

    fun boardDetails(board: Board){
        hideProgressDialog()
        setupActionBar(board.name)

    }
}