package com.cdp.pro_manager.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cdp.pro_manager.R
import com.cdp.pro_manager.adapters.TaskListItemsAdapter
import com.cdp.pro_manager.firebase.FirestoreClass
import com.cdp.pro_manager.models.Board
import com.cdp.pro_manager.models.Task
import com.cdp.pro_manager.utils.Constants

class TaskListActivity : BaseActivity() {

    private lateinit var mBoardDetails : Board

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

     private fun setupActionBar(){
        val toolbartaskactivity:Toolbar = findViewById(R.id.toolbar_task_list_activity)
        setSupportActionBar(toolbartaskactivity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_white_24dp)
            actionBar.title = mBoardDetails.name
        }
         toolbartaskactivity.setNavigationOnClickListener{
            onBackPressed()
        }
    }

    fun boardDetails(board: Board){

        mBoardDetails = board

        var rvTaskList:RecyclerView = findViewById(R.id.rv_task_list)
        hideProgressDialog()
        setupActionBar()

        val addTaskList = Task(resources.getString(R.string.add_list))
        board.taskList.add(addTaskList)

        rvTaskList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        rvTaskList.setHasFixedSize(true)

        val adapter = TaskListItemsAdapter(this,board.taskList)
        rvTaskList.adapter = adapter


    }

    fun addUpdateTaskListSuccess(){

        hideProgressDialog()
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this, mBoardDetails.documentId)
    }

    fun createTaskList(taskListName:String){
        val task = Task(taskListName,FirestoreClass().getCurrentUserId())
        mBoardDetails.taskList.add(0,task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }


}