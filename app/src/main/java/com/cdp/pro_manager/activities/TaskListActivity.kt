package com.cdp.pro_manager.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cdp.pro_manager.R
import com.cdp.pro_manager.adapters.TaskListItemsAdapter
import com.cdp.pro_manager.firebase.FirestoreClass
import com.cdp.pro_manager.models.Board
import com.cdp.pro_manager.models.Card
import com.cdp.pro_manager.models.Task
import com.cdp.pro_manager.utils.Constants

class TaskListActivity : BaseActivity() {

    private lateinit var mBoardDetails : Board
    private lateinit var mBoardDocumentId: String

    override fun onCreate(savedInstanceState: Bundle?) {

        var toolbartaskactivity : Toolbar?=null

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        toolbartaskactivity = findViewById(R.id.toolbar_my_profile_activity)


        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            mBoardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }


        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this,mBoardDocumentId)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK && requestCode == MEMBERS_REQUEST_CODE){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardDetails(this,mBoardDocumentId)

        }else{
            Log.e("Cancelled","Cancelled")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_members ->{
                val intent =Intent(this,MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL,mBoardDetails)
                startActivityForResult(intent, MEMBERS_REQUEST_CODE)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
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

    fun updateTaskList(position:Int, listName:String,model:Task){

        val task = Task(listName,model.createdBy)
        mBoardDetails.taskList[position] = task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size -1)

        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addUpdateTaskList(this,mBoardDetails)

    }
    fun deleteTaskList(position: Int){
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    fun addCardToTaskList(position: Int,cardName: String){

        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size -1)

        val cardAssignUserList: ArrayList<String> = ArrayList()
        cardAssignUserList.add(FirestoreClass().getCurrentUserId())

        val card = Card(cardName, FirestoreClass().getCurrentUserId(), cardAssignUserList)

        val cardsList = mBoardDetails.taskList[position].cards
        cardsList.add(card)

        val task = Task(
            mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].createdBy,
            cardsList
        )

        mBoardDetails.taskList[position] = task

        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addUpdateTaskList(this,mBoardDetails)

    }

    companion object{
        const val MEMBERS_REQUEST_CODE: Int = 13
    }
}